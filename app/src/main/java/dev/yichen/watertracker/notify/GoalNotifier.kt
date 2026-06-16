package dev.yichen.watertracker.notify

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GoalNotifier(private val context: Context) {
    private val prefs = context.getSharedPreferences("water_goal_prefs", Context.MODE_PRIVATE)

    fun notifyIfNeeded() {
        if (!areNotificationsAllowed(context)) return
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        if (prefs.getString("notified_day", "") == today) return
        prefs.edit().putString("notified_day", today).apply()

        val notification = NotificationCompat.Builder(context, CHANNEL_GOAL)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("🎉 Daily goal reached!")
            .setContentText("Great job! You've hit your water goal for today.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIF_ID_GOAL, notification)
        } catch (_: SecurityException) {}
    }
}
