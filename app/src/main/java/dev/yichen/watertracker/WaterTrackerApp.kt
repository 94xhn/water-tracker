package dev.yichen.watertracker

import android.app.Application
import dev.yichen.watertracker.di.AppContainer
import dev.yichen.watertracker.notify.ensureChannel

class WaterTrackerApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        ensureChannel(this)
        container.reminderScheduler.scheduleDailyProgress()
    }
}
