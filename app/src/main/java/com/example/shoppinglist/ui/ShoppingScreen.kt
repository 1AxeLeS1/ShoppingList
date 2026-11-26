package com.example.shoppinglist.ui

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.text.Collator
import java.util.Locale
import com.example.shoppinglist.data.ShoppingItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingScreen(viewModel: ShoppingViewModel, currency: String) {
    val items by viewModel.items.collectAsState()
    val sum by viewModel.currentSum.collectAsState()
    val sortOption by viewModel.sortOption.collectAsState()
    val sumMode by viewModel.sumMode.collectAsState()

    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Список покупок") },
                actions = {
                    // Меню сортировки
                    Box {
                        IconButton(onClick = { showSortMenu = true }) {
                            Icon(Icons.Default.Sort, contentDescription = "Сортировка")
                        }
                        DropdownMenu(expanded = showSortMenu, onDismissRequest = { showSortMenu = false }) {
                            SortOption.values().forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option.title) },
                                    onClick = {
                                        viewModel.setSortOption(option)
                                        showSortMenu = false
                                    }
                                )
                            }
                        }
                    }
                    // Массовая архивация (только если есть выбранные)
                    if (items.any { it.isChecked }) {
                        IconButton(onClick = { viewModel.archiveCheckedItems() }) {
                            Icon(Icons.Default.DeleteSweep, contentDescription = "Убрать выполненные", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                    // Поделиться
                    IconButton(onClick = {
                        val text = items.joinToString("\n") {
                            "${if (it.isChecked) "✅" else "⬜"} ${it.name} (${it.category}) ${it.price?.let { p -> "— $p $currency" } ?: ""}"
                        }
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, text.ifBlank { "Список пуст" })
                        }
                        context.startActivity(Intent.createChooser(intent, "Поделиться списком"))
                    }) { Icon(Icons.Default.Share, contentDescription = "Поделиться") }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Добавить")
            }
        }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            if (items.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Список пуст. Добавьте первый товар.", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                // Сумма (кликабельная)
                val sumTitle = when(sumMode) {
                    SumMode.TOTAL -> "Общая сумма"
                    SumMode.REMAINING -> "Осталось купить"
                    SumMode.IN_CART -> "В корзине"
                }
                Text(
                    "$sumTitle: %.2f $currency (нажмите, чтобы сменить)".format(sum),
                    Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        .clickable { viewModel.toggleSumMode() },
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                LazyColumn(Modifier.weight(1f)) {
                    items(items, key = { it.id }) { item ->
                        ListItem(
                            headlineContent = { Text(item.name) },
                            supportingContent = { Text(item.category) },
                            leadingContent = {
                                Checkbox(
                                    checked = item.isChecked,
                                    onCheckedChange = { viewModel.toggleChecked(item) }
                                )
                            },
                            trailingContent = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    item.price?.let { Text("%.2f $currency".format(it)) }
                                    IconButton(onClick = { viewModel.archiveSingleItem(item) }) {
                                        Icon(Icons.Default.Archive, contentDescription = "В историю", tint = MaterialTheme.colorScheme.secondary)
                                    }
                                }
                            },
                            modifier = Modifier.clickable { viewModel.toggleChecked(item) }
                        )
                        Divider()
                    }
                }
            }
        }
    }

    if (showDialog) {
        AddItemDialog(
            onDismiss = { showDialog = false },
            onAdd = { name, category, price ->
                viewModel.addItem(name, category, price)
                showDialog = false
            }
        )
    }
}

@Composable
fun AddItemDialog(onDismiss: () -> Unit, onAdd: (String, String, Double?) -> Unit) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Общее") }
    var priceText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Новый товар") },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Название") })
                OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Категория") })
                // В цене фильтр только для цифр и точки
                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Цена (необязательно)") }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val price = priceText.toDoubleOrNull()
                onAdd(name, category.ifBlank { "Общее" }, price)
            }) { Text("Добавить") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } }
    )
}