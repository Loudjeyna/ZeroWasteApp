package com.zerowaste.app.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.zerowaste.app.data.model.FoodItem
import kotlinx.coroutines.flow.Flow

/**
 * FoodItemDao — Data Access Object لعمليات قاعدة البيانات على FoodItem
 *
 * كل الدوال تعمل على background thread تلقائياً (suspend)
 * ما عدا those returning Flow/LiveData (تُحدَّث تلقائياً عند تغيير البيانات)
 */
@Dao
interface FoodItemDao {

    // ── INSERT ──────────────────────────────────────────────
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(foodItem: FoodItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(foodItems: List<FoodItem>): List<Long>

    // ── UPDATE ──────────────────────────────────────────────
    @Update
    suspend fun update(foodItem: FoodItem)

    // ── DELETE ──────────────────────────────────────────────
    @Delete
    suspend fun delete(foodItem: FoodItem)

    @Query("DELETE FROM food_items WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM food_items")
    suspend fun deleteAll()

    // ── SELECT ──────────────────────────────────────────────
    @Query("SELECT * FROM food_items ORDER BY expiryTimestamp ASC")
    fun getAllAsStream(): Flow<List<FoodItem>>

    @Query("SELECT * FROM food_items ORDER BY expiryTimestamp ASC")
    suspend fun getAll(): List<FoodItem>

    @Query("SELECT * FROM food_items WHERE id = :id")
    suspend fun getById(id: Long): FoodItem?

    // ── COUNTS (للبادجات) ────────────────────────────────
    @Query("SELECT COUNT(*) FROM food_items")
    fun countAll(): Flow<Int>

    @Query("SELECT COUNT(*) FROM food_items WHERE expiryTimestamp <= :threshold")
    fun countExpiring(threshold: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM food_items WHERE expiryTimestamp <= :now")
    fun countExpired(now: Long): Flow<Int>

    // ── FILTERS (حسب الفئة) ──────────────────────────────
    @Query("SELECT * FROM food_items WHERE categoryId = :category ORDER BY expiryTimestamp ASC")
    fun getByCategory(category: String): Flow<List<FoodItem>>

    // ── FILTERS (حسب مكان التخزين) ───────────────────────
    @Query("SELECT * FROM food_items WHERE locationId = :location ORDER BY expiryTimestamp ASC")
    fun getByLocation(location: String): Flow<List<FoodItem>>

    // ── FILTERS (حسب قرب الانتهاء) ──────────────────────
    @Query("SELECT * FROM food_items WHERE expiryTimestamp <= :threshold AND expiryTimestamp > :now ORDER BY expiryTimestamp ASC")
    fun getExpiringSoon(threshold: Long, now: Long): Flow<List<FoodItem>>

    @Query("SELECT * FROM food_items WHERE expiryTimestamp <= :now ORDER BY expiryTimestamp ASC")
    fun getExpired(now: Long): Flow<List<FoodItem>>

    @Query("SELECT * FROM food_items WHERE expiryTimestamp > :threshold ORDER BY expiryTimestamp ASC")
    fun getFresh(threshold: Long): Flow<List<FoodItem>>
}
