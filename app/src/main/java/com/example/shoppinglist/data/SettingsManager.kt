package com.example.shoppinglist.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsManager(private val context: Context) {
    companion object {
        val THEME_KEY = stringPreferencesKey("theme")
        val CURRENCY_KEY = stringPreferencesKey("currency")
        val IS_LOGGED_IN_KEY = booleanPreferencesKey("is_logged_in")
        val USER_LOGIN_KEY = stringPreferencesKey("user_login")
        val USER_PASSWORD_KEY = stringPreferencesKey("user_password")
    }

    val themeFlow = context.dataStore.data.map { it[THEME_KEY] ?: "system" }
    val currencyFlow = context.dataStore.data.map { it[CURRENCY_KEY] ?: "₽" }
    val isLoggedInFlow = context.dataStore.data.map { it[IS_LOGGED_IN_KEY] ?: false }
    val savedLoginFlow = context.dataStore.data.map { it[USER_LOGIN_KEY] }
    val savedPasswordFlow = context.dataStore.data.map { it[USER_PASSWORD_KEY] }

    suspend fun saveTheme(theme: String) {
        context.dataStore.edit { it[THEME_KEY] = theme }
    }
    suspend fun saveCurrency(symbol: String) {
        context.dataStore.edit { it[CURRENCY_KEY] = symbol }
    }
    suspend fun setLoggedIn(isLoggedIn: Boolean) {
        context.dataStore.edit { it[IS_LOGGED_IN_KEY] = isLoggedIn }
    }
    suspend fun saveUser(login: String, password: String) {
        context.dataStore.edit {
            it[USER_LOGIN_KEY] = login
            it[USER_PASSWORD_KEY] = password
        }
    }
}