package com.zerowaste.app.ui.notifications

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * NotificationScheduler — يجدول التنبيهات اليومية عبر WorkManager
 *
 * يُجدول:
 *  - فحص يومي للمخزون في وقت محدد (افتراضي: 9:00 صباحاً)
 *  - يُعاد جدولته عند تغيير الإعدادات
 *  - يُعاد جدولته بعد إقلاع الجهاز (BootReceiver)
 */
object NotificationScheduler {

    private const val WORK_NAME = "daily_expiry_check"

    /**
     * جدولة الفحص اليومي
     *
     * @param hour ساعة اليوم (0-23)
     * @param minute الدقيقة (0-59)
     */
    fun scheduleDailyCheck(context: Context, hour: Int = 9, minute: Int = 0) {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            // إذا الوقت المحدد قد فات اليوم، نجدول للغد
            if (before(now)) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val delayMillis = target.timeInMillis - now.timeInMillis
        val delayMinutes = TimeUnit.MILLISECONDS.toMinutes(delayMillis)

        val request = PeriodicWorkRequestBuilder<ExpiryNotificationWorker>(
            1, TimeUnit.DAYS  // يتكرر يومياً
        )
            .setInitialDelay(delayMinutes, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,  // استبدال أي جدولة سابقة
            request
        )
    }

    /**
     * إيقاف الجدولة (عند تعطيل التنبيهات)
     */
    fun cancelDailyCheck(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }

    /**
     * إعادة الجدولة بإعدادات جديدة
     */
    fun reschedule(context: Context, hour: Int, minute: Int) {
        scheduleDailyCheck(context, hour, minute)
    }
}
