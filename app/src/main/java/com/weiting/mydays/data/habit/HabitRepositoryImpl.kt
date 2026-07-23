package com.weiting.mydays.data.habit

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.util.UUID

class HabitRepositoryImpl(
    private val dao: HabitDao,
    private val checkInDao: CheckInDao
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
                updatedAt = now
            )
        )
    }

    override suspend fun update(habit: Habit) {
        dao.update(habit.copy(updatedAt = System.currentTimeMillis()).toEntity())
    }

    override suspend fun delete(id: String) {
        dao.deleteById(id)
    }

    override suspend fun checkInToday(habitId: String) {
        checkInDao.insert(
            CheckInEntity(
                id = UUID.randomUUID().toString(),
                habitId = habitId,
                epochDay = localToday(),
                createdAt = System.currentTimeMillis()
            )
        )
    }

    override suspend fun undoTodayCheckIn(habitId: String) {
        checkInDao.deleteByHabitAndDay(habitId, localToday())
    }

    override suspend fun recordRelapse(habitId: String) {
        checkInDao.insert(
            CheckInEntity(
                id = UUID.randomUUID().toString(),
                habitId = habitId,
                epochDay = localToday(),
                createdAt = System.currentTimeMillis()
            )
        )
    }
}
