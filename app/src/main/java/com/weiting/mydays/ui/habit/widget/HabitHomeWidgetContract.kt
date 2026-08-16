package com.weiting.mydays.ui.habit.widget

import com.weiting.mydays.data.habit.HabitType
import com.weiting.mydays.data.habit.HabitWithStreak

/**
 * Habit Home Widget State
 *
 * 定義 widget 需要的資料
 */
data class HabitHomeWidgetState(
    val todayCompleted: Int,
    val todayTotal: Int,
    val recentHabits: List<HabitItem>
) {
    companion object {
        val Empty = HabitHomeWidgetState(
            todayCompleted = 0,
            todayTotal = 0,
            recentHabits = emptyList()
        )
    }
}

/**
 * Habit Home Widget Actions
 *
 * 定義 widget 的互動
 */
interface HabitHomeWidgetActions {
    fun onViewAll()
    fun onQuickCheck(habitId: String)
}

/**
 * Habit Item for Widget Display
 */
data class HabitItem(
    val id: String,
    val name: String,
    val type: HabitType,
    val streak: Int,
    val completedToday: Boolean
)

/**
 * Extension: Convert HabitWithStreak to HabitItem
 */
fun HabitWithStreak.toHabitItem(): HabitItem {
    return HabitItem(
        id = habit.id,
        name = habit.name,
        type = habit.type,
        streak = streak,
        completedToday = completedToday
    )
}
