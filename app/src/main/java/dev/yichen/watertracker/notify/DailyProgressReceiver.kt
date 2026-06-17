package dev.yichen.watertracker.notify

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dev.yichen.watertracker.WaterTrackerApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DailyProgressReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val pending = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val repo = (context.applicationContext as WaterTrackerApp).container.repository
                val (totalMl, goalMl) = repo.getTodayStats()
                if (goalMl > 0 && totalMl < goalMl) {
                    val remaining = goalMl - totalMl
                    sendProgressNotif(context, totalMl, remaining)
                }
            } finally {
                pending.finish()
            }
        }
    }

    private fun sendProgressNotif(context: Context, totalMl: Int, remainingMl: Int) {
        if (!areNotificationsAllowed(context)) return
        ensureChannel(context)
        val notif = NotificationCompat.Builder(context, CHANNEL_PROGRESS)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle("💧 Stay hydrated!")
            .setContentText("${totalMl}ml done · still need ${remainingMl}ml today")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        try {
            NotificationManagerCompat.from(context).notify(NOTIF_ID_PROGRESS, notif)
        } catch (_: SecurityException) {}
    }
}
