package com.zerowaste.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * FoodItem — Entity لقاعدة البيانات
 *
 * يمثّل طعاماً واحداً أضافه المستخدم إلى مخزونه.
 *
 * ✅ نُخزّن:
 *   - categoryId: اسم الفئة (String) — لتفادي TypeConverter
 *   - foodId: معرّف الطعام (مثل "apple", "milk")
 *   - foodLabelResId: مفتاح النص العربي/الإنجليزي
 *   - quantity + unitId
 *   - expiryTimestamp: تاريخ الانتهاء بالـ millis
 *   - locationId: اسم مكان التخزين
 *   - addedTimestamp: تاريخ الإضافة
 */
@Entity(tableName = "food_items")
data class FoodItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val categoryId: String,         // FoodCategory.name
    val foodId: String,            // مثل "apple"
    val foodLabelResId: Int,       // R.string.food_apple
    val quantity: Int,
    val unitId: String,            // QuantityUnit.name
    val expiryTimestamp: Long,     // millis
    val locationId: String,        // StorageLocation.name
    val addedTimestamp: Long = System.currentTimeMillis(),
) {

    /**
     * استرجاع الـ enums من أسمائها (مع fallback آمن)
     */
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
 * حالة الطعام من حيث الصلاحية
 */
enum class FoodStatus {
    FRESH,    // طازج - باقي أكثر من 3 أيام
    SOON,     // قريب الانتهاء - باقي 1-3 أيام
    EXPIRED;  // منتهي - تجاوز تاريخ الانتهاء

    companion object {
        /**
         * حساب حالة الطعام من تاريخ الانتهاء
         *
         * @param expiryTimestamp تاريخ الانتهاء بالـ millis
         * @param now الوقت الحالي (مخصص للاختبار)
         */
        fun fromExpiry(expiryTimestamp: Long, now: Long = System.currentTimeMillis()): FoodStatus {
            val diffMillis = expiryTimestamp - now
            val diffDays = diffMillis / (1000 * 60 * 60 * 24)
            return when {
                diffDays < 0 -> EXPIRED       // تاريخ الانتهاء في الماضي
                diffDays <= 3 -> SOON         // 0-3 أيام
                else -> FRESH                 // أكثر من 3 أيام
            }
        }

        /**
         * صيغة قابلة للعرض: عدد الأيام المتبقية
         * مثلاً: "باقي 5 أيام" / "منتهي منذ يومين"
         */
        fun daysLabel(expiryTimestamp: Long, now: Long = System.currentTimeMillis()): String {
            val diffDays = (expiryTimestamp - now) / (1000 * 60 * 60 * 24)
            return when {
                diffDays > 0 -> "+$diffDays"     // باقي N يوم
                diffDays == 0L -> "0"             // ينتهي اليوم
                else -> diffDays.toString()      // منتهي منذ |N| يوم
            }
        }
    }
}
