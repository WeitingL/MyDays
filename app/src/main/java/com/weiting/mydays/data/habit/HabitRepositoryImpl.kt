package com.weiting.mydays.data.habit

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.util.UUID

class HabitRepositoryImpl(
    private val dao: HabitDao,
    private val checkInDao: CheckInDao,
    private val syncManager: HabitSyncManager
) : HabitRepository {

    override fun observeHabitsWithStreak(): Flow<List<HabitWithStreak>> =
        combine(dao.observeAll(), checkInDao.observeAll()) { habits, checkIns ->
            val today = localToday()
            val daysByHabit = checkIns.groupBy { it.habitId }
            habits.map { entity ->
                val habit = entity.toDomain()
                val days = daysByHabit[habit.id]?.map { it.epochDay } ?: emptyList()
                when (habit.type) {
                    HabitType.BUILD -> {
                        val completed = days.toSet()
                        HabitWithStreak(
                            habit = habit,
                            streak = buildStreak(completed, today),
                            completedToday = today in completed
                        )
                    }
                    HabitType.QUIT -> HabitWithStreak(
                        habit = habit,
                        streak = quitStreak(days, habit.createdAt.toLocalEpochDay(), today),
                        completedToday = false
                    )
                }
            }
        }

    override suspend fun add(name: String, type: HabitType) {
        val now = System.currentTimeMillis()
        dao.insert(
            HabitEntity(
                id = UUID.randomUUID().toString(),
                name = name,
                type = type,
                createdAt = now,
                updatedAt = now,
                pendingSync = true
            )
        )
        syncManager.schedulePush()
    }

    override suspend fun update(habit: Habit) {
        dao.update(
            habit.copy(updatedAt = System.currentTimeMillis()).toEntity().copy(pendingSync = true)
        )
        syncManager.schedulePush()
    }

    override suspend fun delete(id: String) {
        dao.softDelete(id, System.currentTimeMillis())
        syncManager.schedulePush()
    }

    override suspend fun checkInToday(habitId: String) {
        recordCheckIn(habitId)
    }

    override suspend fun undoTodayCheckIn(habitId: String) {
        checkInDao.softDeleteByHabitAndDay(habitId, localToday(), System.currentTimeMillis())
        syncManager.schedulePush()
    }

    override suspend fun recordRelapse(habitId: String) {
        recordCheckIn(habitId)
    }

    // 同日打卡：已存在（含 tombstone）→ 復活，否則新增，維持 habitId + epochDay 唯一
    private suspend fun recordCheckIn(habitId: String) {
        val today = localToday()
        val now = System.currentTimeMillis()
        val existing = checkInDao.getByHabitAndDay(habitId, today)
        if (existing != null) {
            checkInDao.upsert(existing.copy(deletedAt = null, updatedAt = now, pendingSync = true))
        } else {
            checkInDao.insert(
                CheckInEntity(
                    id = UUID.randomUUID().toString(),
                    habitId = habitId,
                    epochDay = today,
                    createdAt = now,
                    updatedAt = now,
                    pendingSync = true
                )
            )
        }
        syncManager.schedulePush()
    }
}
