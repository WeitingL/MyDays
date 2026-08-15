package com.weiting.mydays.ui.features

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.weiting.mydays.ui.habit.HabitScreen
import com.weiting.mydays.ui.setting.SettingScreen

private const val ROUTE_SETTING = "setting"
private const val ROUTE_FEATURES_LIST = "features_list"
internal const val ROUTE_FEATURES_HABIT = "features_habit"

/** Sub-navigation graph for the 設定 tab: Setting screen → Features → sub-modules. */
@Composable
fun FeaturesNavHost(
    navController: NavHostController,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = ROUTE_SETTING,
        modifier = modifier
    ) {
        composable(ROUTE_SETTING) {
            SettingScreen(
                onOpenFeatures = { navController.navigate(ROUTE_FEATURES_LIST) },
                onLogout = onLogout
            )
        }
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
