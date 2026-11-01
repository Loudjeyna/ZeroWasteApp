package com.zerowaste.app.data.repository

import com.zerowaste.app.data.db.ShoppingItemDao
import com.zerowaste.app.data.model.FoodCategory
import com.zerowaste.app.data.model.QuantityUnit
import com.zerowaste.app.data.model.ShoppingItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ShoppingRepository — طبقة تجريد لقائمة التسوق
 */
@Singleton
class ShoppingRepository @Inject constructor(
    private val shoppingItemDao: ShoppingItemDao,
) {

    // ── Insert ────────────────────────────────────────────
    suspend fun insert(item: ShoppingItem): Long {
        return shoppingItemDao.insert(item)
    }

    suspend fun insertAll(items: List<ShoppingItem>): List<Long> {
        return shoppingItemDao.insertAll(items)
    }

    /**
     * إضافة عنصر من فئة/طعام معين (يُستعمل في الإضافة السريعة)
     */
    suspend fun addQuick(
        category: FoodCategory,
        foodId: String,
        quantity: Int = 1,
        unit: QuantityUnit = QuantityUnit.PIECE,
    ): Long {
        val food = category.commonFoods.firstOrNull { it.id == foodId } ?: return -1
        val item = ShoppingItem(
            categoryId = category.name,
            foodId = food.id,
            foodLabelResId = food.labelResId,
            quantity = quantity,
            unitId = unit.name,
        )
        return shoppingItemDao.insert(item)
    }

    /**
     * إضافة عنصر من وصفة (يُستعمل من RecipesViewModel)
     */
    suspend fun addFromRecipe(
        category: FoodCategory,
        foodId: String,
        quantity: Int = 1,
        unit: QuantityUnit = QuantityUnit.PIECE,
        recipeId: String,
    ): Long {
        val food = category.commonFoods.firstOrNull { it.id == foodId } ?: return -1
        val item = ShoppingItem(
            categoryId = category.name,
            foodId = food.id,
            foodLabelResId = food.labelResId,
            quantity = quantity,
            unitId = unit.name,
            fromRecipeId = recipeId,
        )
        return shoppingItemDao.insert(item)
    }

    // ── Update ────────────────────────────────────────────
    suspend fun update(item: ShoppingItem) {
        shoppingItemDao.update(item)
    }

    suspend fun setChecked(item: ShoppingItem, isChecked: Boolean) {
        shoppingItemDao.setChecked(item.id, isChecked)
    }

    // ── Delete ────────────────────────────────────────────
    suspend fun delete(item: ShoppingItem) {
        shoppingItemDao.delete(item)
    }

    suspend fun deleteById(id: Long) {
        shoppingItemDao.deleteById(id)
    }

    suspend fun deleteChecked() {
        shoppingItemDao.deleteChecked()
    }

    suspend fun deleteAll() {
        shoppingItemDao.deleteAll()
    }

    // ── Read ──────────────────────────────────────────────
    fun getAllAsStream(): Flow<List<ShoppingItem>> = shoppingItemDao.getAllAsStream()

    fun getUncheckedAsStream(): Flow<List<ShoppingItem>> = shoppingItemDao.getUncheckedAsStream()

    fun getCheckedAsStream(): Flow<List<ShoppingItem>> = shoppingItemDao.getCheckedAsStream()

    suspend fun getById(id: Long): ShoppingItem? = shoppingItemDao.getById(id)

    // ── Counts ───────────────────────────────────────────
    fun countAll(): Flow<Int> = shoppingItemDao.countAll()

    fun countUnchecked(): Flow<Int> = shoppingItemDao.countUnchecked()

    fun countChecked(): Flow<Int> = shoppingItemDao.countChecked()
}
