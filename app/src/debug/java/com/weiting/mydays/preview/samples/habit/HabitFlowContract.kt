package com.weiting.mydays.preview.samples.habit

import com.weiting.mydays.data.habit.HabitType

/**
 * Habit Flow Contract
 *
 * 定義習慣功能的 state、actions、events
 * 用於 preview 展示，方便之後轉換到正式 app
 */

/**
 * 習慣項目
 */
data class HabitItem(
    val id: String,
    val name: String,
    val type: HabitType,
    val streak: Int,
    val completedToday: Boolean,
    val reminderEnabled: Boolean,
    val reminderTime: String?
)

/**
 * Home 頁面的習慣 widget state
 */
data class HomeHabitWidgetState(
    val todayCompleted: Int,
    val todayTotal: Int,
    val recentHabits: List<HabitItem>
)

/**
 * Home 頁面的習慣 widget actions
 */
interface HomeHabitWidgetActions {
    fun onCreateHabit()
    fun onViewAll()
    fun onQuickCheck(habitId: String)
}

/**
 * 習慣列表 state
 */
data class HabitListState(
    val habits: List<HabitItem>
)

/**
 * 習慣列表 actions
 */
interface HabitListActions {
    fun onCreateHabit()
    fun onEditHabit(habitId: String)
    fun onToggleCheck(habitId: String)
}

/**
 * 習慣表單 state
 */
data class HabitFormState(
    val name: String = "",
    val type: HabitType = HabitType.BUILD,
    val reminderEnabled: Boolean = false,
    val reminderTime: String? = null,
    val isEditMode: Boolean = false,
    val habitId: String? = null,
    val streak: Int = 0,
    val totalDays: Int = 0
)

/**
 * 習慣表單 actions
 */
interface HabitFormActions {
    fun onNameChanged(name: String)
    fun onTypeSelected(type: HabitType)
    fun onReminderToggled(enabled: Boolean)
    fun onReminderTimeChanged(time: String)
    fun onSave()
    fun onDelete()
    fun onCancel()
}

/**
 * Mock data 產生器
 */
object HabitMockData {
    fun createMockHabits(): List<HabitItem> = listOf(
        HabitItem(
            id = "1",
            name = "晨間運動",
            type = HabitType.BUILD,
            streak = 7,
            completedToday = true,
            reminderEnabled = true,
            reminderTime = "07:00"
        ),
        HabitItem(
            id = "2",
            name = "閱讀 30 分鐘",
            type = HabitType.BUILD,
            streak = 3,
            completedToday = false,
            reminderEnabled = true,
            reminderTime = "21:00"
        ),
        HabitItem(
            id = "3",
            name = "戒糖飲",
            type = HabitType.QUIT,
            streak = 14,
            completedToday = true,
            reminderEnabled = false,
            reminderTime = null
        ),
        HabitItem(
            id = "4",
            name = "冥想 10 分鐘",
            type = HabitType.BUILD,
            streak = 30,
            completedToday = true,
            reminderEnabled = true,
            reminderTime = "20:00"
        ),
        HabitItem(
            id = "5",
            name = "戒宵夜",
            type = HabitType.QUIT,
            streak = 5,
            completedToday = false,
            reminderEnabled = false,
            reminderTime = null
        )
    )

    fun createHomeWidgetState(): HomeHabitWidgetState {
        val habits = createMockHabits()
        return HomeHabitWidgetState(
            todayCompleted = habits.count { it.completedToday },
            todayTotal = habits.size,
            recentHabits = habits.take(3)
        )
    }

    fun createHabitListState(): HabitListState {
        return HabitListState(habits = createMockHabits())
    }

    fun createNewFormState(): HabitFormState {
        return HabitFormState(
            name = "",
            type = HabitType.BUILD,
            reminderEnabled = false,
            reminderTime = null,
            isEditMode = false,
            habitId = null
        )
    }

    fun createEditFormState(): HabitFormState {
        val habit = createMockHabits().first()
        return HabitFormState(
            name = habit.name,
            type = habit.type,
            reminderEnabled = habit.reminderEnabled,
            reminderTime = habit.reminderTime,
            isEditMode = true,
            habitId = habit.id,
            streak = habit.streak,
            totalDays = 15
        )
    }
}
