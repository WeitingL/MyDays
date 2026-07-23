package com.weiting.mydays.data.habit

import kotlinx.coroutines.flow.Flow

interface HabitRepository {
    fun observeHabitsWithStreak(): Flow<List<HabitWithStreak>>
    suspend fun add(name: String, type: HabitType)
    suspend fun update(habit: Habit)
    suspend fun delete(id: String)
    suspend fun checkInToday(habitId: String)
    suspend fun undoTodayCheckIn(habitId: String)
    suspend fun recordRelapse(habitId: String)
}
