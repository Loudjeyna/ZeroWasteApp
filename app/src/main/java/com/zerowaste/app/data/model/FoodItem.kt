package com.zerowaste.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * FoodItemStatus — حالة الطعام من حيث الاستهلاك
 *
 * التدفّق:
 *   AVAILABLE ──(Use part)──> IN_USE ──(Use part)──> IN_USE ... ──(Use all)──> FINISHED
 *      │                         │
 *      │                         ├──(Discard)──> DISCARDED
 *      │                         │
 *      ├──(Finish all)──> FINISHED
 *      │
 *      ├──(Discard)──> DISCARDED
 *      │
 *      └──(Auto: expiry < now)──> EXPIRED
 *
 * تحليل لاحق:
 *  - consumedQuantity = استهلاك فعلي
 *  - quantity - consumedQuantity = المتبقي عند FINISHED/DISCARDED/EXPIRED = الهدر
 *  - DISCARDED + EXPIRED = هدر
 *  - FINISHED = استهلاك كامل (ناجح)
 */
enum class FoodItemStatus {
    AVAILABLE,    // متاح (لم يُستهلك بعد)
    IN_USE,       // استُهلك جزء منه (لا يزال متبقٍ)
    FINISHED,     // استُهلك كاملاً ✅
    DISCARDED,    // تخلّص منه المستخدم 🗑️
    EXPIRED;      // انتهت صلاحيته بدون استخدام ⏰

    /** تسمية الحالة بالعربية */
    val labelResId: Int
        get() = when (this) {
            AVAILABLE -> com.zerowaste.app.R.string.status_available
            IN_USE -> com.zerowaste.app.R.string.status_in_use
            FINISHED -> com.zerowaste.app.R.string.status_finished
            DISCARDED -> com.zerowaste.app.R.string.status_discarded
            EXPIRED -> com.zerowaste.app.R.string.status_expired
        }

    /** إيموجي الحالة للعرض البصري */
    val emoji: String
        get() = when (this) {
            AVAILABLE -> "🟢"
            IN_USE -> "🔵"
            FINISHED -> "✅"
            DISCARDED -> "🗑️"
            EXPIRED -> "⏰"
        }

    /** لون الحالة (ARGB) */
    val colorArgb: Long
        get() = when (this) {
            AVAILABLE -> 0xFF4CAF50L   // أخضر
            IN_USE -> 0xFF42A5F5L      // أزرق
            FINISHED -> 0xFF66BB6AL    // أخضر فاتح
            DISCARDED -> 0xFFEF5350L   // أحمر
            EXPIRED -> 0xFFEF6C00L     // برتقالي
        }

    /** هل هذا الطعام "نشط" (يظهر في القائمة الافتراضية)؟ */
    val isActive: Boolean
        get() = this == AVAILABLE || this == IN_USE
}

/**
 * FoodItem — Entity لقاعدة البيانات
 *
 * تحديثات D1:
 *  - quantity أصبحت "الكمية الأصلية" (ثابتة بعد الإنشاء)
 *  - consumedQuantity: الكمية المُستهلَكة (تبدأ من 0)
 *  - status: حالة الطعام (AVAILABLE / IN_USE / FINISHED / DISCARDED / EXPIRED)
 *  - actionTimestamp: وقت آخر إجراء (use/finish/discard)
 *
 * المتبقي = quantity - consumedQuantity (محسوب، غير مخزّن)
 */
@Entity(tableName = "food_items")
data class FoodItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val categoryId: String,         // FoodCategory.name
    val foodId: String,            // مثل "apple"
    val foodLabelResId: Int,       // R.string.food_apple
    val quantity: Int,             // الكمية الأصلية (ثابتة)
    val unitId: String,            // QuantityUnit.name
    val expiryTimestamp: Long,     // millis
    val locationId: String,        // StorageLocation.name
    val addedTimestamp: Long = System.currentTimeMillis(),

    // ✅ حقول جديدة (D1)
    val consumedQuantity: Int = 0,        // الكمية المُستهلَكة (للجزئي)
    val status: String = FoodItemStatus.AVAILABLE.name,  // حالة الطعام
    val actionTimestamp: Long? = null,    // وقت آخر إجراء
) {

    /** استرجاع الـ status كـ enum */
    fun toStatus(): FoodItemStatus = runCatching {
        FoodItemStatus.valueOf(status)
    }.getOrDefault(FoodItemStatus.AVAILABLE)

    /** الكمية المتبقية (محسوبة) */
    val remainingQuantity: Int
        get() = (quantity - consumedQuantity).coerceAtLeast(0)

    /** هل اكتمل استهلاكه؟ */
    val isFullyConsumed: Boolean
        get() = consumedQuantity >= quantity

    /** استرجاع الـ enums من أسمائها (مع fallback آمن) */
    fun toCategory(): FoodCategory = runCatching {
        FoodCategory.valueOf(categoryId)
    }.getOrDefault(FoodCategory.OTHER)

    fun toUnit(): QuantityUnit = runCatching {
        QuantityUnit.valueOf(unitId)
    }.getOrDefault(QuantityUnit.PIECE)

    fun toLocation(): StorageLocation = runCatching {
        StorageLocation.valueOf(locationId)
    }.getOrDefault(StorageLocation.FRIDGE)
}

/**
 * حالة الطعام من حيث الصلاحية (مرتبطة بالـ expiry، مستقلة عن الاستهلاك)
 */
enum class FoodExpiryStatus {
    FRESH,    // طازج - باقي أكثر من 3 أيام
    SOON,     // قريب الانتهاء - باقي 1-3 أيام
    EXPIRED;  // منتهي - تجاوز تاريخ الانتهاء

    companion object {
        fun fromExpiry(expiryTimestamp: Long, now: Long = System.currentTimeMillis()): FoodExpiryStatus {
            val diffMillis = expiryTimestamp - now
            val diffDays = diffMillis / (1000 * 60 * 60 * 24)
            return when {
                diffDays < 0 -> EXPIRED       // تاريخ الانتهاء في الماضي
                diffDays <= 3 -> SOON         // 0-3 أيام
                else -> FRESH                 // أكثر من 3 أيام
            }
        }
    }
}
