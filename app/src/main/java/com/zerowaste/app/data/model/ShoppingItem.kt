package com.zerowaste.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * ShoppingItem — عنصر في قائمة التسوق
 *
 * @param id معرّف فريد
 * @param categoryId الفئة (FoodCategory.name) — للتجميع
 * @param foodId معرّف الطعام (مثل "apple")
 * @param foodLabelResId مفتاح النص
 * @param quantity الكمية المطلوبة
 * @param unitId الوحدة (QuantityUnit.name)
 * @param isChecked هل تمّ شراؤه؟
 * @param addedTimestamp وقت الإضافة
 * @param fromRecipeId معرّف الوصفة التي أضافت هذا العنصر (إن وجدت)
 */
@Entity(tableName = "shopping_items")
data class ShoppingItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val categoryId: String,
    val foodId: String,
    val foodLabelResId: Int,
    val quantity: Int = 1,
    val unitId: String,
    val isChecked: Boolean = false,
    val addedTimestamp: Long = System.currentTimeMillis(),
    val fromRecipeId: String? = null,
) {
    /** استرجاع الـ enums */
    fun toCategory(): FoodCategory = runCatching {
        FoodCategory.valueOf(categoryId)
    }.getOrDefault(FoodCategory.OTHER)

    fun toUnit(): QuantityUnit = runCatching {
        QuantityUnit.valueOf(unitId)
    }.getOrDefault(QuantityUnit.PIECE)
}
