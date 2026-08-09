package com.weiting.mydays.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

/**
 * Frosted glass modal, matching the [SettingGroup] / [GlassTopAppBar] language:
 * a translucent white card with a soft top-down gradient, white hairline border,
 * a title, a content slot, and trailing dismiss / confirm text buttons.
 */
@Composable
fun GlassDialog(
    onDismiss: () -> Unit,
    title: String,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    confirmText: String = "儲存",
    confirmEnabled: Boolean = true,
    dismissText: String = "取消",
    content: @Composable ColumnScope.() -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
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
            Text(
                text = title,
                color = SettingContentColor,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(20.dp))
            content()
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                GlassDialogTextButton(text = dismissText, onClick = onDismiss)
                Spacer(modifier = Modifier.width(8.dp))
                GlassDialogTextButton(
                    text = confirmText,
                    onClick = onConfirm,
                    enabled = confirmEnabled,
                    emphasized = true
                )
            }
        }
    }
}

@Composable
private fun GlassDialogTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    emphasized: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressAlpha by animateFloatAsState(if (isPressed) 0.5f else 1f, label = "glassDialogButtonPress")
    val baseColor = if (enabled) SettingContentColor else SettingContentColor.copy(alpha = 0.3f)

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

/** Single-line glass text field: a translucent rounded pill with a faded placeholder. */
@Composable
fun GlassTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = singleLine,
        textStyle = TextStyle(color = SettingContentColor, fontSize = 16.sp),
        cursorBrush = SolidColor(SettingContentColor),
        modifier = modifier.fillMaxWidth()
    ) { innerTextField ->
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color.White.copy(alpha = 0.6f))
                .border(1.dp, Color.White.copy(alpha = 0.7f), RoundedCornerShape(14.dp))
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    color = SettingContentColor.copy(alpha = 0.4f),
                    fontSize = 16.sp
                )
            }
            innerTextField()
        }
    }
}

/** Selectable glass pill for single/multi choice; filled dark when selected. */
@Composable
fun GlassChoiceChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressAlpha by animateFloatAsState(if (isPressed) 0.6f else 1f, label = "glassChoiceChipPress")

    val background = if (selected) SettingContentColor else Color.White.copy(alpha = 0.5f)
    val contentColor = if (selected) Color.White else SettingContentColor

    Box(
        modifier = modifier
            .graphicsLayer(alpha = pressAlpha)
            .clip(RoundedCornerShape(50))
            .background(background)
            .border(1.dp, Color.White.copy(alpha = 0.7f), RoundedCornerShape(50))
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = contentColor, fontSize = 14.sp)
    }
}
