package com.weiting.mydays.ui.habit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.weiting.mydays.data.habit.Habit
import com.weiting.mydays.data.habit.HabitType
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

private fun HabitType.label(): String = when (this) {
    HabitType.BUILD -> "養成好習慣"
    HabitType.QUIT -> "戒除壞習慣"
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
                habits.forEachIndexed { index, habit ->
                    SettingItem(
                        icon = if (habit.type == HabitType.BUILD) Icons.Default.TrendingUp else Icons.Default.Block,
                        title = habit.name,
                        subtitle = habit.type.label(),
                        onClick = { editorTarget = HabitEditorState(habit) },
                        trailing = {
                            IconButton(onClick = { viewModel.deleteHabit(habit.id) }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "刪除",
                                    tint = SettingContentColor.copy(alpha = 0.5f)
                                )
                            }
                        }
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
            onConfirm = { name, type ->
                val existing = state.habit
                if (existing == null) {
                    viewModel.addHabit(name, type)
                } else {
                    viewModel.updateHabit(existing.copy(name = name, type = type))
                }
                editorTarget = null
            }
        )
    }
}

private data class HabitEditorState(val habit: Habit? = null)

@Composable
private fun HabitEditorDialog(
    state: HabitEditorState,
    onDismiss: () -> Unit,
    onConfirm: (name: String, type: HabitType) -> Unit
) {
    var name by remember { mutableStateOf(state.habit?.name.orEmpty()) }
    var type by remember { mutableStateOf(state.habit?.type ?: HabitType.BUILD) }

    GlassDialog(
        onDismiss = onDismiss,
        title = if (state.habit == null) "新增習慣" else "編輯習慣",
        confirmEnabled = name.isNotBlank(),
        onConfirm = { onConfirm(name, type) }
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
                    text = option.label(),
                    selected = type == option,
                    onClick = { type = option }
                )
            }
        }
    }
}
