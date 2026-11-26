package com.example.shoppinglist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.shoppinglist.data.AppDatabase
import com.example.shoppinglist.data.SettingsManager
import com.example.shoppinglist.data.ShoppingRepository
import com.example.shoppinglist.ui.AppNavigation
import com.example.shoppinglist.ui.SettingsViewModel
import com.example.shoppinglist.ui.ShoppingViewModel
import com.example.shoppinglist.ui.theme.ShoppingListTheme
import androidx.compose.foundation.isSystemInDarkTheme
import com.example.shoppinglist.ui.LoginScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "shopping-db"
        ).fallbackToDestructiveMigration().build()

        val repo = ShoppingRepository(db.shoppingDao())
        val settingsManager = SettingsManager(applicationContext)

        // Фабрика для ShoppingViewModel
        val shoppingViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return ShoppingViewModel(repo) as T
            }
        })[ShoppingViewModel::class.java]

        // Фабрика для SettingsViewModel
        val settingsViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return SettingsViewModel(settingsManager) as T
            }
        })[SettingsViewModel::class.java]

        setContent {
            val themeSetting by settingsViewModel.theme.collectAsState()
            val isDarkTheme = when (themeSetting) {
                "dark" -> true
                "light" -> false
                else -> isSystemInDarkTheme()
            }

            val isLoggedIn by settingsViewModel.isLoggedIn.collectAsState()

            ShoppingListTheme(darkTheme = isDarkTheme) {
                if (!isLoggedIn) {
                    LoginScreen(viewModel = settingsViewModel)
                } else {
                    AppNavigation(shoppingViewModel, settingsViewModel)
                }
            }
        }
    }
}