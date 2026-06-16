package dev.yichen.watertracker.notify

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dev.yichen.watertracker.WaterTrackerApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class QuickLogReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val amountMl = intent.getIntExtra(EXTRA_AMOUNT_ML, 200)
        val pending = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val repo = (context.applicationContext as WaterTrackerApp).container.repository
                repo.addDrink(amountMl)
            } finally {
                pending.finish()
            }
        }
    }

    companion object {
        const val EXTRA_AMOUNT_ML = "amount_ml"

        fun pendingIntent(context: Context, amountMl: Int): PendingIntent {
            val intent = Intent(context, QuickLogReceiver::class.java).apply {
                putExtra(EXTRA_AMOUNT_ML, amountMl)
            }
            return PendingIntent.getBroadcast(
                context,
                amountMl,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
    }
}
