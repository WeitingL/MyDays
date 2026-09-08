package com.weiting.mydays.ui.habit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weiting.mydays.data.habit.Habit
import com.weiting.mydays.data.habit.HabitRepository
import com.weiting.mydays.data.habit.HabitType
import com.weiting.mydays.data.habit.HabitWithStreak
import com.weiting.mydays.ui.habit.record.HabitFlowRecordData
import com.weiting.mydays.ui.habit.widget.HabitHomeWidgetState
import com.weiting.mydays.ui.habit.widget.toHabitItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HabitViewModel(
    private val repository: HabitRepository
) : ViewModel() {

    // 1. 給 Home Widget 用
    val widgetState: StateFlow<HabitHomeWidgetState> = repository.observeHabitsWithStreak()
        .map { habits ->
            HabitHomeWidgetState(
                todayCompleted = habits.count { it.completedToday },
                todayTotal = habits.size,
                recentHabits = habits.take(3).map { it.toHabitItem() }
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HabitHomeWidgetState.Empty)

    // 2. 給 Flow 用（目前先回傳空列表，未來從 CheckInDao 讀取）
    val flowRecords: StateFlow<List<HabitFlowRecordData>> =
        kotlinx.coroutines.flow.flowOf<List<HabitFlowRecordData>>(emptyList())
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // 3. 給 Habit Screen 用（已存在）
    val habits: StateFlow<List<HabitWithStreak>> = repository.observeHabitsWithStreak()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addHabit(name: String, type: HabitType, reminderMinuteOfDay: Int?) {
        viewModelScope.launch { repository.add(name, type, reminderMinuteOfDay) }
    }

    fun updateHabit(habit: Habit) {
        viewModelScope.launch { repository.update(habit) }
    }

    fun deleteHabit(id: String) {
        viewModelScope.launch { repository.delete(id) }
    }

    fun toggleTodayCheckIn(habitId: String, completedToday: Boolean) {
        viewModelScope.launch {
            if (completedToday) repository.undoTodayCheckIn(habitId)
            else repository.checkInToday(habitId)
        }
    }

    fun recordOccurrence(habitId: String) {
        viewModelScope.launch { repository.recordOccurrence(habitId) }
    }
}
