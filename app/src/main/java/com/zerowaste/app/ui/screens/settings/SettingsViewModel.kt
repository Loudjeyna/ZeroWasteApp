package com.zerowaste.app.ui.screens.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zerowaste.app.data.repository.AppLanguage
import com.zerowaste.app.data.repository.AppThemeMode
import com.zerowaste.app.data.repository.FoodRepository
import com.zerowaste.app.data.repository.SettingsRepository
import com.zerowaste.app.data.repository.ShoppingRepository
import com.zerowaste.app.ui.notifications.NotificationScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

/**
 * SettingsViewModel — حالة شاشة الإعدادات
 *
 * ✅ محدّث: لا يستعمل AppCompatDelegate
 *  - اللغة: استعمال Configuration.setLocale() (مدمج في Android SDK)
 *  - الوضع الليلي: يحفظ الإعداد فقط، MainActivity يقرأه ويمرره للـ Theme
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val settingsRepository: SettingsRepository,
    private val foodRepository: FoodRepository,
    private val shoppingRepository: ShoppingRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        refreshState()
    }

    private fun refreshState() {
        _uiState.update {
            it.copy(
                language = settingsRepository.getLanguage(),
                themeMode = settingsRepository.getThemeMode(),
                isDynamicColorEnabled = settingsRepository.isDynamicColorEnabled(),
                areNotificationsEnabled = settingsRepository.areNotificationsEnabled(),
                notifyDaysBefore = settingsRepository.getNotifyDaysBefore(),
                dailyReminderTime = settingsRepository.getDailyReminderTime(),
                isVibrationEnabled = settingsRepository.isVibrationEnabled(),
                darkThemeOverride = settingsRepository.isDarkTheme(),
            )
        }
    }

    // ═══════════════════════════════════════════════════════
    //  Language — استعمال Configuration (بدون AppCompat)
    // ═══════════════════════════════════════════════════════

    fun setLanguage(language: AppLanguage) {
        settingsRepository.setLanguage(language)
        applyLanguage(language)
        refreshState()
    }

    /**
     * تطبيق اللغة فعلياً باستخدام Configuration
     * (لا تحتاج AppCompatDelegate)
     */
    private fun applyLanguage(language: AppLanguage) {
        if (language == AppLanguage.SYSTEM) {
            // إعادة للنظام - نترك الإعداد الافتراضي
            return
        }
        val locale = Locale(language.code)
        Locale.setDefault(locale)
        val config = appContext.resources.configuration
        config.setLocale(locale)
        @Suppress("DEPRECATION")
        appContext.resources.updateConfiguration(config, appContext.resources.displayMetrics)
    }

    // ═══════════════════════════════════════════════════════
    //  Theme — حفظ فقط، MainActivity يطبّقه
    // ═══════════════════════════════════════════════════════

    fun setThemeMode(mode: AppThemeMode) {
        settingsRepository.setThemeMode(mode)
        refreshState()
    }

    fun setDynamicColorEnabled(enabled: Boolean) {
        settingsRepository.setDynamicColorEnabled(enabled)
        refreshState()
    }

    // ═══════════════════════════════════════════════════════
    //  Notifications
    // ═══════════════════════════════════════════════════════

    fun setNotificationsEnabled(enabled: Boolean) {
        settingsRepository.setNotificationsEnabled(enabled)
        // ✅ إعادة جدولة التنبيهات
        if (enabled) {
            val (hour, minute) = settingsRepository.getDailyReminderTime()
            NotificationScheduler.scheduleDailyCheck(appContext, hour, minute)
        } else {
            NotificationScheduler.cancelDailyCheck(appContext)
        }
        refreshState()
    }

    fun setNotifyDaysBefore(days: Int) {
        settingsRepository.setNotifyDaysBefore(days)
        refreshState()
    }

    fun setDailyReminderTime(hour: Int, minute: Int) {
        settingsRepository.setDailyReminderTime(hour, minute)
        // ✅ إعادة جدولة بالوقت الجديد
        if (settingsRepository.areNotificationsEnabled()) {
            NotificationScheduler.reschedule(appContext, hour, minute)
        }
        refreshState()
    }

    fun setVibrationEnabled(enabled: Boolean) {
        settingsRepository.setVibrationEnabled(enabled)
        refreshState()
    }

    // ═══════════════════════════════════════════════════════
    //  Danger Zone: Delete all data
    // ═══════════════════════════════════════════════════════

    fun deleteAllData() {
        viewModelScope.launch {
            foodRepository.deleteAll()
            shoppingRepository.deleteAll()
        }
    }

    // ═══════════════════════════════════════════════════════
    //  حالة الواجهة
    // ═══════════════════════════════════════════════════════

    data class SettingsUiState(
        val language: AppLanguage = AppLanguage.SYSTEM,
        val themeMode: AppThemeMode = AppThemeMode.SYSTEM,
        val isDynamicColorEnabled: Boolean = true,
        val areNotificationsEnabled: Boolean = true,
        val notifyDaysBefore: Int = 5,
        val dailyReminderTime: Pair<Int, Int> = 9 to 0,
        val isVibrationEnabled: Boolean = true,
        // ✅ darkThemeOverride: null = حسب النظام، true = داكن، false = فاتح
        val darkThemeOverride: Boolean? = null,
    )
}
