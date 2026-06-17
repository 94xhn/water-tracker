package dev.yichen.watertracker.notify

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationManagerCompat

const val CHANNEL_REMINDER = "water_reminder"
const val CHANNEL_GOAL = "goal_achieved"
const val CHANNEL_PROGRESS = "daily_progress"
const val NOTIF_ID_GOAL = 1001
const val NOTIF_ID_PROGRESS = 1002

fun ensureChannel(context: Context) {
    val mgr = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    if (mgr.getNotificationChannel(CHANNEL_REMINDER) == null) {
        mgr.createNotificationChannel(
            NotificationChannel(CHANNEL_REMINDER, "Drink Water Reminders", NotificationManager.IMPORTANCE_DEFAULT)
                .also { it.description = "Periodic reminders to drink water" }
        )
    }
    if (mgr.getNotificationChannel(CHANNEL_GOAL) == null) {
        mgr.createNotificationChannel(
            NotificationChannel(CHANNEL_GOAL, "Goal Achieved", NotificationManager.IMPORTANCE_DEFAULT)
                .also { it.description = "Celebration when daily goal is reached" }
        )
    }
    if (mgr.getNotificationChannel(CHANNEL_PROGRESS) == null) {
        mgr.createNotificationChannel(
            NotificationChannel(CHANNEL_PROGRESS, "Daily Progress Reminder", NotificationManager.IMPORTANCE_DEFAULT)
                .also { it.description = "Evening nudge to finish your daily goal" }
        )
    }
}

fun areNotificationsAllowed(context: Context): Boolean =
    NotificationManagerCompat.from(context).areNotificationsEnabled()
