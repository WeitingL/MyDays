package com.weiting.mydays.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** One entry on the Features screen: a module the user can open, or a "coming soon" slot. */
data class FeatureEntry(
    val icon: ImageVector,
    val title: String,
    val enabled: Boolean = true,
    val onClick: () -> Unit = {}
)

/** Square card used for a single [FeatureEntry], dimmed with a "尚未開放" caption when disabled. */
@Composable
fun FeatureCard(entry: FeatureEntry, modifier: Modifier = Modifier) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressAlpha by animateFloatAsState(if (isPressed) 0.6f else 1f, label = "featureCardPress")
    val contentColor = if (entry.enabled) SettingContentColor else SettingContentColor.copy(alpha = 0.4f)

    Column(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = if (entry.enabled) 0.55f else 0.3f))
            .border(1.dp, Color.White.copy(alpha = 0.7f), RoundedCornerShape(20.dp))
            .let { base ->
                if (entry.enabled) {
                    base.clickable(interactionSource = interactionSource, indication = null, onClick = entry.onClick)
                } else {
                    base
                }
            }
            .graphicsLayer(alpha = pressAlpha)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = entry.icon,
            contentDescription = entry.title,
            tint = contentColor,
            modifier = Modifier.size(32.dp)
        )
        Text(
            text = entry.title,
            color = contentColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 10.dp)
        )
        if (!entry.enabled) {
            Text(
                text = "尚未開放",
                color = contentColor.copy(alpha = 0.7f),
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

/** Lays out [entries] as a fixed-column grid of [FeatureCard]s (no lazy/scroll of its own). */
@Composable
fun FeatureGrid(entries: List<FeatureEntry>, modifier: Modifier = Modifier, columns: Int = 2) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(14.dp)) {
        entries.chunked(columns).forEach { rowEntries ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                rowEntries.forEach { entry ->
                    FeatureCard(entry = entry, modifier = Modifier.weight(1f))
                }
                repeat(columns - rowEntries.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
