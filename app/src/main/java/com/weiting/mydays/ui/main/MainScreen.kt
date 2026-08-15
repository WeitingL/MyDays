package com.weiting.mydays.ui.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.weiting.mydays.ui.component.MainScaffold
import com.weiting.mydays.ui.component.NavBarBackground
import com.weiting.mydays.ui.component.NavBarItem
import com.weiting.mydays.ui.features.FeaturesNavHost
import com.weiting.mydays.ui.flow.FlowScreen
import com.weiting.mydays.ui.home.HomeScreen

private val tabItems = listOf(
    NavBarItem(Icons.Default.Home, "首頁"),
    NavBarItem(Icons.Default.DateRange, "河流"),
    NavBarItem(Icons.Default.Settings, "設定")
)

private val tabBackgrounds = listOf(
    NavBarBackground.Sunrise,
    NavBarBackground.Sky,
    NavBarBackground.Mint
)

@Composable
fun MainScreen(
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedIndex by remember { mutableIntStateOf(0) }
    val settingNavController = rememberNavController()
    val currentBackStackEntry by settingNavController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    // Only show NavBar on main pages (Home / Flow / Setting root)
    val showNavBar = selectedIndex != 2 || currentRoute == "setting"

    MainScaffold(
        items = tabItems,
        backgrounds = tabBackgrounds,
        selectedIndex = selectedIndex,
        onItemSelected = { selectedIndex = it },
        showNavBar = showNavBar,
        modifier = modifier.fillMaxSize()
    ) { index ->
        when (index) {
            0 -> HomeScreen()
            1 -> FlowScreen()
            2 -> FeaturesNavHost(
                navController = settingNavController,
                onLogout = onLogout
            )
            else -> HomeScreen()
        }
    }
}
