package com.schoolfinder.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.collectAsState
import com.schoolfinder.app.ui.screens.LoginScreen
import com.schoolfinder.app.ui.screens.MainScaffold
import com.schoolfinder.app.ui.screens.RegisterScreen

@Composable
fun RootApp() {
    val session = LocalSession.current
    val current by session.current.collectAsState()

    if (current == null) {
        AuthFlow()
    } else {
        MainScaffold()
    }
}

@Composable
private fun AuthFlow() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(onNavigateToRegister = { navController.navigate("register") })
        }
        composable("register") {
            RegisterScreen(onBack = { navController.popBackStack() })
        }
    }
}
