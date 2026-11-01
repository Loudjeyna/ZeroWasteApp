package com.zerowaste.app.ui.screens.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zerowaste.app.data.model.FoodExpiryStatus
import com.zerowaste.app.data.model.FoodItem
import com.zerowaste.app.data.model.FoodItemStatus
import com.zerowaste.app.data.repository.FoodRepository
import com.zerowaste.app.data.repository.ShoppingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * فلتر شاشة المخزون — محدّث (D4)
 *
 * توزيع جديد للفلاتر:
 *  - ALL: كل العناصر النشطة (AVAILABLE + IN_USE)
 *  - EXPIRING: النشطة قريبة الانتهاء (≤ 3 أيام)
 *  - EXPIRED: العناصر المنتهية صلاحيتها (EXPIRED)
 *  - HISTORY: السجل (FINISHED + DISCARDED)
 */
enum class InventoryFilter {
    ALL,        // النشط كله
    EXPIRING,   // قريب الانتهاء
    EXPIRED,    // منتهي الصلاحية
    HISTORY,    // سجل الاستهلاك والهدر
}

/**
 * InventoryViewModel — حالة شاشة المخزون (محدّثة D4-fix)
 *
 * ✅ نستعمل combine بـ 3 تدفقات فقط (الحد الأقصى المدعوم بسهولة)
 *   1) getAllAsStream() — كل العناصر
 *   2) _filter — الفلتر الحالي
 *   3) _searchQuery — نص البحث
 *
 * ثم نفلتر القائمة داخل الـ lambda حسب الحالة.
 *
 * ✅ autoExpire: نطلقه في الخلفية عند كل تحميل
 * ✅ إجراءات: useAmount, finish, discard
 */
@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val foodRepository: FoodRepository,
    private val shoppingRepository: ShoppingRepository,  // ⬅️ جديد لإعادة الشراء
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filter = MutableStateFlow(InventoryFilter.ALL)
    val filter: StateFlow<InventoryFilter> = _filter.asStateFlow()

    /**
     * حالة الواجهة — تجمع بين:
     *   - كل العناصر (من Room)
     *   - الفلتر
     *   - البحث
     *
     * ثم نفلتر القائمة داخل الـ lambda:
     *   - ALL: العناصر النشطة فقط (AVAILABLE + IN_USE)
     *   - EXPIRING: نشطة + expiry ≤ 3 أيام
     *   - EXPIRED: status = EXPIRED
     *   - HISTORY: status = FINISHED أو DISCARDED
     */
    val uiState: StateFlow<InventoryUiState> = combine(
        foodRepository.getAllAsStream(),
        _filter,
        _searchQuery,
    ) { allItems, filter, query ->
        // 🔄 Auto-expire: نطلقه في الخلفية
        launchAutoExpire(allItems)

        // فلترة حسب الـ status
        val now = System.currentTimeMillis()
        val soonThreshold = now + (5L * 24 * 60 * 60 * 1000)  // ⬅️ 5 أيام (مُحدَّث من 3)

        val baseList: List<FoodItem> = when (filter) {
            InventoryFilter.ALL -> {
                allItems.filter {
                    it.toStatus() == FoodItemStatus.AVAILABLE ||
                            it.toStatus() == FoodItemStatus.IN_USE
                }
            }
            InventoryFilter.EXPIRING -> {
                allItems.filter {
                    (it.toStatus() == FoodItemStatus.AVAILABLE ||
                            it.toStatus() == FoodItemStatus.IN_USE) &&
                            it.expiryTimestamp in now..soonThreshold
                }
            }
            InventoryFilter.EXPIRED -> {
                allItems.filter { it.toStatus() == FoodItemStatus.EXPIRED }
            }
            InventoryFilter.HISTORY -> {
                allItems.filter {
                    it.toStatus() == FoodItemStatus.FINISHED ||
                            it.toStatus() == FoodItemStatus.DISCARDED
                }
            }
        }

        // تطبيق البحث
        val searched = if (query.isBlank()) baseList else {
            baseList.filter { item ->
                item.foodId.contains(query, ignoreCase = true) ||
                        item.categoryId.contains(query, ignoreCase = true)
            }
        }

        // حساب حالة الصلاحية لكل عنصر
        val withExpiryStatus = searched.map { it to FoodExpiryStatus.fromExpiry(it.expiryTimestamp) }

        // حساب العدّادات
        val activeCount = allItems.count {
            it.toStatus() == FoodItemStatus.AVAILABLE ||
                    it.toStatus() == FoodItemStatus.IN_USE
        }
        val expiredCount = allItems.count { it.toStatus() == FoodItemStatus.EXPIRED }

        // ✅ attentionCount: العناصر التي تحتاج تنبيهاً فعلاً
        // = (نشطة + قاربت الانتهاء SOON) + (نشطة + منتهية EXPIRED) + (حالتها EXPIRED)
        val attentionCount = allItems.count { item ->
            val status = item.toStatus()
            val expiryStatus = FoodExpiryStatus.fromExpiry(item.expiryTimestamp, now)
            when {
                // الحالة EXPIRED (تم تحويلها تلقائياً)
                status == FoodItemStatus.EXPIRED -> true
                // نشطة لكن قاربت الانتهاء (SOON) أو انتهت فعلاً لكن لم تُحوّل بعد
                (status == FoodItemStatus.AVAILABLE || status == FoodItemStatus.IN_USE) &&
                        (expiryStatus == FoodExpiryStatus.SOON || expiryStatus == FoodExpiryStatus.EXPIRED) -> true
                else -> false
            }
        }

        InventoryUiState(
            items = withExpiryStatus,
            totalCount = allItems.size,
            activeCount = activeCount,
            expiredCount = expiredCount,
            attentionCount = attentionCount,  // ✅ العدد الصحيح
            historyCount = allItems.count {
                it.toStatus() == FoodItemStatus.FINISHED ||
                        it.toStatus() == FoodItemStatus.DISCARDED
            },
            filter = filter,
            searchQuery = query,
            isEmpty = allItems.isEmpty(),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = InventoryUiState(),
    )

    /**
     * Auto-Expire: تحويل العناصر المنتهية صلاحيتها ولم تُستهلك إلى EXPIRED
     * نطلقه في الـ background حتى لا يُؤخر عرض القائمة
     */
    private fun launchAutoExpire(allItems: List<FoodItem>) {
        val now = System.currentTimeMillis()
        val toExpire = allItems.any {
            it.expiryTimestamp < now &&
                    (it.toStatus() == FoodItemStatus.AVAILABLE || it.toStatus() == FoodItemStatus.IN_USE)
        }
        if (toExpire) {
            viewModelScope.launch {
                foodRepository.autoExpire()
            }
        }
    }

    fun onFilterChanged(newFilter: InventoryFilter) {
        _filter.value = newFilter
    }

    fun onSearchChanged(query: String) {
        _searchQuery.value = query
    }

    // ═══════════════════════════════════════════════════════
    //  ✅ إجراءات Use / Finish / Discard
    // ═══════════════════════════════════════════════════════

    fun useAmount(item: FoodItem, amount: Int) {
        viewModelScope.launch {
            foodRepository.useAmount(item, amount)
        }
    }

    fun finish(item: FoodItem) {
        viewModelScope.launch {
            foodRepository.finish(item)
        }
    }

    fun discard(item: FoodItem) {
        viewModelScope.launch {
            foodRepository.discard(item)
        }
    }

    // ═══════════════════════════════════════════════════════
    //  ✅ إجراءات إضافية: Edit / Repurchase / Delete
    // ═══════════════════════════════════════════════════════

    /**
     * Edit: تحديث بيانات الطعام (الكمية الأصلية، تاريخ الانتهاء، المكان)
     * نعيد ضبط consumedQuantity = 0 إذا تغيّرت الكمية الأصلية
     */
    fun edit(
        item: FoodItem,
        newQuantity: Int,
        newExpiryTimestamp: Long,
        newLocationId: String,
    ) {
        viewModelScope.launch {
            val updated = item.copy(
                quantity = newQuantity,
                expiryTimestamp = newExpiryTimestamp,
                locationId = newLocationId,
                consumedQuantity = 0,  // إعادة ضبط
                status = com.zerowaste.app.data.model.FoodItemStatus.AVAILABLE.name,
                actionTimestamp = System.currentTimeMillis(),
            )
            foodRepository.update(updated)
        }
    }

    /**
     * Repurchase: إضافة نفس الطعام إلى **قائمة التسوق** (وليس المخزون)
     *
     * ✅ المنطق الصحيح:
     *  - المستخدم انتهى من الطعam (FINISHED/DISCARDED/EXPIRED)
     *  - يريد شراءه مرة أخرى
     *  - نضيفه إلى قائمة التسوق حتى يتذكّره عند التسوّق
     *  - عندما يشتريه فعلاً، يضيفه يدوياً إلى المخزون عبر AddFoodScreen
     *
     * (سابقاً: كان يضيف إلى foodRepository → خطأ! الآن يضيف إلى shoppingRepository)
     */
    fun repurchase(item: FoodItem, quantity: Int = 1) {
        viewModelScope.launch {
            val category = item.toCategory()
            val unit = item.toUnit()
            shoppingRepository.addQuick(
                category = category,
                foodId = item.foodId,
                quantity = quantity,
                unit = unit,
            )
        }
    }

    /**
     * Delete: حذف نهائي من قاعدة البيانات
     * (مختلف عن Discard الذي يحتفظ بالسجل)
     */
    fun delete(item: FoodItem) {
        viewModelScope.launch {
            foodRepository.deleteById(item.id)
        }
    }

    // ── حالة الواجهة ──
    data class InventoryUiState(
        val items: List<Pair<FoodItem, FoodExpiryStatus>> = emptyList(),
        val totalCount: Int = 0,
        val activeCount: Int = 0,
        val expiredCount: Int = 0,
        val attentionCount: Int = 0,  // ✅ جديد: عناصر تحتاج تنبيهاً
        val historyCount: Int = 0,
        val filter: InventoryFilter = InventoryFilter.ALL,
        val searchQuery: String = "",
        val isEmpty: Boolean = true,
    )
}
