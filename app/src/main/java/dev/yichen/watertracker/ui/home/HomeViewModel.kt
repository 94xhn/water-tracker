package dev.yichen.watertracker.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.yichen.watertracker.WaterTrackerApp
import dev.yichen.watertracker.domain.model.DrinkEntry
import dev.yichen.watertracker.domain.model.Settings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = (app as WaterTrackerApp).container.repository

    val entries: StateFlow<List<DrinkEntry>> = repo.todayEntries
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val settings: StateFlow<Settings> = repo.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Settings())

    fun addDrink(amountMl: Int) {
        viewModelScope.launch { repo.addDrink(amountMl) }
    }

    fun deleteDrink(id: Long) {
        viewModelScope.launch { repo.deleteDrink(id) }
    }
}
