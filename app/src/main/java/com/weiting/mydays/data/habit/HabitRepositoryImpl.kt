package com.weiting.mydays.data.habit

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class HabitRepositoryImpl(private val dao: HabitDao) : HabitRepository {

    override fun observeAll(): Flow<List<Habit>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

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
}
