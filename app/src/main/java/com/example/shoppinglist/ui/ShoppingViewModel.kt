package com.example.shoppinglist.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppinglist.data.ShoppingItem
import com.example.shoppinglist.data.ShoppingRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.StateFlow

enum class SortOption(val title: String) {
    CATEGORY("По категориям"),
    NAME("По алфавиту"),
    PRICE_ASC("По цене (возр)"),
    PRICE_DESC("По цене (убыв)")
}

enum class SumMode { TOTAL, REMAINING, IN_CART }

class ShoppingViewModel(private val repo: ShoppingRepository) : ViewModel() {

    private val _sortOption = MutableStateFlow(SortOption.CATEGORY)
    val sortOption = _sortOption.asStateFlow()

    private val _sumMode = MutableStateFlow(SumMode.TOTAL)
    val sumMode = _sumMode.asStateFlow()

    // Получаем товары, сортируем и отдаем в UI
    val items: StateFlow<List<ShoppingItem>> = combine(repo.activeItems, _sortOption) { items, sort ->
        when (sort) {
            SortOption.CATEGORY -> items.sortedBy { it.category }
            SortOption.NAME -> {
                val collator = java.text.Collator.getInstance(java.util.Locale("ru", "RU"))
                items.sortedWith { a, b -> collator.compare(a.name, b.name) }
            }
            SortOption.PRICE_ASC -> items.sortedBy { it.price ?: 0.0 }
            SortOption.PRICE_DESC -> items.sortedByDescending { it.price ?: 0.0 }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Подсчет суммы в зависимости от выбранного режима
    val currentSum: StateFlow<Double> = combine(items, _sumMode) { items, mode ->
        when (mode) {
            SumMode.TOTAL -> items.sumOf { it.price ?: 0.0 }
            SumMode.REMAINING -> items.filter { !it.isChecked }.sumOf { it.price ?: 0.0 }
            SumMode.IN_CART -> items.filter { it.isChecked }.sumOf { it.price ?: 0.0 }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun setSortOption(option: SortOption) { _sortOption.value = option }
    fun toggleSumMode() {
        _sumMode.value = when (_sumMode.value) {
            SumMode.TOTAL -> SumMode.REMAINING
            SumMode.REMAINING -> SumMode.IN_CART
            SumMode.IN_CART -> SumMode.TOTAL
        }
    }

    fun addItem(name: String, category: String, price: Double?) {
        if (name.isBlank()) return
        viewModelScope.launch {
            repo.insert(ShoppingItem(name = name.trim(), category = category, price = price))
        }
    }

    fun toggleChecked(item: ShoppingItem) {
        viewModelScope.launch { repo.update(item.copy(isChecked = !item.isChecked)) }
    }

    // Массовое удаление (отправка в историю)
    fun archiveCheckedItems() {
        viewModelScope.launch {
            val checkedIds = items.value.filter { it.isChecked }.map { it.id }
            if (checkedIds.isNotEmpty()) repo.archiveItems(checkedIds)
        }
    }

    fun archiveSingleItem(item: ShoppingItem) {
        viewModelScope.launch { repo.archiveItems(listOf(item.id)) }
    }

    val archivedItems: StateFlow<List<ShoppingItem>> = repo.archivedItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun restoreItems(ids: List<Long>) {
        viewModelScope.launch { repo.restoreItems(ids) }
    }

    fun deleteItemsPermanently(ids: List<Long>) {
        viewModelScope.launch { repo.deleteItemsPermanently(ids) }
    }

    fun clearHistory() {
        viewModelScope.launch { repo.clearHistory() }
    }
}