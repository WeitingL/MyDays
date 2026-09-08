package com.weiting.mydays.preview.samples.habit

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weiting.mydays.data.habit.HabitType
import com.weiting.mydays.ui.component.NavBarBackground
import com.weiting.mydays.ui.component.SettingContentColor
import com.weiting.mydays.ui.habit.habitStreakText

/**
 * Home 頁面的習慣 widget 變體
 *
 * Version A - Card Grid: 習慣卡片網格，顯示前 3 個習慣
 * Version B - Progress Ring: 用圓環顯示完成率
 * Version C - Compact List: 緊湊列表，顯示前 5 個習慣
 */

// ============================================================================
// Version A - Card Grid
// ============================================================================

@Composable
fun HomeHabitWidgetCardGrid(
    state: HomeHabitWidgetState,
    actions: HomeHabitWidgetActions,
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
        // Header
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

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                SmallIconButton(
                    icon = Icons.Default.Add,
                    onClick = actions::onCreateHabit
                )
                TextButton(
                    text = "全部",
                    onClick = actions::onViewAll
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Habit cards grid
        state.recentHabits.chunked(2).forEach { rowHabits ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowHabits.forEach { habit ->
                    HabitMiniCard(
                        habit = habit,
                        onCheck = { actions.onQuickCheck(habit.id) },
                        modifier = Modifier.weight(1f)
                    )
                }
                // Fill empty space
                repeat(2 - rowHabits.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun HabitMiniCard(
    habit: HabitItem,
    onCheck: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressAlpha by animateFloatAsState(if (isPressed) 0.6f else 1f, label = "miniCardPress")

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.4f))
            .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .clickable(interactionSource = interactionSource, indication = null, onClick = onCheck)
            .graphicsLayer(alpha = pressAlpha)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = habit.name,
                color = SettingContentColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
            Text(
                text = if (habit.type == HabitType.BUILD) "${habit.streak}天" else "${habit.streak}天無",
                color = SettingContentColor.copy(alpha = 0.5f),
                fontSize = 11.sp
            )
        }

        Icon(
            imageVector = if (habit.completedToday) Icons.Default.CheckCircle else Icons.Outlined.CheckCircle,
            contentDescription = "完成",
            tint = SettingContentColor.copy(alpha = if (habit.completedToday) 1f else 0.3f),
            modifier = Modifier.size(18.dp)
        )
    }
}

// ============================================================================
// Version B - Progress Ring
// ============================================================================

@Composable
fun HomeHabitWidgetProgressRing(
    state: HomeHabitWidgetState,
    actions: HomeHabitWidgetActions,
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
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "今日習慣",
                color = SettingContentColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                SmallIconButton(
                    icon = Icons.Default.Add,
                    onClick = actions::onCreateHabit
                )
                TextButton(
                    text = "全部",
                    onClick = actions::onViewAll
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Progress ring + recent habits
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Progress ring
            Box(
                modifier = Modifier.size(80.dp),
                contentAlignment = Alignment.Center
            ) {
                // Background ring
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            Color.White.copy(alpha = 0.3f),
                            CircleShape
                        )
                )

                // Progress overlay (simplified, no actual arc drawing)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${state.todayCompleted}",
                        color = SettingContentColor,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "/ ${state.todayTotal}",
                        color = SettingContentColor.copy(alpha = 0.5f),
                        fontSize = 12.sp
                    )
                }
            }

            // Recent habits list
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                state.recentHabits.take(3).forEach { habit ->
                    HabitCompactRow(
                        habit = habit,
                        onCheck = { actions.onQuickCheck(habit.id) }
                    )
                }
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
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocalFireDepartment,
                contentDescription = null,
                tint = SettingContentColor.copy(alpha = 0.5f),
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = habit.name,
                color = SettingContentColor,
                fontSize = 13.sp,
                maxLines = 1
            )
        }

        IconButton(
            onClick = onCheck,
            modifier = Modifier.size(28.dp)
        ) {
            Icon(
                imageVector = if (habit.completedToday) Icons.Default.CheckCircle else Icons.Outlined.CheckCircle,
                contentDescription = "完成",
                tint = SettingContentColor.copy(alpha = if (habit.completedToday) 1f else 0.3f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

// ============================================================================
// Version C - Compact List
// ============================================================================

@Composable
fun HomeHabitWidgetCompactList(
    state: HomeHabitWidgetState,
    actions: HomeHabitWidgetActions,
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
        // Header with progress bar
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

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                SmallIconButton(
                    icon = Icons.Default.Add,
                    onClick = actions::onCreateHabit
                )
                TextButton(
                    text = "全部",
                    onClick = actions::onViewAll
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color.White.copy(alpha = 0.3f))
        ) {
            val progress = if (state.todayTotal > 0) {
                state.todayCompleted.toFloat() / state.todayTotal.toFloat()
            } else 0f

            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(SettingContentColor.copy(alpha = 0.7f))
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Habits list
        state.recentHabits.forEach { habit ->
            HabitListRow(
                habit = habit,
                onCheck = { actions.onQuickCheck(habit.id) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun HabitListRow(
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
                color = SettingContentColor.copy(alpha = 0.5f),
                fontSize = 12.sp
            )
        }

        IconButton(
            onClick = onCheck,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = if (habit.completedToday) Icons.Default.CheckCircle else Icons.Outlined.CheckCircle,
                contentDescription = "完成",
                tint = SettingContentColor.copy(alpha = if (habit.completedToday) 1f else 0.3f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// ============================================================================
// Common UI components
// ============================================================================

@Composable
private fun SmallIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressAlpha by animateFloatAsState(if (isPressed) 0.6f else 1f, label = "iconButtonPress")

    Box(
        modifier = modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.5f))
            .border(1.dp, Color.White.copy(alpha = 0.7f), CircleShape)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .graphicsLayer(alpha = pressAlpha),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = SettingContentColor,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun TextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressAlpha by animateFloatAsState(if (isPressed) 0.6f else 1f, label = "textButtonPress")

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(Color.White.copy(alpha = 0.5f))
            .border(1.dp, Color.White.copy(alpha = 0.7f), RoundedCornerShape(50))
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .graphicsLayer(alpha = pressAlpha)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            color = SettingContentColor,
            fontSize = 13.sp
        )
    }
}

// ============================================================================
// Previews
// ============================================================================

@Preview(name = "Home Widget - Card Grid", showBackground = true, widthDp = 360)
@Composable
private fun HomeHabitWidgetCardGridPreview() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(NavBarBackground.Mint.brush)
            .padding(20.dp)
    ) {
        HomeHabitWidgetCardGrid(
            state = HabitMockData.createHomeWidgetState(),
            actions = object : HomeHabitWidgetActions {
                override fun onCreateHabit() {}
                override fun onViewAll() {}
                override fun onQuickCheck(habitId: String) {}
            }
        )
    }
}

@Preview(name = "Home Widget - Progress Ring", showBackground = true, widthDp = 360)
@Composable
private fun HomeHabitWidgetProgressRingPreview() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(NavBarBackground.Mint.brush)
            .padding(20.dp)
    ) {
        HomeHabitWidgetProgressRing(
            state = HabitMockData.createHomeWidgetState(),
            actions = object : HomeHabitWidgetActions {
                override fun onCreateHabit() {}
                override fun onViewAll() {}
                override fun onQuickCheck(habitId: String) {}
            }
        )
    }
}

@Preview(name = "Home Widget - Compact List", showBackground = true, widthDp = 360)
@Composable
private fun HomeHabitWidgetCompactListPreview() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(NavBarBackground.Mint.brush)
            .padding(20.dp)
    ) {
        HomeHabitWidgetCompactList(
            state = HabitMockData.createHomeWidgetState(),
            actions = object : HomeHabitWidgetActions {
                override fun onCreateHabit() {}
                override fun onViewAll() {}
                override fun onQuickCheck(habitId: String) {}
            }
        )
    }
}
