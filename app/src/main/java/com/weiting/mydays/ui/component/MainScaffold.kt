package com.weiting.mydays.ui.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.unit.dp

/**
 * Shell for the app's main tab flow: a fixed liquid-glass nav bar floats over
 * a per-tab background that blurs out/in on switch, with the tab's content
 * drawn on top of that same blurring layer.
 */
@Composable
fun MainScaffold(
    items: List<NavBarItem>,
    backgrounds: List<NavBarBackground>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (Int) -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = selectedIndex,
            transitionSpec = { fadeIn(tween(500)) togetherWith fadeOut(tween(500)) },
            label = "mainScaffoldBlurTransition"
        ) { index ->
            val blurRadius by transition.animateDp(label = "mainScaffoldBlur") { state ->
                if (state == EnterExitState.Visible) 0.dp else 24.dp
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .blur(blurRadius)
                    .background(backgrounds[index].brush)
            ) {
                content(index)
            }
        }

        LiquidGlassNavBarSliding(
            items = items,
            selectedIndex = selectedIndex,
            onItemSelected = onItemSelected,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 24.dp)
        )
    }
}
