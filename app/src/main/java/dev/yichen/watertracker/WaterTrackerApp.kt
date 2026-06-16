package dev.yichen.watertracker

import android.app.Application
import dev.yichen.watertracker.di.AppContainer

class WaterTrackerApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
