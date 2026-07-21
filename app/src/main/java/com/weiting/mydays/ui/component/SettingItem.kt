package com.weiting.mydays.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

internal val SettingContentColor = Color(0xFF2E2A45)
private val SettingDestructiveColor = Color(0xFFE0475C)

/**
 * Small caps label used above a [SettingGroup] to name the section
 * (e.g. "帳號", "偏好設定").
 */
@Composable
fun SettingSectionHeader(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        color = SettingContentColor.copy(alpha = 0.6f),
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier.padding(horizontal = 20.dp, vertical = 8.dp)
    )
}

/**
 * Frosted card that groups a handful of [SettingItem]s together, iOS
 * Settings-app style. Insert [SettingDivider] between children.
 */
@Composable
fun SettingGroup(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.55f))
            .border(1.dp, Color.White.copy(alpha = 0.7f), RoundedCornerShape(20.dp)),
        content = content
    )
}

/** Thin separator between rows inside a [SettingGroup], indented past the leading icon. */
@Composable
fun SettingDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier = modifier.padding(start = 52.dp),
        color = SettingContentColor.copy(alpha = 0.12f)
    )
}

/**
 * Base row: leading icon, title (+ optional subtitle), and a trailing slot
 * for whatever the row needs (chevron, switch, value text, ...).
 */
@Composable
fun SettingItem(
    icon: ImageVector,
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    iconTint: Color = SettingContentColor,
    titleColor: Color = SettingContentColor,
    onClick: (() -> Unit)? = null,
    trailing: @Composable () -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressAlpha by animateFloatAsState(if (isPressed) 0.6f else 1f, label = "settingItemPress")

    Row(
        modifier = modifier
            .fillMaxWidth()
            .let { base ->
                if (onClick != null) {
                    base.clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
                } else {
                    base
                }
            }
            .graphicsLayer(alpha = pressAlpha)
            .heightIn(min = 56.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = iconTint,
            modifier = Modifier.size(22.dp)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 14.dp)
        ) {
            Text(text = title, color = titleColor, fontSize = 16.sp)
            if (subtitle != null) {
                Text(text = subtitle, color = titleColor.copy(alpha = 0.6f), fontSize = 13.sp)
            }
        }

        trailing()
    }
}

/** [SettingItem] with a trailing [Switch] for on/off preferences. */
@Composable
fun SettingSwitchItem(
    icon: ImageVector,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null
) {
    SettingItem(
        icon = icon,
        title = title,
        subtitle = subtitle,
        modifier = modifier,
        trailing = { Switch(checked = checked, onCheckedChange = onCheckedChange) }
    )
}

/** [SettingItem] that navigates to a sub-page, with an optional trailing value and a chevron. */
@Composable
fun SettingNavigationItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    value: String? = null
) {
    SettingItem(
        icon = icon,
        title = title,
        subtitle = subtitle,
        onClick = onClick,
        modifier = modifier,
        trailing = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (value != null) {
                    Text(
                        text = value,
                        color = SettingContentColor.copy(alpha = 0.6f),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(end = 6.dp)
                    )
                }
                Text(text = "›", color = SettingContentColor.copy(alpha = 0.4f), fontSize = 18.sp)
            }
        }
    )
}

/** [SettingItem] for a one-tap action row, e.g. logout, with an optional destructive (red) style. */
@Composable
fun SettingActionItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    destructive: Boolean = false
) {
    val color = if (destructive) SettingDestructiveColor else SettingContentColor
    SettingItem(
        icon = icon,
        title = title,
        iconTint = color,
        titleColor = color,
        onClick = onClick,
        modifier = modifier
    )
}

@Preview(name = "Setting items", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun SettingItemGalleryPreview() {
    var notificationsEnabled by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavBarBackground.Lavender.brush)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 32.dp, bottom = 32.dp)
    ) {
        SettingSectionHeader("帳號")
        SettingGroup {
            SettingNavigationItem(
                icon = Icons.Default.Person,
                title = "個人資料",
                onClick = {}
            )
            SettingDivider()
            SettingNavigationItem(
                icon = Icons.Default.Lock,
                title = "密碼與安全性",
                value = "已啟用",
                onClick = {}
            )
        }

        Column(modifier = Modifier.padding(top = 20.dp)) {
            SettingSectionHeader("偏好設定")
            SettingGroup {
                SettingSwitchItem(
                    icon = Icons.Default.Notifications,
                    title = "推播通知",
                    checked = notificationsEnabled,
                    onCheckedChange = { notificationsEnabled = it }
                )
                SettingDivider()
                SettingItem(
                    icon = Icons.Default.Favorite,
                    title = "純文字列",
                    subtitle = "沒有 trailing，用來比對高度"
                )
            }
        }

        Column(modifier = Modifier.padding(top = 20.dp)) {
            SettingGroup {
                SettingActionItem(
                    icon = Icons.Default.ExitToApp,
                    title = "登出",
                    onClick = {},
                    destructive = true
                )
            }
        }
    }
}
