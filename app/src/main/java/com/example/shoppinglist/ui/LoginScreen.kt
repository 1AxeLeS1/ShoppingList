package com.example.shoppinglist.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(viewModel: SettingsViewModel) {
    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isRegisterMode by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (isRegisterMode) "Создание профиля" else "Список покупок",
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = if (isRegisterMode) "Придумайте логин и пароль" else "Войдите, чтобы продолжить",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(Modifier.height(32.dp))

        OutlinedTextField(
            value = login,
            onValueChange = {
                login = it
                errorText = null // Сбрасываем ошибку при вводе
            },
            label = { Text("Логин") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                errorText = null
            },
            label = { Text("Пароль") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        // Вывод ошибки (если есть)
        if (errorText != null) {
            Spacer(Modifier.height(8.dp))
            Text(text = errorText!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = {
                if (isRegisterMode) {
                    viewModel.register(login, password) { success ->
                        if (!success) errorText = "Профиль уже существует. Войдите."
                    }
                } else {
                    viewModel.login(login, password) { success ->
                        if (!success) errorText = "Неверный логин или пароль"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled = login.isNotBlank() && password.isNotBlank()
        ) {
            Text(if (isRegisterMode) "Зарегистрироваться" else "Войти")
        }

        Spacer(Modifier.height(16.dp))

        // Кнопка переключения режима
        TextButton(
            onClick = {
                isRegisterMode = !isRegisterMode
                errorText = null
            }
        ) {
            Text(if (isRegisterMode) "Уже есть профиль? Войти" else "Нет профиля? Создать")
        }
    }
}