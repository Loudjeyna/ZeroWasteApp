package com.zerowaste.app.data.repository

import com.zerowaste.app.data.model.FoodItem
import com.zerowaste.app.data.model.Recipe
import com.zerowaste.app.data.model.RecipeData
import com.zerowaste.app.data.model.RecipeIngredient
import com.zerowaste.app.data.model.RecipeMatchResult
import com.zerowaste.app.data.model.RecipeMatchStatus
import javax.inject.Inject
import javax.inject.Singleton

/**
 * RecipeRepository — طبقة الوصول لبيانات الوصفات + المطابقة الذكية
 *
 * ✅ مطابقة مرنة: مكوّن "حمص" يطابق food_id "chickpeas" أو "chickpeas_canned"
 * ✅ تجاهل الكمية: نتحقق فقط من الوجود، لا نتحقق إن كانت الكمية كافية
 * ✅ مكوّنات اختيارية: لا تُحتسب في نسبة المطابقة
 */
@Singleton
class RecipeRepository @Inject constructor() {

    /**
     * الحصول على كل الوصفات
     */
    fun getAllRecipes(): List<Recipe> = RecipeData.allRecipes

    /**
     * المطابقة الذكية بين وصفة ومخزون المستخدم
     *
     * @param recipe الوصفة
     * @param inventoryItems العناصر في مخزون المستخدم
     * @return نتيجة المطابقة (matched + missing + status)
     */
    fun matchRecipe(recipe: Recipe, inventoryItems: List<FoodItem>): RecipeMatchResult {
        // استخراج كل food IDs الموجودة في المخزون
        val inventoryFoodIds = inventoryItems.map { it.foodId }.toSet()

        val matched = mutableListOf<RecipeIngredient>()
        val missing = mutableListOf<RecipeIngredient>()

        for (ingredient in recipe.ingredients) {
            // مطابقة مرنة: يكفي وجود أحد الأطعمة في matchingFoodIds
            val isAvailable = ingredient.matchingFoodIds.any { it in inventoryFoodIds }

            if (isAvailable) {
                matched.add(ingredient)
            } else {
                missing.add(ingredient)
            }
        }

        val requiredCount = recipe.ingredients.count { !it.isOptional }
        val matchedRequiredCount = matched.count { !it.isOptional }

        val status = when {
            requiredCount == 0 -> RecipeMatchStatus.FULL
            matchedRequiredCount == requiredCount -> RecipeMatchStatus.FULL
            matchedRequiredCount == 0 -> RecipeMatchStatus.NONE
            else -> RecipeMatchStatus.PARTIAL
        }

        return RecipeMatchResult(
            recipe = recipe,
            matchedIngredients = matched,
            missingIngredients = missing,
            matchStatus = status,
        )
    }

    /**
     * مطابقة كل الوصفات دفعة واحدة
     */
    fun matchAllRecipes(inventoryItems: List<FoodItem>): List<RecipeMatchResult> {
        return RecipeData.allRecipes.map { matchRecipe(it, inventoryItems) }
    }

    /**
     * الفلترة: فقط الوصفات التي يمكن طبخها الآن (مطابقة كاملة)
     */
    fun getCookableRecipes(inventoryItems: List<FoodItem>): List<RecipeMatchResult> {
        return matchAllRecipes(inventoryItems).filter { it.matchStatus == RecipeMatchStatus.FULL }
    }

    /**
     * البحث في الوصفات بالاسم (يُستعمل من الـ ViewModel مع الـ context لقراءة الـ strings)
     * نعيد كل الوصفات، والـ ViewModel يفلتر بالاسم
     */
    fun searchRecipes(query: String): List<Recipe> {
        if (query.isBlank()) return RecipeData.allRecipes
        // ملاحظة: البحث الفعلي بالاسم العربي يتم في الـ ViewModel عبر stringResource
        return RecipeData.allRecipes
    }
}
