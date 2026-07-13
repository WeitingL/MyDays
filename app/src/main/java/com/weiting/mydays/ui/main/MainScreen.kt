package com.weiting.mydays.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.weiting.mydays.ui.component.LiquidGlassNavBarSliding
import com.weiting.mydays.ui.component.NavBarItem
import com.weiting.mydays.ui.favorite.FavoriteScreen
import com.weiting.mydays.ui.home.HomeScreen
import com.weiting.mydays.ui.profile.ProfileScreen
import com.weiting.mydays.ui.search.SearchScreen

private const val TAB_HOME = "tab_home"
private const val TAB_SEARCH = "tab_search"
private const val TAB_FAVORITE = "tab_favorite"
private const val TAB_PROFILE = "tab_profile"

private data class TabDestination(
    val route: String,
    val navBarItem: NavBarItem
)

private val tabDestinations = listOf(
    TabDestination(TAB_HOME, NavBarItem(Icons.Default.Home, "Home")),
    TabDestination(TAB_SEARCH, NavBarItem(Icons.Default.Search, "Search")),
    TabDestination(TAB_FAVORITE, NavBarItem(Icons.Default.Favorite, "Favorite")),
    TabDestination(TAB_PROFILE, NavBarItem(Icons.Default.Person, "Profile"))
)

@Composable
fun MainScreen(
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    val selectedIndex = tabDestinations
        .indexOfFirst { it.route == currentRoute }
        .coerceAtLeast(0)

    Box(modifier = modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = TAB_HOME,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(TAB_HOME) { HomeScreen() }
            composable(TAB_SEARCH) { SearchScreen() }
            composable(TAB_FAVORITE) { FavoriteScreen() }
            composable(TAB_PROFILE) { ProfileScreen(onLogout = onLogout) }
        }

        LiquidGlassNavBarSliding(
            items = tabDestinations.map { it.navBarItem },
            selectedIndex = selectedIndex,
            onItemSelected = { index ->
                navController.navigate(tabDestinations[index].route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 24.dp)
        )
    }
}
