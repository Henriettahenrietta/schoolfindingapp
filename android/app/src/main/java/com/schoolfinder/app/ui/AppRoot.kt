package com.schoolfinder.app.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.schoolfinder.app.data.Session
import com.schoolfinder.app.di.ServiceLocator
import com.schoolfinder.app.ui.screens.CompareScreen
import com.schoolfinder.app.ui.screens.DetailScreen
import com.schoolfinder.app.ui.screens.FavoritesScreen
import com.schoolfinder.app.ui.screens.HomeScreen
import com.schoolfinder.app.ui.screens.LoginScreen
import com.schoolfinder.app.ui.screens.ProfileScreen
import kotlinx.coroutines.launch

object Routes {
    const val HOME = "home"
    const val FAVORITES = "favorites"
    const val PROFILE = "profile"
    fun detail(id: Long) = "detail/$id"
    fun compare(ids: List<Long>) = "compare/${ids.joinToString("-")}"
}

@Composable
fun AppRoot() {
    val scope = rememberCoroutineScope()
    var loading by remember { mutableStateOf(true) }
    var session by remember { mutableStateOf<Session?>(null) }

    LaunchedEffect(Unit) {
        session = ServiceLocator.sessionStore.load()
        loading = false
    }

    when {
        loading -> LoadingBox()
        session == null -> LoginScreen(onLoggedIn = { session = it })
        else -> MainScreen(
            session = session!!,
            onLogout = {
                scope.launch {
                    ServiceLocator.sessionStore.clear()
                    session = null
                }
            },
        )
    }
}

private data class Tab(val route: String, val label: String, val icon: @Composable () -> Unit)

@Composable
private fun MainScreen(session: Session, onLogout: () -> Unit) {
    val nav = rememberNavController()
    val tabs = listOf(
        Tab(Routes.HOME, "Discover") { Icon(Icons.Filled.Home, null) },
        Tab(Routes.FAVORITES, "Favourites") { Icon(Icons.Filled.Favorite, null) },
        Tab(Routes.PROFILE, "Profile") { Icon(Icons.Filled.Person, null) },
    )

    Scaffold(
        bottomBar = {
            val backStack by nav.currentBackStackEntryAsState()
            val current = backStack?.destination
            NavigationBar {
                tabs.forEach { tab ->
                    NavigationBarItem(
                        selected = current?.hierarchy?.any { it.route == tab.route } == true,
                        onClick = {
                            nav.navigate(tab.route) {
                                popUpTo(nav.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = tab.icon,
                        label = { Text(tab.label) },
                    )
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = nav,
            startDestination = Routes.HOME,
            modifier = Modifier.fillMaxSize().padding(padding),
        ) {
            composable(Routes.HOME) {
                HomeScreen(
                    onOpenSchool = { nav.navigate(Routes.detail(it)) },
                    onCompare = { ids -> nav.navigate(Routes.compare(ids)) },
                )
            }
            composable(Routes.FAVORITES) {
                FavoritesScreen(onOpenSchool = { nav.navigate(Routes.detail(it)) })
            }
            composable(Routes.PROFILE) {
                ProfileScreen(session = session, onLogout = onLogout)
            }
            composable("detail/{id}") { entry ->
                val id = entry.arguments?.getString("id")?.toLongOrNull() ?: return@composable
                DetailScreen(schoolId = id, onBack = { nav.popBackStack() })
            }
            composable("compare/{ids}") { entry ->
                val ids = entry.arguments?.getString("ids")
                    ?.split("-")?.mapNotNull { it.toLongOrNull() } ?: emptyList()
                CompareScreen(ids = ids, onBack = { nav.popBackStack() })
            }
        }
    }
}
