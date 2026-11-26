package com.example.shoppinglist.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.shoppinglist.data.ShoppingItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(viewModel: ShoppingViewModel, currency: String) {
    val archivedItems by viewModel.archivedItems.collectAsState()
    var showClearDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("История покупок") },
                actions = {
                    if (archivedItems.isNotEmpty()) {
                        IconButton(onClick = { showClearDialog = true }) {
                            Icon(Icons.Default.DeleteForever, contentDescription = "Очистить всё", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (archivedItems.isEmpty()) {
            Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("История пуста", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(Modifier.padding(padding).fillMaxSize()) {
                items(archivedItems, key = { it.id }) { item ->
                    ListItem(
                        headlineContent = { Text(item.name) },
                        supportingContent = { Text(item.category) },
                        trailingContent = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                item.price?.let { Text("%.2f $currency".format(it)) }
                                // Кнопка восстановления
                                IconButton(onClick = { viewModel.restoreItems(listOf(item.id)) }) {
                                    Icon(Icons.Default.Restore, contentDescription = "Вернуть в список", tint = MaterialTheme.colorScheme.primary)
                                }
                                // Кнопка удаления навсегда
                                IconButton(onClick = { viewModel.deleteItemsPermanently(listOf(item.id)) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Удалить навсегда")
                                }
                            }
                        }
                    )
                    Divider()
                }
            }
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Очистить историю?") },
            text = { Text("Все товары из истории будут удалены навсегда. Это действие нельзя отменить.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.clearHistory()
                    showClearDialog = false
                }) { Text("Очистить", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = { TextButton(onClick = { showClearDialog = false }) { Text("Отмена") } }
        )
    }
}