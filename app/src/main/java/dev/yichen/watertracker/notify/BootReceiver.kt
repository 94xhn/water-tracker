package dev.yichen.watertracker.notify

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dev.yichen.watertracker.WaterTrackerApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        val pending = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val app = context.applicationContext as WaterTrackerApp
                val settings = app.container.repository.settings.first()
                app.container.reminderScheduler.schedule(settings)
            } finally {
                pending.finish()
            }
        }
    }
}
