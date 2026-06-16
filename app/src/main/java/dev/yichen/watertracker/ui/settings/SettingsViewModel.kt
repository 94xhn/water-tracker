package dev.yichen.watertracker.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.yichen.watertracker.WaterTrackerApp
import dev.yichen.watertracker.domain.model.Settings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = (app as WaterTrackerApp).container.repository
    private val scheduler = (app as WaterTrackerApp).container.reminderScheduler

    val settings: StateFlow<Settings> = repo.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Settings())

    fun save(settings: Settings) {
        viewModelScope.launch {
            repo.saveSettings(settings)
            scheduler.schedule(settings)
        }
    }
}
