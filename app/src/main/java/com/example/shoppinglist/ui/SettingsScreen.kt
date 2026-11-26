package com.example.shoppinglist.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val theme by viewModel.theme.collectAsState()
    val currency by viewModel.currency.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Настройки") }) }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {

            // ВАЛЮТА
            Text("Валюта", Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                listOf("₽" to "Рубль", "$" to "Доллар", "€" to "Евро").forEach { (symbol, name) ->
                    FilterChip(
                        selected = currency == symbol,
                        onClick = { viewModel.setCurrency(symbol) },
                        label = { Text("$name ($symbol)") }
                    )
                }
            }

            Divider(Modifier.padding(vertical = 16.dp))

            // ТЕМА
            Text("Тема оформления", Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)
            Column {
                listOf("system" to "Системная", "light" to "Светлая", "dark" to "Тёмная").forEach { (value, label) ->
                    Row(
                        Modifier.fillMaxWidth().clickable { viewModel.setTheme(value) }.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = theme == value, onClick = { viewModel.setTheme(value) })
                        Text(label, Modifier.padding(start = 8.dp))
                    }
                }
            }

            Divider(Modifier.padding(vertical = 16.dp))

            // ВЫХОД ИЗ ПРОФИЛЯ
            Text("Аккаунт", Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)
            Button(
                onClick = { viewModel.logout() },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Выйти из профиля")
            }
            Spacer(Modifier.height(100.dp))
        }
    }
}