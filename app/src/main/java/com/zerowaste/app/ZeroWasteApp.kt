package com.zerowaste.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
//import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.HiltAndroidApp

/**
 * ZeroWasteApp — Application Class
 * - مُسيَّر بواسطة Hilt
 * - ينشئ Notification Channels للتنبيهات
 * - يُفعّل دعم RTL الكامل
 */
@HiltAndroidApp
class ZeroWasteApp : Application() {

    override fun onCreate() {
        super.onCreate()
        // إعداد RTL افتراضي للعربية
        applyDefaultLocale()
        // إنشاء قنوات التنبيهات
        createNotificationChannels()
    }

    /**
     * ضبط العربية كلغة افتراضية + RTL
     */
    private fun applyDefaultLocale() {
        val locale = java.util.Locale("ar")
        java.util.Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        // RTL دائم
       // config.layoutDirection = android.view.View.LAYOUT_DIRECTION_RTL
        @Suppress("DEPRECATION")
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    /**
     * إنشاء قنوات التنبيهات (مطلوبة على Android 8+)
     * - قناة حرجة: طعام تجاوز الصلاحية
     * - قناة تحذير: قرب انتهاء الصلاحية
     * - قناة معلومات: تذكير يومي
     */
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // 1. قناة حرجة (طعام تجاوز الصلاحية)
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

            // 2. قناة تحذير (قرب الانتهاء)
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

            // 3. قناة معلومات (تذكير يومي)
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
    }
}
