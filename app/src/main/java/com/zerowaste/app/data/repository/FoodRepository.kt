package com.zerowaste.app.data.repository

import com.zerowaste.app.data.db.FoodItemDao
import com.zerowaste.app.data.model.FoodItem
import com.zerowaste.app.data.model.FoodItemStatus
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * FoodRepository — طبقة تجريد بين ViewModel و DAO
 *
 * تحديثات D1:
 *  - useAmount(item, amount): استهلاك جزئي
 *  - finish(item): استهلاك كامل
 *  - discard(item): تخلّص منه
 *  - autoExpire(): تحويل العناصر المنتهية صلاحيتها إلى EXPIRED تلقائياً
 *  - getActive() / getHistory(): قراءات حسب الحالة
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

    // ── Delete (نادراً ما يُستعمل — نحتفظ بالسجل) ────────
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

    fun getActiveAsStream(): Flow<List<FoodItem>> = foodItemDao.getActiveAsStream()

    fun getExpiredAsStream(): Flow<List<FoodItem>> = foodItemDao.getExpiredAsStream()

    fun getHistoryAsStream(): Flow<List<FoodItem>> = foodItemDao.getHistoryAsStream()

    // ── Counts ───────────────────────────────────────────
    fun countAll(): Flow<Int> = foodItemDao.countAll()

    fun countActive(): Flow<Int> = foodItemDao.countActive()

    fun countExpired(): Flow<Int> = foodItemDao.countExpired()

    /**
     * عدد العناصر النشطة التي ستنتهي صلاحيتها خلال 3 أيام (افتراضياً)
     */
    fun countExpiring(thresholdMillis: Long = System.currentTimeMillis() + (3L * 24 * 60 * 60 * 1000)): Flow<Int> {
        val now = System.currentTimeMillis()
        return foodItemDao.countExpiring(thresholdMillis, now)
    }

    fun countDiscarded(): Flow<Int> = foodItemDao.countDiscarded()

    fun countFinished(): Flow<Int> = foodItemDao.countFinished()

    // ── Filters ───────────────────────────────────────────
    fun getByCategory(category: String): Flow<List<FoodItem>> =
        foodItemDao.getByCategory(category)

    fun getByLocation(location: String): Flow<List<FoodItem>> =
        foodItemDao.getByLocation(location)

    // ═══════════════════════════════════════════════════════
    //  ✅ إجراءات Use / Finish / Discard
    // ═══════════════════════════════════════════════════════

    /**
     * Use: استهلاك جزء من الطعام
     *
     * @param item الطعام
     * @param amount الكمية المُستهلَكة
     *
     * المنطق:
     *  - إذا (المتبقي - amount) <= 0 → status = FINISHED
     *  - وإلا → status = IN_USE
     */
    suspend fun useAmount(item: FoodItem, amount: Int) {
        if (amount <= 0) return

        val newConsumed = (item.consumedQuantity + amount).coerceAtMost(item.quantity)
        val newStatus = if (newConsumed >= item.quantity) {
            FoodItemStatus.FINISHED
        } else {
            FoodItemStatus.IN_USE
        }
        val now = System.currentTimeMillis()

        foodItemDao.useAmount(
            id = item.id,
            amount = amount,
            newStatus = newStatus.name,
            timestamp = now
        )
    }

    /**
     * Finish: استهلاك كامل (وضع consumedQuantity = quantity)
     */
    suspend fun finish(item: FoodItem) {
        val now = System.currentTimeMillis()
        foodItemDao.finishItem(item.id, now)
    }

    /**
     * Discard: تخلّص منه (status = DISCARDED، نحتفظ بـ consumedQuantity كما هو)
     *
     * لاحقًا في الإحصاءات:
     *  - الهدر = quantity - consumedQuantity (لكل عنصر DISCARDED)
     */
    suspend fun discard(item: FoodItem) {
        val now = System.currentTimeMillis()
        foodItemDao.discardItem(item.id, now)
    }

    /**
     * Auto-Expire: تحويل كل العناصر المنتهية صلاحيتها ولم تُستهلك
     * إلى EXPIRED تلقائياً.
     *
     * يُستدعى من ViewModel عند تحميل المخزون.
     */
    suspend fun autoExpire() {
        val now = System.currentTimeMillis()
        val all = foodItemDao.getAll()
        val toExpire = all.filter {
            it.expiryTimestamp < now &&
                    (it.toStatus() == FoodItemStatus.AVAILABLE || it.toStatus() == FoodItemStatus.IN_USE)
        }
        for (item in toExpire) {
            foodItemDao.updateStatus(item.id, FoodItemStatus.EXPIRED.name, now)
        }
    }
}
