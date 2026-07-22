package com.weiting.mydays.data.habit

import kotlinx.coroutines.flow.Flow

interface HabitRepository {
    fun observeAll(): Flow<List<Habit>>
    suspend fun add(name: String, type: HabitType)
    suspend fun update(habit: Habit)
    suspend fun delete(id: String)
}
