package com.zerowaste.app.ui.components

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zerowaste.app.data.repository.FoodRepository
import com.zerowaste.app.ui.navigation.BottomTab
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * BadgesViewModel — يحسب البادجات الحقيقية لكل تبويب
 *
 * - Inventory: عدد الأطعمة التي قاربت الانتهاء
 * - Add: 0 دائماً (ليس له بادج)
 * - Recipes / Shopping / Settings: 0 (ستُربط لاحقاً)
 */
@HiltViewModel
class BadgesViewModel @Inject constructor(
    private val foodRepository: FoodRepository,
) : ViewModel() {

    /**
     * حالة البادجات - تتحدّث تلقائياً عند تغيير Room
     */
    val badges: StateFlow<Map<BottomTab, Int>> = combine(
        foodRepository.countAll(),
        foodRepository.countExpiring(System.currentTimeMillis() + (3L * 24 * 60 * 60 * 1000)),
        foodRepository.countExpired(System.currentTimeMillis()),
    ) { total, expiring, expired ->
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
