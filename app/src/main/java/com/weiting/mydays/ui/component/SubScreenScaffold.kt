package com.weiting.mydays.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Shell for a feature's sub-page (e.g. drilling into Todo from the 功能 tab):
 * a [GlassTopAppBar] with back action floats over a scrollable content column
 * that reserves just enough top space to clear the bar itself, letting the
 * bar's fade tail bleed over the first bit of content.
 */
@Composable
fun SubScreenScaffold(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .windowInsetsPadding(WindowInsets.systemBars)
                .padding(horizontal = 20.dp)
                .padding(top = GlassTopAppBarHeight, bottom = 32.dp),
            content = content
        )

        GlassTopAppBar(title = title, onBack = onBack)
    }
}
