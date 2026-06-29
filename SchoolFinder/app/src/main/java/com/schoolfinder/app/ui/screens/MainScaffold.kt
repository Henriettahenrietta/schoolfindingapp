package com.schoolfinder.app.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.schoolfinder.app.ui.navigation.Routes

private data class Tab(val route: String, val label: String, val icon: ImageVector)

@Composable
fun MainScaffold() {
    val navController = rememberNavController()

    val tabs = listOf(
        Tab(Routes.HOME, "Home", Icons.Filled.Home),
        Tab(Routes.COMPARE, "Compare", Icons.Filled.CompareArrows),
        Tab(Routes.FAVORITES, "Favorites", Icons.Filled.Favorite),
        Tab(Routes.PROFILE, "Profile", Icons.Filled.Person)
    )
    val tabRoutes = tabs.map { it.route }.toSet()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute in tabRoutes) {
                NavigationBar {
                    tabs.forEach { tab ->
                        NavigationBarItem(
                            selected = currentRoute == tab.route,
                            onClick = {
                                navController.navigate(tab.route) {
                                    popUpTo(Routes.HOME) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                            label = { Text(tab.label) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(padding)
        ) {
            composable(Routes.HOME) {
                HomeScreen(onOpenSchool = { id -> navController.navigate(Routes.details(id)) })
            }
            composable(Routes.COMPARE) {
                CompareScreen(onOpenSchool = { id -> navController.navigate(Routes.details(id)) })
            }
            composable(Routes.FAVORITES) {
                FavoritesScreen(onOpenSchool = { id -> navController.navigate(Routes.details(id)) })
            }
            composable(Routes.PROFILE) {
                ProfileScreen(onOpenAdmin = { navController.navigate(Routes.ADMIN) })
            }
            composable(Routes.ADMIN) {
                AdminDashboardScreen(
                    onBack = { navController.popBackStack() },
                    onManageSchools = { navController.navigate(Routes.MANAGE_SCHOOLS) }
                )
            }
            composable(Routes.MANAGE_SCHOOLS) {
                ManageSchoolsScreen(onBack = { navController.popBackStack() })
            }
            composable(
                route = "${Routes.DETAILS}/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { entry ->
                val id = entry.arguments?.getInt("id") ?: 0
                SchoolDetailScreen(
                    schoolId = id,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
