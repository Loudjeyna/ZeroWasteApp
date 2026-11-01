package com.zerowaste.app.ui.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.zerowaste.app.MainActivity
import com.zerowaste.app.R
import com.zerowaste.app.ZeroWasteApp

/**
 * NotificationHelper — مساعد بناء وإرسال التنبيهات
 *
 * 3 أنواع تنبيهات:
 *  - 🔴 حرج: طعام تجاوز تاريخ الصلاحية (CHANNEL_CRITICAL)
 *  - 🟡 تحذير: طعام قارب على الانتهاء (CHANNEL_WARNING)
 *  - 🔵 معلومة: تذكير يومي (CHANNEL_INFO)
 */
object NotificationHelper {

    /**
     * إرسال تنبيه حرج (طعام منتهي)
     */
    fun sendCriticalNotification(
        context: Context,
        title: String,
        message: String,
        vibration: Boolean = true,
    ) {
        sendNotification(
            context = context,
            channelId = ZeroWasteApp.CHANNEL_CRITICAL,
            title = title,
            message = message,
            iconResId = R.drawable.ic_launcher_foreground,
            vibration = vibration,
            highPriority = true,
        )
    }

    /**
     * إرسال تنبيه تحذير (قرب الانتهاء)
     */
    fun sendWarningNotification(
        context: Context,
        title: String,
        message: String,
        vibration: Boolean = true,
    ) {
        sendNotification(
            context = context,
            channelId = ZeroWasteApp.CHANNEL_WARNING,
            title = title,
            message = message,
            iconResId = R.drawable.ic_launcher_foreground,
            vibration = vibration,
            highPriority = false,
        )
    }

    /**
     * إرسال تذكير يومي
     */
    fun sendDailyReminder(
        context: Context,
        title: String,
        message: String,
    ) {
        sendNotification(
            context = context,
            channelId = ZeroWasteApp.CHANNEL_INFO,
            title = title,
            message = message,
            iconResId = R.drawable.ic_launcher_foreground,
            vibration = false,
            highPriority = false,
        )
    }

    /**
     * بناء وإرسال التنبيه
     */
    private fun sendNotification(
        context: Context,
        channelId: String,
        title: String,
        message: String,
        iconResId: Int,
        vibration: Boolean,
        highPriority: Boolean,
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(iconResId)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(if (highPriority) NotificationCompat.PRIORITY_HIGH else NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        if (vibration) {
            builder.setVibrate(longArrayOf(0, 500, 250, 500))
        } else {
            builder.setVibrate(longArrayOf(0L))
        }

        val notificationId = System.currentTimeMillis().toInt()
        try {
            NotificationManagerCompat.from(context).notify(notificationId, builder.build())
        } catch (e: SecurityException) {
            // POST_NOTIFICATIONS permission غير ممنوح
        }
    }
}
