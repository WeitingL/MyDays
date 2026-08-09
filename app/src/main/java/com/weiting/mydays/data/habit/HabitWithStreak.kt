package com.weiting.mydays.data.habit

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

data class HabitWithStreak(
    val habit: Habit,
    val streak: Int,
    val completedToday: Boolean
)

/** BUILD: consecutive completed days counting back from today (or yesterday if today not yet done). */
fun buildStreak(completedDays: Set<Long>, today: Long): Int {
    val anchor = if (today in completedDays) today else today - 1
    var streak = 0
    var day = anchor
    while (day in completedDays) {
        streak++
        day--
    }
    return streak
}

/** QUIT: clean days since the last occurrence, or since the habit was created if none. */
fun quitStreak(occurrenceDays: List<Long>, createdDay: Long, today: Long): Int {
    val anchor = occurrenceDays.maxOrNull() ?: createdDay
    return (today - anchor).toInt().coerceAtLeast(0)
}

fun Long.toLocalEpochDay(): Long =
    Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate().toEpochDay()

fun localToday(): Long = LocalDate.now().toEpochDay()
