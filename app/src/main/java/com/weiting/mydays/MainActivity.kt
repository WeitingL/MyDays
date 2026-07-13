package com.weiting.mydays

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.weiting.mydays.ui.auth.AuthViewModel
import com.weiting.mydays.ui.auth.LoginScreen
import com.weiting.mydays.ui.home.HomeScreen
import com.weiting.mydays.ui.theme.MyDaysTheme

private const val ROUTE_LOGIN = "login"
private const val ROUTE_HOME = "home"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyDaysTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MyDaysNavHost(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
private fun MyDaysNavHost(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = viewModel()
) {
    val navController = rememberNavController()
    val currentUser by authViewModel.currentUser.collectAsState()

    LaunchedEffect(currentUser) {
        val destination = if (currentUser != null) ROUTE_HOME else ROUTE_LOGIN
        navController.navigate(destination) {
            popUpTo(navController.graph.findStartDestination().id) {
                inclusive = true
            }
            launchSingleTop = true
        }
    }

    NavHost(
        navController = navController,
        startDestination = ROUTE_LOGIN,
        modifier = modifier
    ) {
        composable(ROUTE_LOGIN) {
            LoginScreen(viewModel = authViewModel)
        }
        composable(ROUTE_HOME) {
            HomeScreen(onLogout = { authViewModel.signOut() })
        }
    }
}
