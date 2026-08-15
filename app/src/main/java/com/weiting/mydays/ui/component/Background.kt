package com.weiting.mydays.ui.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class NavBarBackground(val label: String, val colors: List<Color>) {
    Sunrise(label = "Sunrise", colors = listOf(Color(0xFFFFE5EC), Color(0xFFFFD6E8), Color(0xFFFFF0C2))),
    Sky(label = "Sky", colors = listOf(Color(0xFFE0F7FA), Color(0xFFB3E5FC), Color(0xFF81D4FA))),
    Mint(label = "Mint", colors = listOf(Color(0xFFE8F9F0), Color(0xFFC8F4DE), Color(0xFFA0E9C8)));

    val brush: Brush get() = Brush.linearGradient(colors)
}

private val LightBackgroundContentColor = Color(0xFF2E2A45)

@Composable
private fun BackgroundLabel(label: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(Color.White.copy(alpha = 0.55f))
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Text(text = label, color = LightBackgroundContentColor)
    }
}

/**
 * Big centered icon + label for the active nav item, so each background
 * visibly carries different page content instead of just a color change.
 */
@Composable
private fun NavPageContent(index: Int, modifier: Modifier = Modifier) {
    val item = previewNavItems[index]
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.label,
            tint = LightBackgroundContentColor,
            modifier = Modifier.size(72.dp)
        )
        Text(
            text = item.label,
            color = LightBackgroundContentColor,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 12.dp)
        )
    }
}

/**
 * No page cut at all: the gradient's own color stops animate towards the
 * selected background, so the scene morphs like flowing liquid.
 */
@Composable
fun GradientInterpolationBackgroundPager(modifier: Modifier = Modifier) {
    var selectedIndex by remember { mutableIntStateOf(0) }
    val target = NavBarBackground.entries[selectedIndex]

    val startColor by animateColorAsState(target.colors[0], tween(600), label = "gradientStart")
    val midColor by animateColorAsState(target.colors[1], tween(600), label = "gradientMid")
    val endColor by animateColorAsState(target.colors[2], tween(600), label = "gradientEnd")

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.linearGradient(listOf(startColor, midColor, endColor)))
    ) {
        BackgroundLabel(
            label = target.label,
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 32.dp)
        )

        Crossfade(
            targetState = selectedIndex,
            animationSpec = tween(600),
            label = "gradientPageContentFade",
            modifier = Modifier.matchParentSize()
        ) { index ->
            NavPageContent(index, modifier = Modifier.fillMaxSize())
        }

        LiquidGlassNavBarSliding(
            items = previewNavItems,
            selectedIndex = selectedIndex,
            onItemSelected = { selectedIndex = it },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 32.dp)
        )
    }
}

@Preview(name = "Gradient interpolation", showBackground = true, widthDp = 360, heightDp = 640)
@Composable
private fun GradientInterpolationBackgroundPagerPreview() {
    GradientInterpolationBackgroundPager()
}

/**
 * The outgoing background blurs away while the incoming one sharpens into
 * focus, for a frosted-glass style transition.
 */
@Composable
fun BlurTransitionBackgroundPager(modifier: Modifier = Modifier) {
    var selectedIndex by remember { mutableIntStateOf(0) }

    Box(modifier = modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = selectedIndex,
            transitionSpec = { fadeIn(tween(500)) togetherWith fadeOut(tween(500)) },
            label = "backgroundBlurTransition"
        ) { index ->
            val background = NavBarBackground.entries[index]
            val blurRadius by transition.animateDp(label = "blurRadius") { state ->
                if (state == EnterExitState.Visible) 0.dp else 24.dp
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .blur(blurRadius)
                    .background(background.brush)
            ) {
                BackgroundLabel(
                    label = background.label,
                    modifier = Modifier.align(Alignment.TopCenter).padding(top = 32.dp)
                )

                NavPageContent(index, modifier = Modifier.align(Alignment.Center))
            }
        }

        LiquidGlassNavBarSliding(
            items = previewNavItems,
            selectedIndex = selectedIndex,
            onItemSelected = { selectedIndex = it },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 32.dp)
        )
    }
}

@Preview(name = "Blur transition", showBackground = true, widthDp = 360, heightDp = 640)
@Composable
private fun BlurTransitionBackgroundPagerPreview() {
    BlurTransitionBackgroundPager()
}
