package com.weiting.mydays.data.habit

import kotlinx.coroutines.flow.Flow

interface HabitRepository {
    fun observeHabitsWithStreak(): Flow<List<HabitWithStreak>>
    suspend fun add(name: String, type: HabitType, reminderMinuteOfDay: Int?)
    suspend fun update(habit: Habit)
    suspend fun delete(id: String)
    suspend fun checkInToday(habitId: String)
    suspend fun undoTodayCheckIn(habitId: String)
    suspend fun recordOccurrence(habitId: String)
}
