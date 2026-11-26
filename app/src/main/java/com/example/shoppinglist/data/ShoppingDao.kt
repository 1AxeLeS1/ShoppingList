package com.example.shoppinglist.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingDao {
    // Активные товары
    @Query("SELECT * FROM shopping_items WHERE isArchived = 0")
    fun getActiveItems(): Flow<List<ShoppingItem>>

    // История
    @Query("SELECT * FROM shopping_items WHERE isArchived = 1")
    fun getArchivedItems(): Flow<List<ShoppingItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ShoppingItem)

    @Update
    suspend fun update(item: ShoppingItem)

    // Массовая архивация (в историю) по списку ID
    @Query("UPDATE shopping_items SET isArchived = 1 WHERE id IN (:ids)")
    suspend fun archiveItems(ids: List<Long>)

    // Массовое восстановление из истории
    @Query("UPDATE shopping_items SET isArchived = 0 WHERE id IN (:ids)")
    suspend fun restoreItems(ids: List<Long>)

    // Массовое удаление из истории навсегда
    @Query("DELETE FROM shopping_items WHERE id IN (:ids)")
    suspend fun deleteItemsPermanently(ids: List<Long>)

    // Полная очистка истории
    @Query("DELETE FROM shopping_items WHERE isArchived = 1")
    suspend fun clearHistory()

    // Удалить всё (сброс данных)
    @Query("DELETE FROM shopping_items")
    suspend fun clearAllData()
}