package com.weiting.mydays.ui.features

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Savings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.weiting.mydays.ui.component.FeatureEntry
import com.weiting.mydays.ui.component.FeatureGrid
import com.weiting.mydays.ui.component.SettingSectionHeader

@Composable
fun FeaturesScreen(
    modifier: Modifier = Modifier,
    onOpenTodo: () -> Unit = {},
    onOpenHabit: () -> Unit = {}
) {
    val featureEntries = remember(onOpenTodo, onOpenHabit) {
        listOf(
            FeatureEntry(icon = Icons.Default.CheckCircle, title = "待辦事項", enabled = true, onClick = onOpenTodo),
            FeatureEntry(icon = Icons.Default.FitnessCenter, title = "運動管理", enabled = false),
            FeatureEntry(icon = Icons.Default.Restaurant, title = "飲食", enabled = false),
            FeatureEntry(icon = Icons.Default.Savings, title = "記帳", enabled = false),
            FeatureEntry(icon = Icons.Default.Loop, title = "習慣養成", enabled = true, onClick = onOpenHabit)
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 48.dp, bottom = 32.dp)
    ) {
        SettingSectionHeader("功能")
        FeatureGrid(entries = featureEntries)
    }
}
