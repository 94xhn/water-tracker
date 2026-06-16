package dev.yichen.watertracker.notify

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationManagerCompat

const val CHANNEL_REMINDER = "water_reminder"

fun ensureChannel(context: Context) {
    val mgr = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    if (mgr.getNotificationChannel(CHANNEL_REMINDER) != null) return
    val ch = NotificationChannel(
        CHANNEL_REMINDER,
        "Drink Water Reminders",
        NotificationManager.IMPORTANCE_DEFAULT
    )
    ch.description = "Periodic reminders to drink water"
    mgr.createNotificationChannel(ch)
}

fun areNotificationsAllowed(context: Context): Boolean =
    NotificationManagerCompat.from(context).areNotificationsEnabled()
