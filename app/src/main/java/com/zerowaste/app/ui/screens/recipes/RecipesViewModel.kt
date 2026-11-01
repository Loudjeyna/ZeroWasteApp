package com.zerowaste.app.ui.screens.recipes

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zerowaste.app.data.model.RecipeMatchResult
import com.zerowaste.app.data.model.RecipeMatchStatus
import com.zerowaste.app.data.repository.FoodRepository
import com.zerowaste.app.data.repository.RecipeRepository
import com.zerowaste.app.data.repository.ShoppingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * فلتر شاشة الوصفات
 */
enum class RecipeFilter {
    ALL,                // كل الوصفات
    COOKABLE_NOW,       // ما يمكنني طبخه الآن (مطابقة كاملة)
    PARTIAL,            // ما ينقصه بعض المكوّنات
}

/**
 * RecipesViewModel — حالة شاشة الوصفات
 *
 * يجمع بين:
 *   - قائمة الوصفات (ثابتة في RecipeData)
 *   - مخزون المستخدم (من Room)
 *   - الفلتر + البحث
 *   - نتيجة المطابقة الذكية لكل وصفة
 */
@HiltViewModel
class RecipesViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val recipeRepository: RecipeRepository,
    private val foodRepository: FoodRepository,
    private val shoppingRepository: ShoppingRepository,
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _filter = MutableStateFlow(RecipeFilter.ALL)
    val filter: StateFlow<RecipeFilter> = _filter

    /**
     * حالة الواجهة — تتحدّث تلقائياً عند:
     *   - تغيير المخزون (إضافة/حذف طعام)
     *   - تغيير الفلتر
     *   - تغيير البحث
     */
    val uiState: StateFlow<RecipesUiState> = combine(
        foodRepository.getAllAsStream(),
        _filter,
        _searchQuery,
    ) { items, filter, query ->
        // مطابقة كل الوصفات مع المخزون
        val allMatches = recipeRepository.matchAllRecipes(items)

        // تطبيق الفلتر
        val filtered = when (filter) {
            RecipeFilter.ALL -> allMatches
            RecipeFilter.COOKABLE_NOW -> allMatches.filter { it.matchStatus == RecipeMatchStatus.FULL }
            RecipeFilter.PARTIAL -> allMatches.filter { it.matchStatus == RecipeMatchStatus.PARTIAL }
        }

        // تطبيق البحث على اسم الوصفة
        val searched = if (query.isBlank()) {
            filtered
        } else {
            filtered.filter { match ->
                // قراءة اسم الوصفة من الـ resources
                val recipeName = appContext.getString(match.recipe.nameResId)
                recipeName.contains(query, ignoreCase = true)
            }
        }

        RecipesUiState(
            recipes = searched,
            totalCount = allMatches.size,
            cookableCount = allMatches.count { it.matchStatus == RecipeMatchStatus.FULL },
            partialCount = allMatches.count { it.matchStatus == RecipeMatchStatus.PARTIAL },
            filter = filter,
            searchQuery = query,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = RecipesUiState(),
    )

    fun onFilterChanged(newFilter: RecipeFilter) {
        _filter.value = newFilter
    }

    fun onSearchChanged(query: String) {
        _searchQuery.value = query
    }

    /**
     * إضافة المكوّنات الناقصة لقائمة التسوق
     * ✅ فعّال الآن (S6): نضيف كل مكوّن ناقص إلى ShoppingRepository
     */
    fun addMissingToShopping(match: RecipeMatchResult) {
        viewModelScope.launch {
            val recipe = match.recipe
            for (ingredient in match.missingIngredients) {
                // نجد foodId من قائمة matchingFoodIds (نأخذ الأول)
                val foodId = ingredient.matchingFoodIds.firstOrNull() ?: continue
                // نجد الفئة المناسبة من FoodCategory
                val category = com.zerowaste.app.data.model.FoodCategory.entries.firstOrNull { cat ->
                    cat.commonFoods.any { it.id == foodId }
                } ?: com.zerowaste.app.data.model.FoodCategory.OTHER

                shoppingRepository.addFromRecipe(
                    category = category,
                    foodId = foodId,
                    quantity = 1,
                    unit = com.zerowaste.app.data.model.QuantityUnit.PIECE,
                    recipeId = recipe.id,
                )
            }
        }
    }

    data class RecipesUiState(
        val recipes: List<RecipeMatchResult> = emptyList(),
        val totalCount: Int = 0,
        val cookableCount: Int = 0,
        val partialCount: Int = 0,
        val filter: RecipeFilter = RecipeFilter.ALL,
        val searchQuery: String = "",
    )
}
