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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weiting.mydays.data.habit.HabitType
import com.weiting.mydays.ui.component.GlassChoiceChip
import com.weiting.mydays.ui.component.NavBarBackground
import com.weiting.mydays.ui.component.SettingContentColor
import com.weiting.mydays.ui.habit.habitStreakText
import com.weiting.mydays.ui.habit.milestoneText

/**
 * 習慣列表頁面變體
 *
 * Version A - Card List: 每個習慣是 Liquid Glass 卡片
 * Version B - Grouped List: 按狀態分組（已完成 / 進行中）
 * Version C - Minimal List: 極簡列表，無卡片背景
 */

// ============================================================================
// Version A - Card List
// ============================================================================

@Composable
fun HabitListCardStyle(
    state: HabitListState,
    actions: HabitListActions,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header with Add button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "我的習慣",
                    color = SettingContentColor,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.55f))
                        .border(1.dp, Color.White.copy(alpha = 0.7f), CircleShape)
                        .clickable(onClick = actions::onCreateHabit),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "新增習慣",
                        tint = SettingContentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Habit cards
        items(state.habits) { habit ->
            HabitCard(
                habit = habit,
                onEdit = { actions.onEditHabit(habit.id) },
                onToggleCheck = { actions.onToggleCheck(habit.id) }
            )
        }
    }
}

@Composable
private fun HabitCard(
    habit: HabitItem,
    onEdit: () -> Unit,
    onToggleCheck: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressAlpha by animateFloatAsState(if (isPressed) 0.6f else 1f, label = "habitCardPress")

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.55f))
            .border(1.dp, Color.White.copy(alpha = 0.7f), RoundedCornerShape(20.dp))
            .clickable(interactionSource = interactionSource, indication = null, onClick = onEdit)
            .graphicsLayer(alpha = pressAlpha)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Leading icon
        Icon(
            imageVector = Icons.Default.LocalFireDepartment,
            contentDescription = null,
            tint = SettingContentColor,
            modifier = Modifier.size(28.dp)
        )

        // Content
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 14.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = habit.name,
                    color = SettingContentColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )

                // Type badge
                Text(
                    text = if (habit.type == HabitType.BUILD) "養成" else "戒除",
                    color = SettingContentColor.copy(alpha = 0.5f),
                    fontSize = 11.sp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.White.copy(alpha = 0.5f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = habitStreakText(habit.type, habit.streak),
                color = SettingContentColor,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            milestoneText(habit.streak)?.let { milestone ->
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = milestone,
                    color = SettingContentColor.copy(alpha = 0.6f),
                    fontSize = 13.sp
                )
            }
        }

        // Trailing check button
        IconButton(
            onClick = onToggleCheck,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = if (habit.completedToday) Icons.Default.CheckCircle else Icons.Outlined.CheckCircle,
                contentDescription = "今日打卡",
                tint = SettingContentColor.copy(alpha = if (habit.completedToday) 1f else 0.3f),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

// ============================================================================
// Version B - Grouped List
// ============================================================================

@Composable
fun HabitListGroupedStyle(
    state: HabitListState,
    actions: HabitListActions,
    modifier: Modifier = Modifier
) {
    val completedHabits = state.habits.filter { it.completedToday }
    val incompleteHabits = state.habits.filter { !it.completedToday }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "我的習慣",
                    color = SettingContentColor,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.55f))
                        .border(1.dp, Color.White.copy(alpha = 0.7f), CircleShape)
                        .clickable(onClick = actions::onCreateHabit),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "新增習慣",
                        tint = SettingContentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Incomplete section
        if (incompleteHabits.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "進行中 (${incompleteHabits.size})",
                    color = SettingContentColor.copy(alpha = 0.6f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            items(incompleteHabits) { habit ->
                HabitGroupedRow(
                    habit = habit,
                    onEdit = { actions.onEditHabit(habit.id) },
                    onToggleCheck = { actions.onToggleCheck(habit.id) }
                )
            }
        }

        // Completed section
        if (completedHabits.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "已完成 (${completedHabits.size})",
                    color = SettingContentColor.copy(alpha = 0.6f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            items(completedHabits) { habit ->
                HabitGroupedRow(
                    habit = habit,
                    onEdit = { actions.onEditHabit(habit.id) },
                    onToggleCheck = { actions.onToggleCheck(habit.id) }
                )
            }
        }
    }
}

@Composable
private fun HabitGroupedRow(
    habit: HabitItem,
    onEdit: () -> Unit,
    onToggleCheck: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressAlpha by animateFloatAsState(if (isPressed) 0.6f else 1f, label = "groupedRowPress")

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.55f))
            .border(1.dp, Color.White.copy(alpha = 0.7f), RoundedCornerShape(16.dp))
            .clickable(interactionSource = interactionSource, indication = null, onClick = onEdit)
            .graphicsLayer(alpha = pressAlpha)
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocalFireDepartment,
                    contentDescription = null,
                    tint = SettingContentColor.copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp)
                )

                Text(
                    text = habit.name,
                    color = SettingContentColor,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            IconButton(
                onClick = onToggleCheck,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = if (habit.completedToday) Icons.Default.CheckCircle else Icons.Outlined.CheckCircle,
                    contentDescription = "今日打卡",
                    tint = SettingContentColor.copy(alpha = if (habit.completedToday) 1f else 0.3f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = habitStreakText(habit.type, habit.streak),
                color = SettingContentColor.copy(alpha = 0.7f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = "·",
                color = SettingContentColor.copy(alpha = 0.3f),
                fontSize = 14.sp
            )

            Text(
                text = if (habit.type == HabitType.BUILD) "養成" else "戒除",
                color = SettingContentColor.copy(alpha = 0.5f),
                fontSize = 12.sp
            )
        }
    }
}

// ============================================================================
// Version C - Minimal List
// ============================================================================

@Composable
fun HabitListMinimalStyle(
    state: HabitListState,
    actions: HabitListActions,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "我的習慣",
                    color = SettingContentColor,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.55f))
                        .border(1.dp, Color.White.copy(alpha = 0.7f), CircleShape)
                        .clickable(onClick = actions::onCreateHabit),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "新增習慣",
                        tint = SettingContentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Habits
        items(state.habits) { habit ->
            HabitMinimalRow(
                habit = habit,
                onEdit = { actions.onEditHabit(habit.id) },
                onToggleCheck = { actions.onToggleCheck(habit.id) }
            )
        }
    }
}

@Composable
private fun HabitMinimalRow(
    habit: HabitItem,
    onEdit: () -> Unit,
    onToggleCheck: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressAlpha by animateFloatAsState(if (isPressed) 0.6f else 1f, label = "minimalRowPress")

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(interactionSource = interactionSource, indication = null, onClick = onEdit)
            .graphicsLayer(alpha = pressAlpha)
            .heightIn(min = 60.dp)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Leading icon
        Icon(
            imageVector = Icons.Default.LocalFireDepartment,
            contentDescription = null,
            tint = SettingContentColor.copy(alpha = 0.5f),
            modifier = Modifier.size(24.dp)
        )

        // Content
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
        ) {
            Text(
                text = habit.name,
                color = SettingContentColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = habitStreakText(habit.type, habit.streak),
                color = SettingContentColor.copy(alpha = 0.6f),
                fontSize = 14.sp
            )
        }

        // Trailing check
        IconButton(
            onClick = onToggleCheck,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = if (habit.completedToday) Icons.Default.CheckCircle else Icons.Outlined.CheckCircle,
                contentDescription = "今日打卡",
                tint = SettingContentColor.copy(alpha = if (habit.completedToday) 1f else 0.3f),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

// ============================================================================
// Previews
// ============================================================================

@Preview(name = "Habit List - Card Style", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun HabitListCardStylePreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NavBarBackground.Mint.brush)
    ) {
        HabitListCardStyle(
            state = HabitMockData.createHabitListState(),
            actions = object : HabitListActions {
                override fun onCreateHabit() {}
                override fun onEditHabit(habitId: String) {}
                override fun onToggleCheck(habitId: String) {}
            }
        )
    }
}

@Preview(name = "Habit List - Grouped Style", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun HabitListGroupedStylePreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NavBarBackground.Mint.brush)
    ) {
        HabitListGroupedStyle(
            state = HabitMockData.createHabitListState(),
            actions = object : HabitListActions {
                override fun onCreateHabit() {}
                override fun onEditHabit(habitId: String) {}
                override fun onToggleCheck(habitId: String) {}
            }
        )
    }
}

@Preview(name = "Habit List - Minimal Style", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun HabitListMinimalStylePreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NavBarBackground.Mint.brush)
    ) {
        HabitListMinimalStyle(
            state = HabitMockData.createHabitListState(),
            actions = object : HabitListActions {
                override fun onCreateHabit() {}
                override fun onEditHabit(habitId: String) {}
                override fun onToggleCheck(habitId: String) {}
            }
        )
    }
}
