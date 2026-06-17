package dev.yichen.watertracker.ui.settings

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.yichen.watertracker.WaterTrackerApp
import dev.yichen.watertracker.domain.model.DrinkType
import dev.yichen.watertracker.domain.model.Settings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = (app as WaterTrackerApp).container.repository
    private val scheduler = (app as WaterTrackerApp).container.reminderScheduler

    val settings: StateFlow<Settings> = repo.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Settings())

    val customDrinks: StateFlow<List<DrinkType>> = repo.allDrinkTypes
        .map { types -> types.filter { it.isCustom } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addCustomDrink(name: String, emoji: String, factor: Float) {
        viewModelScope.launch { repo.addCustomDrink(name, emoji, factor) }
    }

    fun deleteCustomDrink(id: Long) {
        viewModelScope.launch { repo.deleteCustomDrink(id) }
    }

    fun save(settings: Settings) {
        viewModelScope.launch {
            repo.saveSettings(settings)
            scheduler.schedule(settings)
        }
    }

    fun exportCsv(context: Context) {
        viewModelScope.launch {
            val csv = repo.exportCsv()
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, csv)
                putExtra(Intent.EXTRA_SUBJECT, "WaterTracker Data Export")
            }
            context.startActivity(Intent.createChooser(intent, "Export Data"))
        }
    }
}
