package dev.yichen.watertracker.notify

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import dev.yichen.watertracker.domain.model.Settings
import java.util.Calendar

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

    fun scheduleDailyProgress() {
        val pi = progressPendingIntent()
        alarmManager.cancel(pi)

        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 20)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) add(Calendar.DAY_OF_YEAR, 1)
        }
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            cal.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pi
        )
    }

    private fun pendingIntent(): PendingIntent {
        val intent = Intent(context, ReminderReceiver::class.java)
        return PendingIntent.getBroadcast(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun progressPendingIntent(): PendingIntent {
        val intent = Intent(context, DailyProgressReceiver::class.java)
        return PendingIntent.getBroadcast(
            context, 100, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
}
