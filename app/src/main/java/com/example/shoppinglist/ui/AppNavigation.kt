package com.example.shoppinglist.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.Surface
import androidx.compose.foundation.layout.fillMaxSize

@Composable
fun AppNavigation(
    shoppingViewModel: ShoppingViewModel,
    settingsViewModel: SettingsViewModel
) {

    val navController = rememberNavController()
    val currency by settingsViewModel.currency.collectAsState()

    val items = listOf(
        BottomNavItem("list", "Список", Icons.Default.ShoppingCart),
        BottomNavItem("history", "История", Icons.Default.History),
        BottomNavItem("settings", "Настройки", Icons.Default.Settings)
    )
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            bottomBar = {
                NavigationBar {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route
                    items.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = currentRoute == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "list",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("list") { ShoppingScreen(shoppingViewModel, currency) }
                composable("history") { HistoryScreen(shoppingViewModel, currency) }
                composable("settings") { SettingsScreen(settingsViewModel) }
            }
        }
    }
}

data class BottomNavItem(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)