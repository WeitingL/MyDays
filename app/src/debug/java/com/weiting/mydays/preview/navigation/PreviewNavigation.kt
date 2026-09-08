package com.weiting.mydays.preview.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Preview Navigation System
 *
 * 簡單的 navigation 系統，用於 preview 展示：
 * - 支援頁面切換
 * - 顯示當前頁面
 * - 支援 back stack（返回上一頁）
 *
 * 使用方式：
 * ```
 * PreviewNavigator(initialScreen = "flow") { currentScreen, navigateTo, onBack ->
 *     when {
 *         currentScreen == "flow" -> FlowScreen(...)
 *         currentScreen.startsWith("detail/") -> RecordDetailScreen(...)
 *     }
 * }
 * ```
 */
@Composable
fun PreviewNavigator(
    initialScreen: String = "flow",
    showDebugBar: Boolean = false,
    content: @Composable (
        currentScreen: String,
        navigateTo: (String) -> Unit,
        onBack: () -> Unit
    ) -> Unit
) {
    var backStack by remember { mutableStateOf(listOf(initialScreen)) }
    val currentScreen = backStack.last()

    val navigateTo: (String) -> Unit = { screen ->
        backStack = backStack + screen
    }

    val onBack: () -> Unit = {
        if (backStack.size > 1) {
            backStack = backStack.dropLast(1)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Debug bar（顯示當前 screen 和 back stack）
        if (showDebugBar) {
            PreviewNavigationDebugBar(
                currentScreen = currentScreen,
                backStackSize = backStack.size,
                onBack = onBack
            )
        }

        // 內容
        content(currentScreen, navigateTo, onBack)
    }
}

/**
 * Debug bar - 顯示當前頁面和 back stack 狀態
 */
@Composable
private fun PreviewNavigationDebugBar(
    currentScreen: String,
    backStackSize: Int,
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF2E2A45))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Back button
        if (backStackSize > 1) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
        }

        // Current screen
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Preview Navigation",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 10.sp
            )
            Text(
                text = currentScreen,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Stack size
        Text(
            text = "Stack: $backStackSize",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 12.sp
        )
    }
}
