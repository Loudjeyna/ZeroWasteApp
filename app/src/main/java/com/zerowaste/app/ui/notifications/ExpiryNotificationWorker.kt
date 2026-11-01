package com.zerowaste.app.ui.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.zerowaste.app.data.db.ZeroWasteDatabase
import com.zerowaste.app.data.model.FoodItemStatus
import com.zerowaste.app.data.repository.SettingsRepository
import com.zerowaste.app.util.NumberUtils

/**
 * ExpiryNotificationWorker — Worker يفحص المخزون ويرسل تنبيهات
 *
 * يُجدول يومياً عبر WorkManager (NotificationScheduler)
 *
 * المنطق:
 *  1. يقرأ الإعدادات (هل التنبيهات مفعّلة؟ كم يوم قبل؟ اهتزاز؟)
 *  2. يقرأ كل الأطعمة من Room
 *  3. يفصل: منتهي / قريب الانتهاء
 *  4. يرسل تنبيهات مناسبة لكل حالة
 */
class ExpiryNotificationWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val context = applicationContext

        // ── قراءة الإعدادات ──
        val settings = SettingsRepository(context)
        if (!settings.areNotificationsEnabled()) {
            return Result.success()
        }

        val daysBefore = settings.getNotifyDaysBefore()
        val vibration = settings.isVibrationEnabled()

        // ── قراءة المخزون ──
        val db = ZeroWasteDatabase.getInstance(context)
        val allItems = db.foodItemDao().getAll()

        if (allItems.isEmpty()) return Result.success()

        val now = System.currentTimeMillis()
        val threshold = now + (daysBefore.toLong() * 24 * 60 * 60 * 1000)

        // ── تصنيف الأطعمة ──
        val expiredItems = allItems.filter {
            (it.toStatus() == FoodItemStatus.AVAILABLE || it.toStatus() == FoodItemStatus.IN_USE) &&
            it.expiryTimestamp < now
        }

        val expiringSoonItems = allItems.filter {
            (it.toStatus() == FoodItemStatus.AVAILABLE || it.toStatus() == FoodItemStatus.IN_USE) &&
            it.expiryTimestamp in now..threshold
        }

        // ── إرسال التنبيهات ──

        // 🔴 تنبيه حرج: طعام منتهي
        if (expiredItems.isNotEmpty()) {
            val names = expiredItems.take(3).joinToString("، ") {
                context.getString(it.foodLabelResId)
            }
            val extra = if (expiredItems.size > 3) " +${NumberUtils.formatInt(expiredItems.size - 3)}" else ""
            NotificationHelper.sendCriticalNotification(
                context = context,
                title = "🔴 طعام منتهي الصلاحية",
                message = "${NumberUtils.formatInt(expiredItems.size)} عنصر تجاوز تاريخه:\n$names$extra",
                vibration = vibration,
            )
        }

        // 🟡 تنبيه تحذير: قرب الانتهاء
        if (expiringSoonItems.isNotEmpty()) {
            val names = expiringSoonItems.take(3).joinToString("، ") {
                context.getString(it.foodLabelResId)
            }
            val extra = if (expiringSoonItems.size > 3) " +${NumberUtils.formatInt(expiringSoonItems.size - 3)}" else ""
            NotificationHelper.sendWarningNotification(
                context = context,
                title = "🟡 قرب انتهاء الصلاحية",
                message = "${NumberUtils.formatInt(expiringSoonItems.size)} عنصر سينتهي خلال $daysBefore أيام:\n$names$extra",
                vibration = vibration,
            )
        }

        // 🔵 تذكير يومي (فقط إذا لا يوجد منتهي أو قارب — لتجنّب الإزعاج)
        if (expiredItems.isEmpty() && expiringSoonItems.isEmpty()) {
            val activeCount = allItems.count {
                it.toStatus() == FoodItemStatus.AVAILABLE || it.toStatus() == FoodItemStatus.IN_USE
            }
            if (activeCount > 0) {
                NotificationHelper.sendDailyReminder(
                    context = context,
                    title = "🌱 قلل الهدر",
                    message = "كل شيء على ما يرام! لديك $activeCount عنصر في المخزون.",
                )
            }
        }

        return Result.success()
    }
}
