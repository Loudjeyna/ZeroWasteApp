package com.zerowaste.app.ui.screens.shopping

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zerowaste.app.data.model.FoodCategory
import com.zerowaste.app.data.model.QuantityUnit
import com.zerowaste.app.data.model.ShoppingItem
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
 * فلتر شاشة التسوق
 */
enum class ShoppingFilter {
    ALL,        // الكل (مع Checked + Unchecked)
    UNCHECKED,  // غير مشترى
    CHECKED,    // مشترى
}

/**
 * ShoppingViewModel — حالة شاشة التسوق
 *
 * ✅ تجميع تلقائي حسب الفئة
 * ✅ Check/Uncheck للعناصر المُشتراة
 * ✅ إضافة سريعة
 * ✅ حذف عنصر / حذف المُشترى كله
 */
@HiltViewModel
class ShoppingViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val shoppingRepository: ShoppingRepository,
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _filter = MutableStateFlow(ShoppingFilter.ALL)
    val filter: StateFlow<ShoppingFilter> = _filter

    /**
     * حالة الواجهة — تجمع بين:
     *   - كل عناصر التسوق
     *   - الفلتر
     *   - البحث
     *
     * ثم نجمّع حسب الفئة داخل الـ lambda
     */
    val uiState: StateFlow<ShoppingUiState> = combine(
        shoppingRepository.getAllAsStream(),
        _filter,
        _searchQuery,
    ) { allItems, filter, query ->
        // تطبيق الفلتر
        val filtered = when (filter) {
            ShoppingFilter.ALL -> allItems
            ShoppingFilter.UNCHECKED -> allItems.filter { !it.isChecked }
            ShoppingFilter.CHECKED -> allItems.filter { it.isChecked }
        }

        // تطبيق البحث
        val searched = if (query.isBlank()) filtered else {
            filtered.filter { item ->
                item.foodId.contains(query, ignoreCase = true) ||
                item.categoryId.contains(query, ignoreCase = true)
            }
        }

        // 🎯 تجميع تلقائي حسب الفئة
        val grouped = searched
            .groupBy { it.categoryId }
            .map { (categoryId, items) ->
                val category = runCatching { FoodCategory.valueOf(categoryId) }
                    .getOrDefault(FoodCategory.OTHER)
                ShoppingGroup(
                    category = category,
                    items = items,
                )
            }
            .sortedBy { it.category.ordinal }

        ShoppingUiState(
            groups = grouped,
            totalCount = allItems.size,
            uncheckedCount = allItems.count { !it.isChecked },
            checkedCount = allItems.count { it.isChecked },
            filter = filter,
            searchQuery = query,
            isEmpty = allItems.isEmpty(),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ShoppingUiState(),
    )

    fun onFilterChanged(newFilter: ShoppingFilter) {
        _filter.value = newFilter
    }

    fun onSearchChanged(query: String) {
        _searchQuery.value = query
    }

    // ── Check/Uncheck ────────────────────────────────────
    fun toggleChecked(item: ShoppingItem) {
        viewModelScope.launch {
            shoppingRepository.setChecked(item, !item.isChecked)
        }
    }

    // ── Delete ────────────────────────────────────────────
    fun delete(item: ShoppingItem) {
        viewModelScope.launch {
            shoppingRepository.delete(item)
        }
    }

    fun deleteChecked() {
        viewModelScope.launch {
            shoppingRepository.deleteChecked()
        }
    }

    // ── Quick Add (من Bottom Sheet) ─────────────────────
    fun quickAdd(category: FoodCategory, foodId: String, quantity: Int, unit: QuantityUnit) {
        viewModelScope.launch {
            shoppingRepository.addQuick(category, foodId, quantity, unit)
        }
    }

    // ── حالة الواجهة ──
    data class ShoppingGroup(
        val category: FoodCategory,
        val items: List<ShoppingItem>,
    )

    data class ShoppingUiState(
        val groups: List<ShoppingGroup> = emptyList(),
        val totalCount: Int = 0,
        val uncheckedCount: Int = 0,
        val checkedCount: Int = 0,
        val filter: ShoppingFilter = ShoppingFilter.ALL,
        val searchQuery: String = "",
        val isEmpty: Boolean = true,
    )
}
