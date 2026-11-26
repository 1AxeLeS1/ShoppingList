package com.example.shoppinglist.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppinglist.data.SettingsManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val settingsManager: SettingsManager) : ViewModel() {
    val theme = settingsManager.themeFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "system")
    val currency = settingsManager.currencyFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "₽")
    val isLoggedIn = settingsManager.isLoggedInFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun setTheme(theme: String) = viewModelScope.launch { settingsManager.saveTheme(theme) }
    fun setCurrency(symbol: String) = viewModelScope.launch { settingsManager.saveCurrency(symbol) }
    fun logout() = viewModelScope.launch { settingsManager.setLoggedIn(false) }
    fun register(login: String, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val existingLogin = settingsManager.savedLoginFlow.first()
            if (existingLogin != null) {
                // Профиль уже существует
                onResult(false)
            } else {
                settingsManager.saveUser(login, password)
                settingsManager.setLoggedIn(true)
                onResult(true)
            }
        }
    }
    fun login(login: String, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val savedLogin = settingsManager.savedLoginFlow.first()
            val savedPassword = settingsManager.savedPasswordFlow.first()

            if (savedLogin == login && savedPassword == password) {
                settingsManager.setLoggedIn(true)
                onResult(true)
            } else {
                onResult(false)
            }
        }
    }
}