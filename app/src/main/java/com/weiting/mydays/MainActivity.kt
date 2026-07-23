package com.weiting.mydays

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.weiting.mydays.data.habit.HabitSyncManager
import com.weiting.mydays.ui.auth.AuthViewModel
import com.weiting.mydays.ui.auth.LoginScreen
import com.weiting.mydays.ui.main.MainScreen
import com.weiting.mydays.ui.theme.MyDaysTheme
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

private const val ROUTE_LOGIN = "login"
private const val ROUTE_MAIN = "main"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyDaysTheme {
                MyDaysNavHost(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
private fun MyDaysNavHost(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = koinViewModel(),
    syncManager: HabitSyncManager = koinInject()
) {
    val navController = rememberNavController()
    val currentUser by authViewModel.currentUser.collectAsState()

    LaunchedEffect(currentUser) {
        val destination = if (currentUser != null) ROUTE_MAIN else ROUTE_LOGIN
        navController.navigate(destination) {
            popUpTo(navController.graph.id) {
                inclusive = true
            }
            launchSingleTop = true
        }
        if (currentUser != null) syncManager.scheduleSync()
    }

    NavHost(
        navController = navController,
        startDestination = ROUTE_LOGIN,
        modifier = modifier
    ) {
        composable(ROUTE_LOGIN) {
            LoginScreen(viewModel = authViewModel)
        }
        composable(ROUTE_MAIN) {
            MainScreen(onLogout = { authViewModel.signOut() })
        }
    }
}
