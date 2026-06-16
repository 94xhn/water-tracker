package dev.yichen.watertracker.notify

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dev.yichen.watertracker.R
import dev.yichen.watertracker.WaterTrackerApp
import dev.yichen.watertracker.ui.MainActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.Calendar

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val app = context.applicationContext as WaterTrackerApp
        val settings = runBlocking { app.container.repository.settings.first() }

        // Always reschedule next alarm first
        app.container.reminderScheduler.schedule(settings)

        if (!settings.reminderEnabled) return

        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        if (hour < settings.reminderStartHour || hour >= settings.reminderEndHour) return

        ensureChannel(context)
        if (!areNotificationsAllowed(context)) return

        val launchIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pi = PendingIntent.getActivity(context, 0, launchIntent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(context, CHANNEL_REMINDER)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Time to drink water!")
            .setContentText("Stay hydrated — tap to log your drink")
            .setContentIntent(pi)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(1, notification)
    }
}
