package com.zerowaste.app.ui.components

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zerowaste.app.data.repository.FoodRepository
import com.zerowaste.app.ui.navigation.BottomTab
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * BadgesViewModel — يحسب البادجات الحقيقية لكل تبويب
 *
 * - Inventory: عدد العناصر التي تحتاج انتباهاً (قاربت الانتهاء + منتهية)
 * - Add: 0 دائماً
 * - Recipes / Shopping / Settings: 0 (ستُربط لاحقاً)
 */
@HiltViewModel
class BadgesViewModel @Inject constructor(
    private val foodRepository: FoodRepository,
) : ViewModel() {

    /**
     * حالة البادجات - تتحدّث تلقائياً عند تغيير Room
     *
     * ✅ محدّث (D-fix):
     *  - نستعمل countExpiring() (بدون معاملات) - يأخذ الافتراضي 3 أيام
     *  - نستعمل countExpired() (بدون معاملات)
     */
    val badges: StateFlow<Map<BottomTab, Int>> = combine(
        foodRepository.countExpiring(),
        foodRepository.countExpired(),
    ) { expiring, expired ->
        // بادج المخزون = (قارب الانتهاء + منتهي) — يدل على ما يستوجب الانتباه
        mapOf(
            BottomTab.INVENTORY to (expiring + expired),
            BottomTab.RECIPES to 0,
            BottomTab.ADD to 0,
            BottomTab.SHOPPING to 0,
            BottomTab.SETTINGS to 0,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = mapOf(),
    )
}
