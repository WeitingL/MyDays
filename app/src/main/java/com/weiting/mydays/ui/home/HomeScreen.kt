package com.weiting.mydays.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.weiting.mydays.ui.component.BaseScreen
import com.weiting.mydays.ui.habit.HabitViewModel
import com.weiting.mydays.ui.habit.widget.HabitHomeWidget
import com.weiting.mydays.ui.habit.widget.HabitHomeWidgetActions
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    habitViewModel: HabitViewModel = koinViewModel()
) {
    val habitWidgetState by habitViewModel.widgetState.collectAsState()

    BaseScreen(
        title = "首頁",
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                HabitHomeWidget(
                    state = habitWidgetState,
                    actions = object : HabitHomeWidgetActions {
                        override fun onViewAll() {
                            // TODO: navigate to HabitScreen
                        }

                        override fun onQuickCheck(habitId: String) {
                            // 從 widgetState 找到對應 habit 的 completedToday 狀態
                            val habit = habitWidgetState.recentHabits.find { it.id == habitId }
                            if (habit != null) {
                                habitViewModel.toggleTodayCheckIn(habitId, habit.completedToday)
                            }
                        }
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                // 未來加其他 feature widgets
            }
        }
    }
}
