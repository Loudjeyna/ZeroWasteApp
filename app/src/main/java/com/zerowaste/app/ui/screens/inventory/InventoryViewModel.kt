package com.zerowaste.app.ui.screens.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zerowaste.app.data.model.FoodItem
import com.zerowaste.app.data.model.FoodStatus
import com.zerowaste.app.data.repository.FoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * فلتر المخزون
 */
enum class InventoryFilter {
    ALL,        // الكل
    EXPIRING,   // قريب الانتهاء
    EXPIRED,    // منتهي
    FRESH,      // طازج
}

/**
 * InventoryViewModel — حالة شاشة المخزون
 *
 * يستمع لتغييرات Room تلقائياً عبر Flow → UI تتحدث لحظياً.
 */
@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val foodRepository: FoodRepository,
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filter = MutableStateFlow(InventoryFilter.ALL)
    val filter: StateFlow<InventoryFilter> = _filter.asStateFlow()

    /**
     * حالة الواجهة — تجمع البيانات + الفلتر + البحث
     */
    val uiState: StateFlow<InventoryUiState> = combine(
        foodRepository.getAllAsStream(),
        _filter,
        _searchQuery,
    ) { items, filter, query ->
        val now = System.currentTimeMillis()
        // 3 أيام threshold للـ soon
        val soonThreshold = now + (3L * 24 * 60 * 60 * 1000)

        // إضافة الـ status لكل عنصر
        val withStatus = items.map { it to FoodStatus.fromExpiry(it.expiryTimestamp, now) }

        // تطبيق الفلتر
        val filtered = when (filter) {
            InventoryFilter.ALL -> withStatus
            InventoryFilter.EXPIRING -> withStatus.filter {
                it.second == FoodStatus.SOON || it.second == FoodStatus.EXPIRED
            }
            InventoryFilter.EXPIRED -> withStatus.filter { it.second == FoodStatus.EXPIRED }
            InventoryFilter.FRESH -> withStatus.filter { it.second == FoodStatus.FRESH }
        }

        // تطبيق البحث (ابحث في أسماء الأطعمة)
        val searched = if (query.isBlank()) filtered else {
            filtered.filter { (item, _) ->
                // مقارنة الـ foodId باسم البحث
                item.foodId.contains(query, ignoreCase = true) ||
                item.categoryId.contains(query, ignoreCase = true)
            }
        }

        InventoryUiState(
            items = searched,
            totalCount = items.size,
            expiringCount = withStatus.count {
                it.second == FoodStatus.SOON || it.second == FoodStatus.EXPIRED
            },
            expiredCount = withStatus.count { it.second == FoodStatus.EXPIRED },
            freshCount = withStatus.count { it.second == FoodStatus.FRESH },
            filter = filter,
            searchQuery = query,
            isEmpty = items.isEmpty(),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = InventoryUiState(),
    )

    // ── تغيير الفلتر ──
    fun onFilterChanged(newFilter: InventoryFilter) {
        _filter.value = newFilter
    }

    // ── تغيير البحث ──
    fun onSearchChanged(query: String) {
        _searchQuery.value = query
    }

    // ── حذف عنصر ──
    fun deleteItem(item: FoodItem) {
        viewModelScope.launch {
            foodRepository.deleteById(item.id)
        }
    }

    // ── حالة الواجهة ──
    data class InventoryUiState(
        val items: List<Pair<FoodItem, FoodStatus>> = emptyList(),
        val totalCount: Int = 0,
        val expiringCount: Int = 0,
        val expiredCount: Int = 0,
        val freshCount: Int = 0,
        val filter: InventoryFilter = InventoryFilter.ALL,
        val searchQuery: String = "",
        val isEmpty: Boolean = true,
    )
}
