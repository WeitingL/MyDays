package com.weiting.mydays.ui.features

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.weiting.mydays.ui.component.SettingGroup
import com.weiting.mydays.ui.component.SettingNavigationItem
import com.weiting.mydays.ui.component.SettingSectionHeader

@Composable
fun FeaturesScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 48.dp, bottom = 32.dp)
    ) {
        SettingSectionHeader("功能")
        SettingGroup {
            SettingNavigationItem(
                icon = Icons.Default.CheckCircle,
                title = "待辦事項",
                onClick = {}
            )
        }
    }
}
