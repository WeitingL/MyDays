package com.weiting.mydays.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Height of the back/title row itself, excluding the status bar inset and the fade tail below it. */
val GlassTopAppBarHeight = 56.dp
private val GlassTopAppBarFadeHeight = 32.dp

/** Total space (status bar excluded) a screen should reserve at its top so content starts clear of the bar. */
val GlassTopAppBarZoneHeight = GlassTopAppBarHeight + GlassTopAppBarFadeHeight

/**
 * Edge-to-edge scrim for feature sub-pages: no card/border, just a white
 * gradient that's near-opaque behind the status bar + back/title row and
 * fades to fully transparent below, so scrolled content softly disappears
 * underneath instead of getting a hard cut.
 */
@Composable
fun GlassTopAppBar(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressAlpha by animateFloatAsState(if (isPressed) 0.6f else 1f, label = "appBarBackPress")
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
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(50))
                    .clickable(interactionSource = interactionSource, indication = null, onClick = onBack)
                    .graphicsLayer(alpha = pressAlpha),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "返回",
                    tint = SettingContentColor
                )
            }

            Text(
                text = title,
                color = SettingContentColor,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}
