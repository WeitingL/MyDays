package com.weiting.mydays.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
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

/**
 * Base screen composable that provides:
 * - Status bar padding
 * - Optional Liquid Glass AppBar with title, back button, and action buttons
 * - Flexible content slot
 *
 * @param title AppBar title. If null, AppBar is hidden.
 * @param showBackButton Whether to show back button on AppBar.
 * @param onBackClick Callback when back button is clicked.
 * @param actions Right-side action buttons in AppBar.
 * @param modifier Modifier for the root Box.
 * @param content Content lambda that receives PaddingValues.
 */
@Composable
fun BaseScreen(
    title: String? = null,
    showBackButton: Boolean = false,
    onBackClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    modifier: Modifier = Modifier,
    content: @Composable (PaddingValues) -> Unit
) {
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val topPadding = if (title != null) {
        statusBarHeight + GlassTopAppBarZoneHeight
    } else {
        statusBarHeight
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Content with padding
        content(PaddingValues(top = topPadding))

        // AppBar overlay (if title is provided)
        if (title != null) {
            LiquidGlassAppBar(
                title = title,
                showBackButton = showBackButton,
                onBackClick = onBackClick,
                actions = actions
            )
        }
    }
}

/**
 * Liquid Glass style AppBar with:
 * - White gradient background (opaque at top, fading to transparent)
 * - Optional back button
 * - Title
 * - Optional action buttons
 */
@Composable
private fun LiquidGlassAppBar(
    title: String,
    showBackButton: Boolean,
    onBackClick: (() -> Unit)?,
    actions: @Composable RowScope.() -> Unit,
    modifier: Modifier = Modifier
) {
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(statusBarHeight + GlassTopAppBarZoneHeight)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.85f),
                        Color.White.copy(alpha = 0.5f),
                        Color.White.copy(alpha = 0f)
                    )
                )
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = statusBarHeight)
                .height(GlassTopAppBarHeight)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back button (if enabled)
            if (showBackButton && onBackClick != null) {
                BackButton(onClick = onBackClick)
            }

            // Title
            Text(
                text = title,
                color = SettingContentColor,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = if (showBackButton) 4.dp else 0.dp)
            )

            // Actions
            Row(content = actions)
        }
    }
}

/**
 * Glass-style back button with press feedback.
 */
@Composable
private fun BackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressAlpha by animateFloatAsState(
        targetValue = if (isPressed) 0.6f else 1f,
        label = "backButtonPressAlpha"
    )

    Box(
        modifier = modifier
            .size(40.dp)
            .clip(RoundedCornerShape(50))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .graphicsLayer(alpha = pressAlpha),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "返回",
            tint = SettingContentColor
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Previews
// ─────────────────────────────────────────────────────────────────────────────

@Preview(name = "BaseScreen - with AppBar", showBackground = true, widthDp = 360, heightDp = 640)
@Composable
private fun BaseScreenWithAppBarPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF6D5BFF), Color(0xFFFF6FA8))
                )
            )
    ) {
        BaseScreen(
            title = "首頁"
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                items(10) { index ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .padding(vertical = 8.dp)
                            .background(
                                Color.White.copy(alpha = 0.3f),
                                RoundedCornerShape(12.dp)
                            )
                    ) {
                        Text(
                            text = "Item $index",
                            color = Color.White,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(name = "BaseScreen - with back button", showBackground = true, widthDp = 360, heightDp = 640)
@Composable
private fun BaseScreenWithBackButtonPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFFFF6FA8), Color(0xFFFFC371))
                )
            )
    ) {
        BaseScreen(
            title = "習慣養成",
            showBackButton = true,
            onBackClick = { /* navigate back */ },
            actions = {
                IconButton(onClick = { /* add habit */ }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "新增",
                        tint = SettingContentColor
                    )
                }
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                items(5) { index ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .padding(vertical = 8.dp)
                            .background(
                                Color.White.copy(alpha = 0.3f),
                                RoundedCornerShape(12.dp)
                            )
                    ) {
                        Text(
                            text = "Habit $index",
                            color = Color.White,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(name = "BaseScreen - no AppBar", showBackground = true, widthDp = 360, heightDp = 640)
@Composable
private fun BaseScreenNoAppBarPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF4CAF50), Color(0xFF8BC34A))
                )
            )
    ) {
        BaseScreen(
            title = null  // No AppBar
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Custom content without AppBar\n(only status bar padding)",
                    color = Color.White,
                    fontSize = 18.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}
