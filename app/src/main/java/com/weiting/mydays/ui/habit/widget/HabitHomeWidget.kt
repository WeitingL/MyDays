package com.weiting.mydays.ui.habit.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weiting.mydays.data.habit.HabitType
import com.weiting.mydays.ui.component.GlassChoiceChip
import com.weiting.mydays.ui.component.SettingContentColor
import com.weiting.mydays.ui.habit.habitStreakText

@Composable
fun HabitHomeWidget(
    state: HabitHomeWidgetState,
    actions: HabitHomeWidgetActions,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.55f))
            .border(1.dp, Color.White.copy(alpha = 0.7f), RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        // Header: 標題 + 摘要 + 查看全部按鈕
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "今日習慣",
                    color = SettingContentColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${state.todayCompleted}/${state.todayTotal} 完成",
                    color = SettingContentColor.copy(alpha = 0.6f),
                    fontSize = 13.sp
                )
            }
            TextButton(onClick = actions::onViewAll) {
                Text(
                    text = "全部",
                    color = SettingContentColor,
                    fontSize = 14.sp
                )
            }
        }

        if (state.recentHabits.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))

            // Content: 最近習慣列表
            state.recentHabits.forEach { habit ->
                HabitCompactRow(
                    habit = habit,
                    onCheck = { actions.onQuickCheck(habit.id) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun HabitCompactRow(
    habit: HabitItem,
    onCheck: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = habit.name,
                color = SettingContentColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = habitStreakText(habit.type, habit.streak),
                color = SettingContentColor.copy(alpha = 0.6f),
                fontSize = 13.sp
            )
        }

        // Quick action: 打卡按鈕（BUILD）或記錄按鈕（QUIT）
        when (habit.type) {
            HabitType.BUILD -> IconButton(onClick = onCheck) {
                Icon(
                    imageVector = if (habit.completedToday) Icons.Default.CheckCircle
                    else Icons.Outlined.CheckCircle,
                    contentDescription = "打卡",
                    tint = SettingContentColor
                )
            }
            HabitType.QUIT -> GlassChoiceChip(
                text = "記錄",
                selected = false,
                onClick = onCheck
            )
        }
    }
}
