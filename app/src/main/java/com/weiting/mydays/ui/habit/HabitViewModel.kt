package com.weiting.mydays.ui.habit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weiting.mydays.data.habit.Habit
import com.weiting.mydays.data.habit.HabitRepository
import com.weiting.mydays.data.habit.HabitType
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HabitViewModel(
    private val repository: HabitRepository
) : ViewModel() {

    val habits: StateFlow<List<Habit>> = repository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addHabit(name: String, type: HabitType) {
        viewModelScope.launch { repository.add(name, type) }
    }

    fun updateHabit(habit: Habit) {
        viewModelScope.launch { repository.update(habit) }
    }

    fun deleteHabit(id: String) {
        viewModelScope.launch { repository.delete(id) }
    }
}
