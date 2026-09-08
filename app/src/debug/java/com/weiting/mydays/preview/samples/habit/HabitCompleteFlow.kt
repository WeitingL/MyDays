package com.weiting.mydays.preview.samples.habit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.weiting.mydays.data.habit.HabitType
import com.weiting.mydays.preview.navigation.PreviewNavigator
import com.weiting.mydays.ui.component.NavBarBackground

/**
 * 習慣功能完整流程
 *
 * 使用 PreviewNavigator 串起所有頁面，展示完整的使用流程：
 * 1. Home Widget → 點擊「建立習慣」→ 建立表單
 * 2. Home Widget → 點擊「查看全部」→ 習慣列表
 * 3. 習慣列表 → 點擊習慣 → 編輯表單
 * 4. 表單 → 儲存 → 返回上一頁
 *
 * Navigation routes:
 * - "home" - Home 頁面的習慣 widget
 * - "habit_list" - 習慣列表
 * - "habit_create" - 建立習慣表單
 * - "habit_edit/{id}" - 編輯習慣表單
 */

// ============================================================================
// Complete Flow - Dialog Style Forms
// ============================================================================

@Preview(
    name = "Habit Complete Flow - Dialog Forms",
    showBackground = true,
    widthDp = 360,
    heightDp = 800
)
@Composable
fun HabitCompleteFlowDialogPreview() {
    var habits by remember { mutableStateOf(HabitMockData.createMockHabits()) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf<String?>(null) }

    PreviewNavigator(
        initialScreen = "home",
        showDebugBar = true
    ) { currentScreen, navigateTo, onBack ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(NavBarBackground.Mint.brush)
        ) {
            when {
                currentScreen == "home" -> {
                    // Home page with habit widget
                    Column(modifier = Modifier.padding(20.dp)) {
                        HomeHabitWidgetCompactList(
                            state = HomeHabitWidgetState(
                                todayCompleted = habits.count { it.completedToday },
                                todayTotal = habits.size,
                                recentHabits = habits.take(5)
                            ),
                            actions = object : HomeHabitWidgetActions {
                                override fun onCreateHabit() {
                                    showCreateDialog = true
                                }

                                override fun onViewAll() {
                                    navigateTo("habit_list")
                                }

                                override fun onQuickCheck(habitId: String) {
                                    habits = habits.map {
                                        if (it.id == habitId) {
                                            it.copy(completedToday = !it.completedToday)
                                        } else it
                                    }
                                }
                            }
                        )
                    }
                }

                currentScreen == "habit_list" -> {
                    // Habit list page
                    HabitListCardStyle(
                        state = HabitListState(habits = habits),
                        actions = object : HabitListActions {
                            override fun onCreateHabit() {
                                showCreateDialog = true
                            }

                            override fun onEditHabit(habitId: String) {
                                showEditDialog = habitId
                            }

                            override fun onToggleCheck(habitId: String) {
                                habits = habits.map {
                                    if (it.id == habitId) {
                                        it.copy(completedToday = !it.completedToday)
                                    } else it
                                }
                            }
                        }
                    )
                }
            }

            // Create dialog
            if (showCreateDialog) {
                var formState by remember { mutableStateOf(HabitMockData.createNewFormState()) }

                HabitFormDialog(
                    state = formState,
                    actions = object : HabitFormActions {
                        override fun onNameChanged(name: String) {
                            formState = formState.copy(name = name)
                        }

                        override fun onTypeSelected(type: HabitType) {
                            formState = formState.copy(type = type)
                        }

                        override fun onReminderToggled(enabled: Boolean) {
                            formState = formState.copy(reminderEnabled = enabled)
                        }

                        override fun onReminderTimeChanged(time: String) {
                            formState = formState.copy(reminderTime = time)
                        }

                        override fun onSave() {
                            // Add new habit
                            val newHabit = HabitItem(
                                id = (habits.size + 1).toString(),
                                name = formState.name,
                                type = formState.type,
                                streak = 0,
                                completedToday = false,
                                reminderEnabled = formState.reminderEnabled,
                                reminderTime = formState.reminderTime
                            )
                            habits = habits + newHabit
                            showCreateDialog = false
                        }

                        override fun onDelete() {
                            // Not applicable for create
                        }

                        override fun onCancel() {
                            showCreateDialog = false
                        }
                    }
                )
            }

            // Edit dialog
            showEditDialog?.let { habitId ->
                val habit = habits.find { it.id == habitId }
                if (habit != null) {
                    var formState by remember {
                        mutableStateOf(
                            HabitFormState(
                                name = habit.name,
                                type = habit.type,
                                reminderEnabled = habit.reminderEnabled,
                                reminderTime = habit.reminderTime,
                                isEditMode = true,
                                habitId = habit.id,
                                streak = habit.streak,
                                totalDays = 20
                            )
                        )
                    }

                    HabitFormDialog(
                        state = formState,
                        actions = object : HabitFormActions {
                            override fun onNameChanged(name: String) {
                                formState = formState.copy(name = name)
                            }

                            override fun onTypeSelected(type: HabitType) {
                                formState = formState.copy(type = type)
                            }

                            override fun onReminderToggled(enabled: Boolean) {
                                formState = formState.copy(reminderEnabled = enabled)
                            }

                            override fun onReminderTimeChanged(time: String) {
                                formState = formState.copy(reminderTime = time)
                            }

                            override fun onSave() {
                                // Update habit
                                habits = habits.map {
                                    if (it.id == habitId) {
                                        it.copy(
                                            name = formState.name,
                                            type = formState.type,
                                            reminderEnabled = formState.reminderEnabled,
                                            reminderTime = formState.reminderTime
                                        )
                                    } else it
                                }
                                showEditDialog = null
                            }

                            override fun onDelete() {
                                // Delete habit
                                habits = habits.filter { it.id != habitId }
                                showEditDialog = null
                            }

                            override fun onCancel() {
                                showEditDialog = null
                            }
                        }
                    )
                }
            }
        }
    }
}

// ============================================================================
// Complete Flow - Full Screen Forms
// ============================================================================

@Preview(
    name = "Habit Complete Flow - Full Screen Forms",
    showBackground = true,
    widthDp = 360,
    heightDp = 800
)
@Composable
fun HabitCompleteFlowFullScreenPreview() {
    var habits by remember { mutableStateOf(HabitMockData.createMockHabits()) }

    PreviewNavigator(
        initialScreen = "home",
        showDebugBar = true
    ) { currentScreen, navigateTo, onBack ->
        when {
            currentScreen == "home" -> {
                // Home page with habit widget
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(NavBarBackground.Mint.brush)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        HomeHabitWidgetProgressRing(
                            state = HomeHabitWidgetState(
                                todayCompleted = habits.count { it.completedToday },
                                todayTotal = habits.size,
                                recentHabits = habits.take(3)
                            ),
                            actions = object : HomeHabitWidgetActions {
                                override fun onCreateHabit() {
                                    navigateTo("habit_create")
                                }

                                override fun onViewAll() {
                                    navigateTo("habit_list")
                                }

                                override fun onQuickCheck(habitId: String) {
                                    habits = habits.map {
                                        if (it.id == habitId) {
                                            it.copy(completedToday = !it.completedToday)
                                        } else it
                                    }
                                }
                            }
                        )
                    }
                }
            }

            currentScreen == "habit_list" -> {
                // Habit list page
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(NavBarBackground.Mint.brush)
                ) {
                    HabitListGroupedStyle(
                        state = HabitListState(habits = habits),
                        actions = object : HabitListActions {
                            override fun onCreateHabit() {
                                navigateTo("habit_create")
                            }

                            override fun onEditHabit(habitId: String) {
                                navigateTo("habit_edit/$habitId")
                            }

                            override fun onToggleCheck(habitId: String) {
                                habits = habits.map {
                                    if (it.id == habitId) {
                                        it.copy(completedToday = !it.completedToday)
                                    } else it
                                }
                            }
                        }
                    )
                }
            }

            currentScreen == "habit_create" -> {
                var formState by remember { mutableStateOf(HabitMockData.createNewFormState()) }

                HabitFormFullScreen(
                    state = formState,
                    actions = object : HabitFormActions {
                        override fun onNameChanged(name: String) {
                            formState = formState.copy(name = name)
                        }

                        override fun onTypeSelected(type: HabitType) {
                            formState = formState.copy(type = type)
                        }

                        override fun onReminderToggled(enabled: Boolean) {
                            formState = formState.copy(reminderEnabled = enabled)
                        }

                        override fun onReminderTimeChanged(time: String) {
                            formState = formState.copy(reminderTime = time)
                        }

                        override fun onSave() {
                            val newHabit = HabitItem(
                                id = (habits.size + 1).toString(),
                                name = formState.name,
                                type = formState.type,
                                streak = 0,
                                completedToday = false,
                                reminderEnabled = formState.reminderEnabled,
                                reminderTime = formState.reminderTime
                            )
                            habits = habits + newHabit
                            onBack()
                        }

                        override fun onDelete() {}
                        override fun onCancel() {}
                    },
                    onBack = onBack
                )
            }

            currentScreen.startsWith("habit_edit/") -> {
                val habitId = currentScreen.removePrefix("habit_edit/")
                val habit = habits.find { it.id == habitId }

                if (habit != null) {
                    var formState by remember {
                        mutableStateOf(
                            HabitFormState(
                                name = habit.name,
                                type = habit.type,
                                reminderEnabled = habit.reminderEnabled,
                                reminderTime = habit.reminderTime,
                                isEditMode = true,
                                habitId = habit.id,
                                streak = habit.streak,
                                totalDays = 25
                            )
                        )
                    }

                    HabitFormFullScreen(
                        state = formState,
                        actions = object : HabitFormActions {
                            override fun onNameChanged(name: String) {
                                formState = formState.copy(name = name)
                            }

                            override fun onTypeSelected(type: HabitType) {
                                formState = formState.copy(type = type)
                            }

                            override fun onReminderToggled(enabled: Boolean) {
                                formState = formState.copy(reminderEnabled = enabled)
                            }

                            override fun onReminderTimeChanged(time: String) {
                                formState = formState.copy(reminderTime = time)
                            }

                            override fun onSave() {
                                habits = habits.map {
                                    if (it.id == habitId) {
                                        it.copy(
                                            name = formState.name,
                                            type = formState.type,
                                            reminderEnabled = formState.reminderEnabled,
                                            reminderTime = formState.reminderTime
                                        )
                                    } else it
                                }
                                onBack()
                            }

                            override fun onDelete() {
                                habits = habits.filter { it.id != habitId }
                                onBack()
                            }

                            override fun onCancel() {}
                        },
                        onBack = onBack
                    )
                }
            }
        }
    }
}

// ============================================================================
// Variant Combination - Mix different styles
// ============================================================================

@Preview(
    name = "Habit Flow - Mixed Styles",
    showBackground = true,
    widthDp = 360,
    heightDp = 800
)
@Composable
fun HabitFlowMixedStylesPreview() {
    var habits by remember { mutableStateOf(HabitMockData.createMockHabits()) }
    var showBottomSheet by remember { mutableStateOf<String?>(null) }

    PreviewNavigator(
        initialScreen = "home",
        showDebugBar = true
    ) { currentScreen, navigateTo, onBack ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(NavBarBackground.Mint.brush)
        ) {
            when {
                currentScreen == "home" -> {
                    Column(modifier = Modifier.padding(20.dp)) {
                        HomeHabitWidgetCardGrid(
                            state = HomeHabitWidgetState(
                                todayCompleted = habits.count { it.completedToday },
                                todayTotal = habits.size,
                                recentHabits = habits.take(3)
                            ),
                            actions = object : HomeHabitWidgetActions {
                                override fun onCreateHabit() {
                                    showBottomSheet = "create"
                                }

                                override fun onViewAll() {
                                    navigateTo("habit_list")
                                }

                                override fun onQuickCheck(habitId: String) {
                                    habits = habits.map {
                                        if (it.id == habitId) {
                                            it.copy(completedToday = !it.completedToday)
                                        } else it
                                    }
                                }
                            }
                        )
                    }
                }

                currentScreen == "habit_list" -> {
                    HabitListMinimalStyle(
                        state = HabitListState(habits = habits),
                        actions = object : HabitListActions {
                            override fun onCreateHabit() {
                                showBottomSheet = "create"
                            }

                            override fun onEditHabit(habitId: String) {
                                showBottomSheet = habitId
                            }

                            override fun onToggleCheck(habitId: String) {
                                habits = habits.map {
                                    if (it.id == habitId) {
                                        it.copy(completedToday = !it.completedToday)
                                    } else it
                                }
                            }
                        }
                    )
                }
            }

            // Bottom sheet
            showBottomSheet?.let { target ->
                if (target == "create") {
                    var formState by remember { mutableStateOf(HabitMockData.createNewFormState()) }

                    HabitFormBottomSheet(
                        state = formState,
                        actions = object : HabitFormActions {
                            override fun onNameChanged(name: String) {
                                formState = formState.copy(name = name)
                            }

                            override fun onTypeSelected(type: HabitType) {
                                formState = formState.copy(type = type)
                            }

                            override fun onReminderToggled(enabled: Boolean) {
                                formState = formState.copy(reminderEnabled = enabled)
                            }

                            override fun onReminderTimeChanged(time: String) {
                                formState = formState.copy(reminderTime = time)
                            }

                            override fun onSave() {
                                val newHabit = HabitItem(
                                    id = (habits.size + 1).toString(),
                                    name = formState.name,
                                    type = formState.type,
                                    streak = 0,
                                    completedToday = false,
                                    reminderEnabled = formState.reminderEnabled,
                                    reminderTime = formState.reminderTime
                                )
                                habits = habits + newHabit
                                showBottomSheet = null
                            }

                            override fun onDelete() {}
                            override fun onCancel() {
                                showBottomSheet = null
                            }
                        }
                    )
                } else {
                    val habit = habits.find { it.id == target }
                    if (habit != null) {
                        var formState by remember {
                            mutableStateOf(
                                HabitFormState(
                                    name = habit.name,
                                    type = habit.type,
                                    reminderEnabled = habit.reminderEnabled,
                                    reminderTime = habit.reminderTime,
                                    isEditMode = true,
                                    habitId = habit.id,
                                    streak = habit.streak,
                                    totalDays = 18
                                )
                            )
                        }

                        HabitFormBottomSheet(
                            state = formState,
                            actions = object : HabitFormActions {
                                override fun onNameChanged(name: String) {
                                    formState = formState.copy(name = name)
                                }

                                override fun onTypeSelected(type: HabitType) {
                                    formState = formState.copy(type = type)
                                }

                                override fun onReminderToggled(enabled: Boolean) {
                                    formState = formState.copy(reminderEnabled = enabled)
                                }

                                override fun onReminderTimeChanged(time: String) {
                                    formState = formState.copy(reminderTime = time)
                                }

                                override fun onSave() {
                                    habits = habits.map {
                                        if (it.id == target) {
                                            it.copy(
                                                name = formState.name,
                                                type = formState.type,
                                                reminderEnabled = formState.reminderEnabled,
                                                reminderTime = formState.reminderTime
                                            )
                                        } else it
                                    }
                                    showBottomSheet = null
                                }

                                override fun onDelete() {
                                    habits = habits.filter { it.id != target }
                                    showBottomSheet = null
                                }

                                override fun onCancel() {
                                    showBottomSheet = null
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
