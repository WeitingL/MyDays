package com.weiting.mydays.ui.habit

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.weiting.mydays.data.habit.Habit
import com.weiting.mydays.data.habit.HabitType
import com.weiting.mydays.data.habit.HabitWithStreak
import com.weiting.mydays.ui.component.GlassChoiceChip
import com.weiting.mydays.ui.component.GlassDialog
import com.weiting.mydays.ui.component.GlassTextField
import com.weiting.mydays.ui.component.SettingContentColor
import com.weiting.mydays.ui.component.SettingDivider
import com.weiting.mydays.ui.component.SettingGroup
import com.weiting.mydays.ui.component.SettingItem
import com.weiting.mydays.ui.component.SettingSectionHeader
import com.weiting.mydays.ui.component.SubScreenScaffold
import org.koin.androidx.compose.koinViewModel
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.graphics.graphicsLayer

/** 習慣列：leading icon + 三行文字 + trailing 控制 */
@Composable
private fun HabitRow(
    item: HabitWithStreak,
    onEdit: () -> Unit,
    onToggleBuild: () -> Unit,
    onRecordQuit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val habit = item.habit
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressAlpha by animateFloatAsState(if (isPressed) 0.6f else 1f, label = "habitRowPress")

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(interactionSource = interactionSource, indication = null, onClick = onEdit)
            .graphicsLayer(alpha = pressAlpha)
            .heightIn(min = 56.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.LocalFireDepartment,
            contentDescription = habit.name,
            tint = SettingContentColor,
            modifier = Modifier.size(22.dp)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 14.dp)
        ) {
            Text(text = habit.name, color = SettingContentColor, fontSize = 16.sp)
            Text(
                text = habitStreakText(habit.type, item.streak),
                color = SettingContentColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
            milestoneText(item.streak)?.let { milestone ->
                Text(
                    text = milestone,
                    color = SettingContentColor.copy(alpha = 0.6f),
                    fontSize = 14.sp
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (habit.type) {
                HabitType.BUILD -> IconButton(onClick = onToggleBuild) {
                    Icon(
                        imageVector = if (item.completedToday) Icons.Default.CheckCircle
                        else Icons.Outlined.CheckCircle,
                        contentDescription = "今日打卡",
                        tint = SettingContentColor
                    )
                }
                HabitType.QUIT -> GlassChoiceChip(
                    text = "今天發生了",
                    selected = false,
                    onClick = onRecordQuit
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "刪除",
                    tint = SettingContentColor.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
fun HabitScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HabitViewModel = koinViewModel()
) {
    val habits by viewModel.habits.collectAsState()
    var editorTarget by remember { mutableStateOf<HabitEditorState?>(null) }

    SubScreenScaffold(title = "習慣養成", onBack = onBack, modifier = modifier) {
        SettingGroup {
            SettingItem(
                icon = Icons.Default.Add,
                title = "新增習慣",
                onClick = { editorTarget = HabitEditorState() }
            )
        }

        if (habits.isNotEmpty()) {
            Spacer(modifier = Modifier.height(20.dp))
            SettingSectionHeader("我的習慣")
            SettingGroup {
                habits.forEachIndexed { index, item ->
                    HabitRow(
                        item = item,
                        onEdit = { editorTarget = HabitEditorState(item.habit) },
                        onToggleBuild = { viewModel.toggleTodayCheckIn(item.habit.id, item.completedToday) },
                        onRecordQuit = { viewModel.recordOccurrence(item.habit.id) },
                        onDelete = { viewModel.deleteHabit(item.habit.id) }
                    )
                    if (index != habits.lastIndex) {
                        SettingDivider()
                    }
                }
            }
        }
    }

    editorTarget?.let { state ->
        HabitEditorDialog(
            state = state,
            onDismiss = { editorTarget = null },
            onConfirm = { name, type, reminderMinuteOfDay ->
                val existing = state.habit
                if (existing == null) {
                    viewModel.addHabit(name, type, reminderMinuteOfDay)
                } else {
                    viewModel.updateHabit(
                        existing.copy(name = name, type = type, reminderMinuteOfDay = reminderMinuteOfDay)
                    )
                }
                editorTarget = null
            }
        )
    }
}

private data class HabitEditorState(val habit: Habit? = null)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HabitEditorDialog(
    state: HabitEditorState,
    onDismiss: () -> Unit,
    onConfirm: (name: String, type: HabitType, reminderMinuteOfDay: Int?) -> Unit
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf(state.habit?.name.orEmpty()) }
    var type by remember { mutableStateOf(state.habit?.type ?: HabitType.BUILD) }
    var reminderEnabled by remember { mutableStateOf(state.habit?.reminderMinuteOfDay != null) }

    val initialMinute = state.habit?.reminderMinuteOfDay ?: DEFAULT_REMINDER_MINUTE
    val timeState = rememberTimePickerState(
        initialHour = initialMinute / 60,
        initialMinute = initialMinute % 60,
        is24Hour = true
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> reminderEnabled = granted }

    GlassDialog(
        onDismiss = onDismiss,
        title = if (state.habit == null) "新增習慣" else "編輯習慣",
        confirmEnabled = name.isNotBlank(),
        onConfirm = {
            val minute = if (reminderEnabled) timeState.hour * 60 + timeState.minute else null
            onConfirm(name, type, minute)
        }
    ) {
        GlassTextField(
            value = name,
            onValueChange = { name = it },
            placeholder = "習慣名稱"
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            HabitType.entries.forEach { option ->
                GlassChoiceChip(
                    text = option.modeLabel(),
                    selected = type == option,
                    onClick = { type = option }
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "每日提醒", color = SettingContentColor)
            Switch(
                checked = reminderEnabled,
                onCheckedChange = { checked ->
                    if (!checked) {
                        reminderEnabled = false
                    } else if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
                        PackageManager.PERMISSION_GRANTED
                    ) {
                        reminderEnabled = true
                    } else {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            )
        }
        if (reminderEnabled) {
            Spacer(modifier = Modifier.height(8.dp))
            TimeInput(state = timeState)
        }
    }
}

private const val DEFAULT_REMINDER_MINUTE = 9 * 60
