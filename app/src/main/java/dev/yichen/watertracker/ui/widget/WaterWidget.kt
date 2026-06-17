package dev.yichen.watertracker.ui.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import dev.yichen.watertracker.R
import dev.yichen.watertracker.WaterTrackerApp
import dev.yichen.watertracker.notify.QuickLogReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WaterWidget : AppWidgetProvider() {

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == "dev.yichen.watertracker.DATA_CHANGED") {
            val mgr = AppWidgetManager.getInstance(context)
            val ids = mgr.getAppWidgetIds(ComponentName(context, WaterWidget::class.java))
            if (ids.isNotEmpty()) onUpdate(context, mgr, ids)
        }
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        CoroutineScope(Dispatchers.IO).launch {
            val repo = (context.applicationContext as WaterTrackerApp).container.repository
            val (totalMl, goalMl) = repo.getTodayStats()
            val pct = if (goalMl > 0) (totalMl * 100 / goalMl).coerceIn(0, 100) else 0

            appWidgetIds.forEach { id ->
                val views = RemoteViews(context.packageName, R.layout.widget_water)
                views.setTextViewText(R.id.widget_total, "${totalMl} ml")
                views.setTextViewText(R.id.widget_pct, "$pct% · goal ${goalMl}ml")
                views.setOnClickPendingIntent(
                    R.id.widget_add_200,
                    QuickLogReceiver.pendingIntent(context, 200)
                )
                appWidgetManager.updateAppWidget(id, views)
            }
        }
    }
}
