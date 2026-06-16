package dev.yichen.watertracker.di

import android.content.Context
import dev.yichen.watertracker.data.WaterRepository
import dev.yichen.watertracker.data.db.WaterDatabase
import dev.yichen.watertracker.notify.GoalNotifier
import dev.yichen.watertracker.notify.WaterReminderScheduler

class AppContainer(context: Context) {
    private val appContext = context.applicationContext
    private val database: WaterDatabase by lazy { WaterDatabase.build(appContext) }
    val repository: WaterRepository by lazy { WaterRepository(database) }
    val reminderScheduler: WaterReminderScheduler by lazy { WaterReminderScheduler(appContext) }
    val goalNotifier: GoalNotifier by lazy { GoalNotifier(appContext) }
}
