package dev.yichen.watertracker.notify

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import dev.yichen.watertracker.domain.model.Settings

class WaterReminderScheduler(private val context: Context) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun schedule(settings: Settings) {
        val pi = pendingIntent()
        alarmManager.cancel(pi)
        if (!settings.reminderEnabled) return
        val triggerMs = System.currentTimeMillis() + settings.reminderIntervalHours * 3_600_000L
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerMs, pi)
    }

    fun cancel() {
        alarmManager.cancel(pendingIntent())
    }

    private fun pendingIntent(): PendingIntent {
        val intent = Intent(context, ReminderReceiver::class.java)
        return PendingIntent.getBroadcast(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
}
