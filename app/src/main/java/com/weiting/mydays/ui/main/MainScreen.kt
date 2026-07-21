package com.weiting.mydays.ui.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.weiting.mydays.ui.component.MainScaffold
import com.weiting.mydays.ui.component.NavBarBackground
import com.weiting.mydays.ui.component.NavBarItem
import com.weiting.mydays.ui.features.FeaturesScreen
import com.weiting.mydays.ui.home.HomeScreen
import com.weiting.mydays.ui.profile.ProfileScreen
import com.weiting.mydays.ui.records.RecordsScreen

private val tabItems = listOf(
    NavBarItem(Icons.Default.Home, "首頁"),
    NavBarItem(Icons.Default.DateRange, "紀錄"),
    NavBarItem(Icons.Default.Menu, "功能"),
    NavBarItem(Icons.Default.Person, "我的")
)

private val tabBackgrounds = listOf(
    NavBarBackground.Sunrise,
    NavBarBackground.Sky,
    NavBarBackground.Mint,
    NavBarBackground.Lavender
)

@Composable
fun MainScreen(
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedIndex by remember { mutableIntStateOf(0) }

    MainScaffold(
        items = tabItems,
        backgrounds = tabBackgrounds,
        selectedIndex = selectedIndex,
        onItemSelected = { selectedIndex = it },
        modifier = modifier.fillMaxSize()
    ) { index ->
        when (index) {
            0 -> HomeScreen()
            1 -> RecordsScreen()
            2 -> FeaturesScreen()
            else -> ProfileScreen(onLogout = onLogout)
        }
    }
}
