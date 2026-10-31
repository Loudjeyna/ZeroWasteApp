package com.zerowaste.app.data.db

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
 * تحديثات D1:
 *  - دوال use/finish/discard (تحديث consumedQuantity و status)
 *  - فلترة حسب status (نشط / منتهي / سجل)
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

    // ── SELECT (الكل) ─────────────────────────────────────
    @Query("SELECT * FROM food_items ORDER BY expiryTimestamp ASC")
    fun getAllAsStream(): Flow<List<FoodItem>>

    @Query("SELECT * FROM food_items ORDER BY expiryTimestamp ASC")
    suspend fun getAll(): List<FoodItem>

    @Query("SELECT * FROM food_items WHERE id = :id")
    suspend fun getById(id: Long): FoodItem?

    // ── SELECT حسب status ───────────────────────────────
    /** العناصر النشطة فقط (AVAILABLE + IN_USE) — للمخزون الافتراضي */
    @Query("SELECT * FROM food_items WHERE status IN ('AVAILABLE', 'IN_USE') ORDER BY expiryTimestamp ASC")
    fun getActiveAsStream(): Flow<List<FoodItem>>

    /** العناصر المنتهية صلاحيتها */
    @Query("SELECT * FROM food_items WHERE status = 'EXPIRED' ORDER BY expiryTimestamp ASC")
    fun getExpiredAsStream(): Flow<List<FoodItem>>

    /** السجل (FINISHED + DISCARDED) — لشاشة الإحصاءات لاحقاً */
    @Query("SELECT * FROM food_items WHERE status IN ('FINISHED', 'DISCARDED') ORDER BY actionTimestamp DESC")
    fun getHistoryAsStream(): Flow<List<FoodItem>>

    // ── COUNTS ───────────────────────────────────────────
    @Query("SELECT COUNT(*) FROM food_items")
    fun countAll(): Flow<Int>

    @Query("SELECT COUNT(*) FROM food_items WHERE status IN ('AVAILABLE', 'IN_USE')")
    fun countActive(): Flow<Int>

    @Query("SELECT COUNT(*) FROM food_items WHERE status = 'EXPIRED'")
    fun countExpired(): Flow<Int>

    /**
     * عدد العناصر النشطة التي ستنتهي صلاحيتها خلال `thresholdMillis` (3 أيام عادة)
     */
    @Query("""
        SELECT COUNT(*) FROM food_items
        WHERE status IN ('AVAILABLE', 'IN_USE')
          AND expiryTimestamp <= :thresholdMillis
          AND expiryTimestamp > :nowMillis
    """)
    fun countExpiring(thresholdMillis: Long, nowMillis: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM food_items WHERE status = 'DISCARDED'")
    fun countDiscarded(): Flow<Int>

    @Query("SELECT COUNT(*) FROM food_items WHERE status = 'FINISHED'")
    fun countFinished(): Flow<Int>

    // ── FILTERS حسب الفئة/المكان ──────────────────────
    @Query("SELECT * FROM food_items WHERE categoryId = :category ORDER BY expiryTimestamp ASC")
    fun getByCategory(category: String): Flow<List<FoodItem>>

    @Query("SELECT * FROM food_items WHERE locationId = :location ORDER BY expiryTimestamp ASC")
    fun getByLocation(location: String): Flow<List<FoodItem>>

    // ── تحديثات Use / Finish / Discard ───────────────

    /**
     * Use: استهلاك جزء من الطعام
     * @param id معرّف الطعام
     * @param amount الكمية المُستهلَكة (سيُضاف إلى consumedQuantity)
     * @param newStatus الحالة الجديدة (IN_USE أو FINISHED)
     * @param timestamp وقت الإجراء
     */
    @Query("""
        UPDATE food_items
        SET consumedQuantity = consumedQuantity + :amount,
            status = :newStatus,
            actionTimestamp = :timestamp
        WHERE id = :id
    """)
    suspend fun useAmount(
        id: Long,
        amount: Int,
        newStatus: String,
        timestamp: Long
    )

    /**
     * Finish: استهلاك كامل (وضع consumedQuantity = quantity)
     */
    @Query("""
        UPDATE food_items
        SET consumedQuantity = quantity,
            status = 'FINISHED',
            actionTimestamp = :timestamp
        WHERE id = :id
    """)
    suspend fun finishItem(id: Long, timestamp: Long)

    /**
     * Discard: تخلّص منه (status = DISCARDED، لا نغيّر consumedQuantity)
     */
    @Query("""
        UPDATE food_items
        SET status = 'DISCARDED',
            actionTimestamp = :timestamp
        WHERE id = :id
    """)
    suspend fun discardItem(id: Long, timestamp: Long)

    /**
     * تحديث الحالة فقط (للـ auto-expire)
     */
    @Query("UPDATE food_items SET status = :newStatus, actionTimestamp = :timestamp WHERE id = :id")
    suspend fun updateStatus(id: Long, newStatus: String, timestamp: Long)
}
