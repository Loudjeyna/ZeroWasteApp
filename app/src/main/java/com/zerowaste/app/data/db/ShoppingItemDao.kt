package com.zerowaste.app.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.zerowaste.app.data.model.ShoppingItem
import kotlinx.coroutines.flow.Flow

/**
 * ShoppingItemDao — DAO لقائمة التسوق
 */
@Dao
interface ShoppingItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ShoppingItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<ShoppingItem>): List<Long>

    @Update
    suspend fun update(item: ShoppingItem)

    @Delete
    suspend fun delete(item: ShoppingItem)

    @Query("DELETE FROM shopping_items WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM shopping_items WHERE isChecked = 1")
    suspend fun deleteChecked()

    @Query("DELETE FROM shopping_items")
    suspend fun deleteAll()

    // ── Select ──────────────────────────────────────────
    @Query("SELECT * FROM shopping_items ORDER BY isChecked ASC, categoryId ASC, addedTimestamp DESC")
    fun getAllAsStream(): Flow<List<ShoppingItem>>

    @Query("SELECT * FROM shopping_items WHERE isChecked = 0 ORDER BY categoryId ASC, addedTimestamp DESC")
    fun getUncheckedAsStream(): Flow<List<ShoppingItem>>

    @Query("SELECT * FROM shopping_items WHERE isChecked = 1 ORDER BY categoryId ASC, addedTimestamp DESC")
    fun getCheckedAsStream(): Flow<List<ShoppingItem>>

    @Query("SELECT * FROM shopping_items WHERE id = :id")
    suspend fun getById(id: Long): ShoppingItem?

    // ── Counts ────────────────────────────────────────
    @Query("SELECT COUNT(*) FROM shopping_items")
    fun countAll(): Flow<Int>

    @Query("SELECT COUNT(*) FROM shopping_items WHERE isChecked = 0")
    fun countUnchecked(): Flow<Int>

    @Query("SELECT COUNT(*) FROM shopping_items WHERE isChecked = 1")
    fun countChecked(): Flow<Int>

    // ── Toggle check ──────────────────────────────────
    @Query("UPDATE shopping_items SET isChecked = :isChecked WHERE id = :id")
    suspend fun setChecked(id: Long, isChecked: Boolean)
}
