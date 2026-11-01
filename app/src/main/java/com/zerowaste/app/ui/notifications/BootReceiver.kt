package com.zerowaste.app.ui.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.zerowaste.app.data.repository.SettingsRepository

/**
 * BootReceiver — يعيد جدولة التنبيهات بعد إقلاع الجهاز
 *
 * عند إعادة تشغيل الجهاز، كل مهام WorkManager تُلغى.
 * هذا المستقبل يعيد جدولتها.
 *
 * (مُسجّل في AndroidManifest.xml)
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == Intent.ACTION_MY_PACKAGE_REPLACED) {

            val settings = SettingsRepository(context)

            // إذا التنبيهات مفعّلة، نعيد الجدولة
            if (settings.areNotificationsEnabled()) {
                val (hour, minute) = settings.getDailyReminderTime()
                NotificationScheduler.scheduleDailyCheck(context, hour, minute)
            }
        }
    }
}
