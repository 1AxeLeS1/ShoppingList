package com.example.shoppinglist.data

import kotlinx.coroutines.flow.Flow

class ShoppingRepository(private val dao: ShoppingDao) {
    val activeItems: Flow<List<ShoppingItem>> = dao.getActiveItems()
    val archivedItems: Flow<List<ShoppingItem>> = dao.getArchivedItems()

    suspend fun insert(item: ShoppingItem) = dao.insert(item)
    suspend fun update(item: ShoppingItem) = dao.update(item)
    suspend fun archiveItems(ids: List<Long>) = dao.archiveItems(ids)
    suspend fun restoreItems(ids: List<Long>) = dao.restoreItems(ids)
    suspend fun deleteItemsPermanently(ids: List<Long>) = dao.deleteItemsPermanently(ids)
    suspend fun clearHistory() = dao.clearHistory()
    suspend fun clearAllData() = dao.clearAllData()
}