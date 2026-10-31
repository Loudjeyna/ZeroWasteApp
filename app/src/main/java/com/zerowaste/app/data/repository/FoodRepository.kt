package com.zerowaste.app.data.repository

import com.zerowaste.app.data.db.FoodItemDao
import com.zerowaste.app.data.model.FoodItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * FoodRepository — طبقة تجريد بين ViewModel و DAO
 *
 * - يخفي تفاصيل قاعدة البيانات عن الـ UI
 * - يسهّل الاختبار (mockable)
 * - يسمح بإضافة مصادر بيانات أخرى لاحقاً (API, File...)
 */
@Singleton
class FoodRepository @Inject constructor(
    private val foodItemDao: FoodItemDao,
) {
    // ── Insert ─────────────────────────────────────────────
    suspend fun insert(foodItem: FoodItem): Long {
        return foodItemDao.insert(foodItem)
    }

    suspend fun insertAll(foodItems: List<FoodItem>): List<Long> {
        return foodItemDao.insertAll(foodItems)
    }

    // ── Update ────────────────────────────────────────────
    suspend fun update(foodItem: FoodItem) {
        foodItemDao.update(foodItem)
    }

    // ── Delete ────────────────────────────────────────────
    suspend fun delete(foodItem: FoodItem) {
        foodItemDao.delete(foodItem)
    }

    suspend fun deleteById(id: Long) {
        foodItemDao.deleteById(id)
    }

    suspend fun deleteAll() {
        foodItemDao.deleteAll()
    }

    // ── Read ──────────────────────────────────────────────
    fun getAllAsStream(): Flow<List<FoodItem>> = foodItemDao.getAllAsStream()

    suspend fun getAll(): List<FoodItem> = foodItemDao.getAll()

    suspend fun getById(id: Long): FoodItem? = foodItemDao.getById(id)

    // ── Counts ───────────────────────────────────────────
    fun countAll(): Flow<Int> = foodItemDao.countAll()

    fun countExpiring(threshold: Long): Flow<Int> = foodItemDao.countExpiring(threshold)

    fun countExpired(now: Long): Flow<Int> = foodItemDao.countExpired(now)

    // ── Filters ───────────────────────────────────────────
    fun getByCategory(category: String): Flow<List<FoodItem>> =
        foodItemDao.getByCategory(category)

    fun getByLocation(location: String): Flow<List<FoodItem>> =
        foodItemDao.getByLocation(location)

    fun getExpiringSoon(threshold: Long, now: Long): Flow<List<FoodItem>> =
        foodItemDao.getExpiringSoon(threshold, now)

    fun getExpired(now: Long): Flow<List<FoodItem>> =
        foodItemDao.getExpired(now)

    fun getFresh(threshold: Long): Flow<List<FoodItem>> =
        foodItemDao.getFresh(threshold)
}
