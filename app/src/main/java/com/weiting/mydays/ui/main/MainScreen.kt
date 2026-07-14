package com.weiting.mydays.ui.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.weiting.mydays.ui.component.MainScaffold
import com.weiting.mydays.ui.component.NavBarBackground
import com.weiting.mydays.ui.component.NavBarItem
import com.weiting.mydays.ui.favorite.FavoriteScreen
import com.weiting.mydays.ui.home.HomeScreen
import com.weiting.mydays.ui.profile.ProfileScreen
import com.weiting.mydays.ui.search.SearchScreen

private val tabItems = listOf(
    NavBarItem(Icons.Default.Home, "Home"),
    NavBarItem(Icons.Default.Search, "Search"),
    NavBarItem(Icons.Default.Favorite, "Favorite"),
    NavBarItem(Icons.Default.Person, "Profile")
)

private val tabBackgrounds = listOf(
    NavBarBackground.Sunset,
    NavBarBackground.Ocean,
    NavBarBackground.Midnight,
    NavBarBackground.Aurora
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
            1 -> SearchScreen()
            2 -> FavoriteScreen()
            else -> ProfileScreen(onLogout = onLogout)
        }
    }
}
