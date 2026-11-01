package com.zerowaste.app.ui.screens.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zerowaste.app.data.model.ExpiryPreset
import com.zerowaste.app.data.model.FoodCategory
import com.zerowaste.app.data.model.FoodItem
import com.zerowaste.app.data.model.QuantityUnit
import com.zerowaste.app.data.model.StorageLocation
import com.zerowaste.app.data.repository.FoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

/**
 * AddFoodViewModel — حالة شاشة الإضافة
 *
 * كل الاختيارات يتم تخزينها في StateFlow:
 * - category: الفئة المختارة
 * - foodId: معرّف الطعام المختار من قائمة الفئة
 * - quantity: عدد الوحدات (1-999)
 * - unit: الوحدة
 * - expiryPreset: خيار تاريخ الانتهاء
 * - location: مكان التخزين
 *
 * ✅ لا يوجد أي Text Field — كل الاختيارات أيقونية
 * ✅ بعد الحفظ: يُدخل FoodItem فعلياً في قاعدة Room
 */
@HiltViewModel
class AddFoodViewModel @Inject constructor(
    private val foodRepository: FoodRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddFoodUiState())
    val uiState: StateFlow<AddFoodUiState> = _uiState.asStateFlow()

    // ── اختيار الفئة ──
    fun onCategorySelected(category: FoodCategory) {
        // ✅ اقتراح ذكي: نضبط مكان التخزين + تاريخ الصلاحية الافتراضي
        val smartExpiry = ExpiryPreset.entries.firstOrNull { it.daysFromNow == category.defaultExpiryDays }
            ?: ExpiryPreset.ONE_WEEK

        _uiState.update {
            it.copy(
                category = category,
                foodId = null,
                location = category.defaultLocation,        // ⭐ اقتراح ذكي للمكان
                expiryPreset = smartExpiry,                  // ⭐ اقتراح ذكي للصلاحية
            )
        }
    }

    // ── اختيار الطعام ──
    fun onFoodSelected(foodId: String) {
        _uiState.update { it.copy(foodId = foodId) }
    }

    // ── الكمية: زيادة / نقص ──
    fun onQuantityIncrease() {
        _uiState.update {
            if (it.quantity < 999) it.copy(quantity = it.quantity + 1) else it
        }
    }

    fun onQuantityDecrease() {
        _uiState.update {
            if (it.quantity > 1) it.copy(quantity = it.quantity - 1) else it
        }
    }

    fun onQuantityReset() {
        _uiState.update { it.copy(quantity = 1) }
    }

    // ✅ اختيار سريع للكمية (شيبس بالأرقام بدل +/- فقط)
    fun onQuantitySelected(value: Int) {
        _uiState.update {
            if (value in 1..999) it.copy(quantity = value) else it
        }
    }

    // ── اختيار الوحدة ──
    fun onUnitSelected(unit: QuantityUnit) {
        _uiState.update { it.copy(unit = unit) }
    }

    // ── اختيار تاريخ الانتهاء ──
    fun onExpiryPresetSelected(preset: ExpiryPreset) {
        _uiState.update { it.copy(expiryPreset = preset) }
    }

    // ── اختيار مكان التخزين ──
    fun onLocationSelected(location: StorageLocation) {
        _uiState.update { it.copy(location = location) }
    }

    // ── الحفظ الفعلي في Room ──
    fun saveFood(onSaved: () -> Unit) {
        val state = _uiState.value
        val foodId = state.foodId ?: return
        val food = state.category.commonFoods.firstOrNull { it.id == foodId } ?: return

        viewModelScope.launch {
            val expiryTimestamp = calculateExpiryTimestamp(state.expiryPreset)
            val foodItem = FoodItem(
                categoryId = state.category.name,
                foodId = food.id,
                foodLabelResId = food.labelResId,
                quantity = state.quantity,
                unitId = state.unit.name,
                expiryTimestamp = expiryTimestamp,
                locationId = state.location.name,
            )
            foodRepository.insert(foodItem)

            // إعادة ضبط الحالة بعد النجاح
            _uiState.value = AddFoodUiState()
            onSaved()
        }
    }

    /**
     * ✅ حفظ + إضافة آخر: يحفظ الطعam ثم يُعيد ضبط الحالة
     * لكن بدل الرجوع للمخزون، يبقى في شاشة الإضافة
     * يحتفظ بنفس الفئة (لتسهيل إضافة عناصر متشابهة)
     */
    fun saveAndAddAnother(onSaved: () -> Unit) {
        val state = _uiState.value
        val foodId = state.foodId ?: return
        val food = state.category.commonFoods.firstOrNull { it.id == foodId } ?: return

        viewModelScope.launch {
            val expiryTimestamp = calculateExpiryTimestamp(state.expiryPreset)
            val foodItem = FoodItem(
                categoryId = state.category.name,
                foodId = food.id,
                foodLabelResId = food.labelResId,
                quantity = state.quantity,
                unitId = state.unit.name,
                expiryTimestamp = expiryTimestamp,
                locationId = state.location.name,
            )
            foodRepository.insert(foodItem)

            // ✅ إعادة ضبط لكن نحتفظ بنفس الفئة + اقتراحاتها الذكية
            val savedCategory = state.category
            val smartExpiry = ExpiryPreset.entries.firstOrNull { it.daysFromNow == savedCategory.defaultExpiryDays }
                ?: ExpiryPreset.ONE_WEEK
            _uiState.value = AddFoodUiState(
                category = savedCategory,
                location = savedCategory.defaultLocation,
                expiryPreset = smartExpiry,
            )
            onSaved()
        }
    }

    /**
     * حساب timestamp لتاريخ الانتهاء بناء على preset
     * مثلاً: ONE_WEEK → now + 7 days
     */
    private fun calculateExpiryTimestamp(preset: ExpiryPreset): Long {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, preset.daysFromNow)
        // ضبط الوقت إلى الساعة 23:59:59 من نفس اليوم
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    // ── حالة الواجهة ──
    data class AddFoodUiState(
        val category: FoodCategory = FoodCategory.default,
        val foodId: String? = null,
        val quantity: Int = 1,
        val unit: QuantityUnit = QuantityUnit.default,
        val expiryPreset: ExpiryPreset = ExpiryPreset.default,
        val location: StorageLocation = StorageLocation.default,
        val isSaving: Boolean = false,
        val errorMessage: String? = null,
    ) {
        val isComplete: Boolean get() = foodId != null
    }
}
