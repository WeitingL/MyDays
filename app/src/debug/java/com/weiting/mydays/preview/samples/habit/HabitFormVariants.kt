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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.window.Dialog
import com.weiting.mydays.data.habit.HabitType
import com.weiting.mydays.ui.component.GlassChoiceChip
import com.weiting.mydays.ui.component.GlassTextField
import com.weiting.mydays.ui.component.NavBarBackground
import com.weiting.mydays.ui.component.SettingContentColor
import com.weiting.mydays.ui.habit.modeLabel

/**
 * 習慣表單變體（建立/編輯）
 *
 * Version A - Dialog: 浮動 dialog 風格
 * Version B - Full Screen: 全螢幕表單
 * Version C - Bottom Sheet: 底部彈出表單（簡化版，用 Dialog 模擬）
 */

// ============================================================================
// Version A - Dialog Style
// ============================================================================

@Composable
fun HabitFormDialog(
    state: HabitFormState,
    actions: HabitFormActions,
    modifier: Modifier = Modifier
) {
    Dialog(onDismissRequest = actions::onCancel) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.92f),
                            Color.White.copy(alpha = 0.78f)
                        )
                    )
                )
                .border(1.dp, Color.White.copy(alpha = 0.7f), RoundedCornerShape(28.dp))
                .padding(24.dp)
        ) {
            // Title
            Text(
                text = if (state.isEditMode) "編輯習慣" else "新增習慣",
                color = SettingContentColor,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Name field
            GlassTextField(
                value = state.name,
                onValueChange = actions::onNameChanged,
                placeholder = "習慣名稱"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Type selection
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                HabitType.entries.forEach { type ->
                    GlassChoiceChip(
                        text = type.modeLabel(),
                        selected = state.type == type,
                        onClick = { actions.onTypeSelected(type) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Reminder toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "每日提醒",
                    color = SettingContentColor,
                    fontSize = 15.sp
                )
                Switch(
                    checked = state.reminderEnabled,
                    onCheckedChange = actions::onReminderToggled
                )
            }

            if (state.reminderEnabled) {
                Spacer(modifier = Modifier.height(8.dp))

                // Time picker placeholder
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White.copy(alpha = 0.6f))
                        .border(1.dp, Color.White.copy(alpha = 0.7f), RoundedCornerShape(14.dp))
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = state.reminderTime ?: "09:00",
                        color = SettingContentColor,
                        fontSize = 16.sp
                    )
                }
            }

            // Stats (only in edit mode)
            if (state.isEditMode) {
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatItem(label = "連續天數", value = "${state.streak} 天")
                    StatItem(label = "累積天數", value = "${state.totalDays} 天")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = if (state.isEditMode) Arrangement.SpaceBetween else Arrangement.End
            ) {
                if (state.isEditMode) {
                    DialogButton(
                        text = "刪除",
                        onClick = actions::onDelete,
                        destructive = true
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DialogButton(
                        text = "取消",
                        onClick = actions::onCancel
                    )
                    DialogButton(
                        text = "儲存",
                        onClick = actions::onSave,
                        enabled = state.name.isNotBlank(),
                        emphasized = true
                    )
                }
            }
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            color = SettingContentColor,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = SettingContentColor.copy(alpha = 0.6f),
            fontSize = 12.sp
        )
    }
}

@Composable
private fun DialogButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    emphasized: Boolean = false,
    destructive: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressAlpha by animateFloatAsState(if (isPressed) 0.5f else 1f, label = "dialogButtonPress")

    val baseColor = when {
        destructive -> Color(0xFFE0475C)
        enabled -> SettingContentColor
        else -> SettingContentColor.copy(alpha = 0.3f)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .let { base ->
                if (enabled) {
                    base.clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
                } else {
                    base
                }
            }
            .graphicsLayer(alpha = pressAlpha)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            color = baseColor,
            fontSize = 16.sp,
            fontWeight = if (emphasized) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

// ============================================================================
// Version B - Full Screen Style
// ============================================================================

@Composable
fun HabitFormFullScreen(
    state: HabitFormState,
    actions: HabitFormActions,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NavBarBackground.Mint.brush)
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.55f))
                    .border(1.dp, Color.White.copy(alpha = 0.7f), CircleShape)
                    .clickable(onClick = onBack),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "返回",
                    tint = SettingContentColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                text = if (state.isEditMode) "編輯習慣" else "新增習慣",
                color = SettingContentColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.55f))
                    .border(1.dp, Color.White.copy(alpha = 0.7f), CircleShape)
                    .clickable(onClick = actions::onSave, enabled = state.name.isNotBlank())
                    .graphicsLayer(alpha = if (state.name.isNotBlank()) 1f else 0.4f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "存",
                    color = SettingContentColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Stats (only in edit mode)
            if (state.isEditMode) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.55f))
                        .border(1.dp, Color.White.copy(alpha = 0.7f), RoundedCornerShape(20.dp))
                        .padding(vertical = 24.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(label = "連續天數", value = "${state.streak} 天")
                    Box(
                        modifier = Modifier
                            .size(width = 1.dp, height = 40.dp)
                            .background(SettingContentColor.copy(alpha = 0.2f))
                    )
                    StatItem(label = "累積天數", value = "${state.totalDays} 天")
                }
            }

            // Form fields
            FormSection(title = "基本資訊") {
                GlassTextField(
                    value = state.name,
                    onValueChange = actions::onNameChanged,
                    placeholder = "習慣名稱"
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    HabitType.entries.forEach { type ->
                        GlassChoiceChip(
                            text = type.modeLabel(),
                            selected = state.type == type,
                            onClick = { actions.onTypeSelected(type) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            FormSection(title = "提醒設定") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "每日提醒",
                            color = SettingContentColor,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "在指定時間收到通知",
                            color = SettingContentColor.copy(alpha = 0.6f),
                            fontSize = 13.sp
                        )
                    }
                    Switch(
                        checked = state.reminderEnabled,
                        onCheckedChange = actions::onReminderToggled
                    )
                }

                if (state.reminderEnabled) {
                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.White.copy(alpha = 0.6f))
                            .border(1.dp, Color.White.copy(alpha = 0.7f), RoundedCornerShape(14.dp))
                            .padding(horizontal = 14.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = "提醒時間: ${state.reminderTime ?: "09:00"}",
                            color = SettingContentColor,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            // Delete button (only in edit mode)
            if (state.isEditMode) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.55f))
                        .border(1.dp, Color.White.copy(alpha = 0.7f), RoundedCornerShape(20.dp))
                        .clickable(onClick = actions::onDelete)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "刪除",
                            tint = Color(0xFFE0475C),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "刪除習慣",
                            color = Color(0xFFE0475C),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FormSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            color = SettingContentColor.copy(alpha = 0.6f),
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White.copy(alpha = 0.55f))
                .border(1.dp, Color.White.copy(alpha = 0.7f), RoundedCornerShape(20.dp))
                .padding(16.dp)
        ) {
            content()
        }
    }
}

// ============================================================================
// Version C - Bottom Sheet Style (using Dialog)
// ============================================================================

@Composable
fun HabitFormBottomSheet(
    state: HabitFormState,
    actions: HabitFormActions,
    modifier: Modifier = Modifier
) {
    Dialog(onDismissRequest = actions::onCancel) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.95f),
                            Color.White.copy(alpha = 0.85f)
                        )
                    )
                )
                .border(
                    1.dp,
                    Color.White.copy(alpha = 0.7f),
                    RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                )
                .padding(24.dp)
        ) {
            // Handle bar
            Box(
                modifier = Modifier
                    .size(width = 40.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(SettingContentColor.copy(alpha = 0.2f))
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Title with close button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (state.isEditMode) "編輯習慣" else "新增習慣",
                    color = SettingContentColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.5f))
                        .clickable(onClick = actions::onCancel),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "關閉",
                        tint = SettingContentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Form content (compact)
            GlassTextField(
                value = state.name,
                onValueChange = actions::onNameChanged,
                placeholder = "習慣名稱"
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                HabitType.entries.forEach { type ->
                    GlassChoiceChip(
                        text = type.modeLabel(),
                        selected = state.type == type,
                        onClick = { actions.onTypeSelected(type) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "每日提醒",
                    color = SettingContentColor,
                    fontSize = 15.sp
                )
                Switch(
                    checked = state.reminderEnabled,
                    onCheckedChange = actions::onReminderToggled
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(SettingContentColor.copy(alpha = if (state.name.isNotBlank()) 1f else 0.3f))
                    .clickable(onClick = actions::onSave, enabled = state.name.isNotBlank())
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "儲存",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (state.isEditMode) {
                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, Color(0xFFE0475C).copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                        .clickable(onClick = actions::onDelete)
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "刪除習慣",
                        color = Color(0xFFE0475C),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

// ============================================================================
// Previews
// ============================================================================

@Preview(name = "Habit Form - Dialog (Create)", showBackground = true)
@Composable
private fun HabitFormDialogCreatePreview() {
    var state by remember { mutableStateOf(HabitMockData.createNewFormState()) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NavBarBackground.Mint.brush)
    ) {
        HabitFormDialog(
            state = state,
            actions = object : HabitFormActions {
                override fun onNameChanged(name: String) { state = state.copy(name = name) }
                override fun onTypeSelected(type: HabitType) { state = state.copy(type = type) }
                override fun onReminderToggled(enabled: Boolean) { state = state.copy(reminderEnabled = enabled) }
                override fun onReminderTimeChanged(time: String) { state = state.copy(reminderTime = time) }
                override fun onSave() {}
                override fun onDelete() {}
                override fun onCancel() {}
            }
        )
    }
}

@Preview(name = "Habit Form - Dialog (Edit)", showBackground = true)
@Composable
private fun HabitFormDialogEditPreview() {
    var state by remember { mutableStateOf(HabitMockData.createEditFormState()) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NavBarBackground.Mint.brush)
    ) {
        HabitFormDialog(
            state = state,
            actions = object : HabitFormActions {
                override fun onNameChanged(name: String) { state = state.copy(name = name) }
                override fun onTypeSelected(type: HabitType) { state = state.copy(type = type) }
                override fun onReminderToggled(enabled: Boolean) { state = state.copy(reminderEnabled = enabled) }
                override fun onReminderTimeChanged(time: String) { state = state.copy(reminderTime = time) }
                override fun onSave() {}
                override fun onDelete() {}
                override fun onCancel() {}
            }
        )
    }
}

@Preview(name = "Habit Form - Full Screen (Create)", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun HabitFormFullScreenCreatePreview() {
    var state by remember { mutableStateOf(HabitMockData.createNewFormState()) }

    HabitFormFullScreen(
        state = state,
        actions = object : HabitFormActions {
            override fun onNameChanged(name: String) { state = state.copy(name = name) }
            override fun onTypeSelected(type: HabitType) { state = state.copy(type = type) }
            override fun onReminderToggled(enabled: Boolean) { state = state.copy(reminderEnabled = enabled) }
            override fun onReminderTimeChanged(time: String) { state = state.copy(reminderTime = time) }
            override fun onSave() {}
            override fun onDelete() {}
            override fun onCancel() {}
        },
        onBack = {}
    )
}

@Preview(name = "Habit Form - Full Screen (Edit)", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun HabitFormFullScreenEditPreview() {
    var state by remember { mutableStateOf(HabitMockData.createEditFormState()) }

    HabitFormFullScreen(
        state = state,
        actions = object : HabitFormActions {
            override fun onNameChanged(name: String) { state = state.copy(name = name) }
            override fun onTypeSelected(type: HabitType) { state = state.copy(type = type) }
            override fun onReminderToggled(enabled: Boolean) { state = state.copy(reminderEnabled = enabled) }
            override fun onReminderTimeChanged(time: String) { state = state.copy(reminderTime = time) }
            override fun onSave() {}
            override fun onDelete() {}
            override fun onCancel() {}
        },
        onBack = {}
    )
}

@Preview(name = "Habit Form - Bottom Sheet (Create)", showBackground = true)
@Composable
private fun HabitFormBottomSheetCreatePreview() {
    var state by remember { mutableStateOf(HabitMockData.createNewFormState()) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NavBarBackground.Mint.brush)
    ) {
        HabitFormBottomSheet(
            state = state,
            actions = object : HabitFormActions {
                override fun onNameChanged(name: String) { state = state.copy(name = name) }
                override fun onTypeSelected(type: HabitType) { state = state.copy(type = type) }
                override fun onReminderToggled(enabled: Boolean) { state = state.copy(reminderEnabled = enabled) }
                override fun onReminderTimeChanged(time: String) { state = state.copy(reminderTime = time) }
                override fun onSave() {}
                override fun onDelete() {}
                override fun onCancel() {}
            }
        )
    }
}

@Preview(name = "Habit Form - Bottom Sheet (Edit)", showBackground = true)
@Composable
private fun HabitFormBottomSheetEditPreview() {
    var state by remember { mutableStateOf(HabitMockData.createEditFormState()) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NavBarBackground.Mint.brush)
    ) {
        HabitFormBottomSheet(
            state = state,
            actions = object : HabitFormActions {
                override fun onNameChanged(name: String) { state = state.copy(name = name) }
                override fun onTypeSelected(type: HabitType) { state = state.copy(type = type) }
                override fun onReminderToggled(enabled: Boolean) { state = state.copy(reminderEnabled = enabled) }
                override fun onReminderTimeChanged(time: String) { state = state.copy(reminderTime = time) }
                override fun onSave() {}
                override fun onDelete() {}
                override fun onCancel() {}
            }
        )
    }
}
