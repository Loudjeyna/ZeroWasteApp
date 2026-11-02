package com.zerowaste.app.data.repository

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * SettingsRepository — إدارة كل إعدادات التطبيق
 *
 * يستعمل SharedPreferences للحفظ
 *
 * ✅ محدّث: لا يستعمل AppCompatDelegate (لا حاجة لمكتبة appcompat)
 */
@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // ═══════════════════════════════════════════════════════
    //  Language
    // ═══════════════════════════════════════════════════════

    fun getLanguage(): AppLanguage {
        val value = prefs.getString(KEY_LANGUAGE, AppLanguage.SYSTEM.code) ?: AppLanguage.SYSTEM.code
        return AppLanguage.fromCode(value)
    }

    fun setLanguage(language: AppLanguage) {
        prefs.edit().putString(KEY_LANGUAGE, language.code).apply()
    }

    // ═══════════════════════════════════════════════════════
    //  Theme (Dark/Light) — بدون AppCompatDelegate
    // ═══════════════════════════════════════════════════════

    fun getThemeMode(): AppThemeMode {
        val value = prefs.getString(KEY_THEME, AppThemeMode.SYSTEM.code) ?: AppThemeMode.SYSTEM.code
        return AppThemeMode.fromCode(value)
    }

    fun setThemeMode(mode: AppThemeMode) {
        prefs.edit().putString(KEY_THEME, mode.code).apply()
    }

    /**
     * هل الوضع الليلي مفعّل؟
     * - SYSTEM → نعيد null (يعني "حسب النظام")
     * - LIGHT → false
     * - DARK → true
     *
     * يُستعمل في MainActivity لتمريره للـ Theme
     */
    fun isDarkTheme(): Boolean? {
        return when (getThemeMode()) {
            AppThemeMode.LIGHT -> false
            AppThemeMode.DARK -> true
            AppThemeMode.SYSTEM -> null  // حسب النظام
        }
    }

    // ═══════════════════════════════════════════════════════
    //  Material You (Dynamic Color)
    // ═══════════════════════════════════════════════════════

    fun isDynamicColorEnabled(): Boolean {
        return prefs.getBoolean(KEY_DYNAMIC_COLOR, true)
    }

    fun setDynamicColorEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DYNAMIC_COLOR, enabled).apply()
    }

    // ═══════════════════════════════════════════════════════
    //  Notifications
    // ═══════════════════════════════════════════════════════

    fun areNotificationsEnabled(): Boolean {
        return prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, false)  // ✅ معطّل افتراضياً
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled).apply()
    }

    fun getNotifyDaysBefore(): Int {
        return prefs.getInt(KEY_NOTIFY_DAYS_BEFORE, 5)
    }

    fun setNotifyDaysBefore(days: Int) {
        prefs.edit().putInt(KEY_NOTIFY_DAYS_BEFORE, days).apply()
    }

    fun getDailyReminderTime(): Pair<Int, Int> {
        val hour = prefs.getInt(KEY_DAILY_REMINDER_HOUR, 9)
        val minute = prefs.getInt(KEY_DAILY_REMINDER_MINUTE, 0)
        return hour to minute
    }

    fun setDailyReminderTime(hour: Int, minute: Int) {
        prefs.edit()
            .putInt(KEY_DAILY_REMINDER_HOUR, hour)
            .putInt(KEY_DAILY_REMINDER_MINUTE, minute)
            .apply()
    }

    fun isVibrationEnabled(): Boolean {
        return prefs.getBoolean(KEY_VIBRATION, true)
    }

    fun setVibrationEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_VIBRATION, enabled).apply()
    }

    companion object {
        private const val PREFS_NAME = "zerowaste_settings"

        private const val KEY_LANGUAGE = "language"
        private const val KEY_THEME = "theme_mode"
        private const val KEY_DYNAMIC_COLOR = "dynamic_color"
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
        private const val KEY_NOTIFY_DAYS_BEFORE = "notify_days_before"
        private const val KEY_DAILY_REMINDER_HOUR = "daily_reminder_hour"
        private const val KEY_DAILY_REMINDER_MINUTE = "daily_reminder_minute"
        private const val KEY_VIBRATION = "vibration"
    }
}

/**
 * اللغة المعتمدة
 */
enum class AppLanguage(val code: String, val labelResId: Int) {
    SYSTEM("system", com.zerowaste.app.R.string.settings_language_system),
    ARABIC("ar", com.zerowaste.app.R.string.settings_language_ar),
    ENGLISH("en", com.zerowaste.app.R.string.settings_language_en),
    FRENCH("fr", com.zerowaste.app.R.string.settings_language_fr);

    companion object {
        fun fromCode(code: String): AppLanguage =
            values().firstOrNull { it.code == code } ?: SYSTEM
    }
}

/**
 * وضع الثيم
 */
enum class AppThemeMode(val code: String, val labelResId: Int) {
    SYSTEM("system", com.zerowaste.app.R.string.settings_theme_system),
    LIGHT("light", com.zerowaste.app.R.string.settings_theme_light),
    DARK("dark", com.zerowaste.app.R.string.settings_theme_dark);

    companion object {
        fun fromCode(code: String): AppThemeMode =
            values().firstOrNull { it.code == code } ?: SYSTEM
    }
}
