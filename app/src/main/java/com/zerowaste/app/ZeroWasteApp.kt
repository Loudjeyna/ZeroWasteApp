package com.zerowaste.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import dagger.hilt.android.HiltAndroidApp
import com.zerowaste.app.ui.notifications.NotificationScheduler
import java.util.Locale

/**
 * ZeroWasteApp — Application Class
 * - مُسيَّر بواسطة Hilt
 * - ينشئ Notification Channels للتنبيهات
 * - يطبّق اللغة المحفوظة عند بدء التطبيق
 * - ✅ يجدول التنبيهات اليومية
 */
@HiltAndroidApp
class ZeroWasteApp : Application() {

    override fun onCreate() {
        super.onCreate()
        applySavedLanguage()
        createNotificationChannels()
        scheduleNotifications()  // ✅ جدولة التنبيهات
    }

    /**
     * تطبيق اللغة المحفوظة عند بدء التطبيق
     * نقرأ SharedPreferences مباشرة (بدون Hilt)
     *
     * ✅ لا يستعمل AppCompatDelegate — فقط Configuration
     */
    private fun applySavedLanguage() {
        val prefs = getSharedPreferences(SETTINGS_PREFS_NAME, Context.MODE_PRIVATE)
        val languageCode = prefs.getString(KEY_LANGUAGE, "system") ?: "system"

        if (languageCode != "system") {
            val locale = Locale(languageCode)
            Locale.setDefault(locale)
            val config = resources.configuration
            config.setLocale(locale)
            @Suppress("DEPRECATION")
            resources.updateConfiguration(config, resources.displayMetrics)
        }
    }

    /**
     * ✅ جدولة التنبيهات اليومية عند بدء التطبيق
     * يقرأ الإعدادات ويفدول WorkManager
     */
    private fun scheduleNotifications() {
        val prefs = getSharedPreferences(SETTINGS_PREFS_NAME, Context.MODE_PRIVATE)
        val notificationsEnabled = prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, false)  // ✅ معطّل افتراضياً

        if (notificationsEnabled) {
            val hour = prefs.getInt(KEY_DAILY_REMINDER_HOUR, 9)
            val minute = prefs.getInt(KEY_DAILY_REMINDER_MINUTE, 0)
            NotificationScheduler.scheduleDailyCheck(this, hour, minute)
        }
    }

    /**
     * إنشاء قنوات التنبيهات (مطلوبة على Android 8+)
     */
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            manager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_CRITICAL,
                    getString(R.string.channel_critical_name),
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = getString(R.string.channel_critical_desc)
                    enableVibration(true)
                    enableLights(true)
                    vibrationPattern = longArrayOf(0, 500, 250, 500)
                }
            )

            manager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_WARNING,
                    getString(R.string.channel_warning_name),
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = getString(R.string.channel_warning_desc)
                    enableVibration(true)
                }
            )

            manager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_INFO,
                    getString(R.string.channel_info_name),
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = getString(R.string.channel_info_desc)
                }
            )
        }
    }

    companion object {
        const val CHANNEL_CRITICAL = "channel_critical"
        const val CHANNEL_WARNING = "channel_warning"
        const val CHANNEL_INFO = "channel_info"

        private const val SETTINGS_PREFS_NAME = "zerowaste_settings"
        private const val KEY_LANGUAGE = "language"
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
        private const val KEY_DAILY_REMINDER_HOUR = "daily_reminder_hour"
        private const val KEY_DAILY_REMINDER_MINUTE = "daily_reminder_minute"
    }
}
