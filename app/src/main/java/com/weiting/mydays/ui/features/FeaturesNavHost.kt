package com.weiting.mydays.ui.features

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.weiting.mydays.ui.habit.HabitScreen

private const val ROUTE_FEATURES_LIST = "features_list"
internal const val ROUTE_FEATURES_HABIT = "features_habit"

/** Sub-navigation graph for the 功能 tab: the feature grid, drilling into each module. */
@Composable
fun FeaturesNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = ROUTE_FEATURES_LIST,
        modifier = modifier
    ) {
        composable(ROUTE_FEATURES_LIST) {
            FeaturesScreen(
                onOpenHabit = { navController.navigate(ROUTE_FEATURES_HABIT) }
            )
        }
        composable(ROUTE_FEATURES_HABIT) {
            HabitScreen(onBack = { navController.popBackStack() })
        }
    }
}
