package dev.yichen.watertracker.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.yichen.watertracker.WaterTrackerApp
import dev.yichen.watertracker.domain.GoalCalculator
import dev.yichen.watertracker.domain.model.DrinkEntry
import dev.yichen.watertracker.domain.model.DrinkType
import dev.yichen.watertracker.domain.model.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class HomeViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = (app as WaterTrackerApp).container.repository

    val entries: StateFlow<List<DrinkEntry>> = repo.todayEntries
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val settings: StateFlow<Settings> = repo.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Settings())

    private val _selectedDrinkType = MutableStateFlow(DrinkType.WATER)
    val selectedDrinkType: StateFlow<DrinkType> = _selectedDrinkType.asStateFlow()

    val streak: StateFlow<Int> = combine(
        repo.entriesFrom(GoalCalculator.todayStartMs() - 29L * 86_400_000L),
        repo.settings
    ) { entries, settings ->
        computeStreak(entries, settings.goalMl)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun selectDrinkType(type: DrinkType) { _selectedDrinkType.value = type }

    fun addDrink(amountMl: Int) {
        viewModelScope.launch { repo.addDrink(amountMl, _selectedDrinkType.value) }
    }

    fun deleteDrink(id: Long) {
        viewModelScope.launch { repo.deleteDrink(id) }
    }

    private fun dayStartMs(ms: Long): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = ms
        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    private fun computeStreak(entries: List<DrinkEntry>, goalMl: Int): Int {
        if (goalMl <= 0) return 0
        val byDay = entries.groupBy { dayStartMs(it.timestampMs) }
        val today = dayStartMs(System.currentTimeMillis())
        val todayEff = byDay[today]?.sumOf { it.effectiveMl } ?: 0

        var streak = if (todayEff >= goalMl) 1 else 0
        var check = today - 86_400_000L
        while (true) {
            val eff = byDay[check]?.sumOf { it.effectiveMl } ?: 0
            if (eff >= goalMl) { streak++; check -= 86_400_000L } else break
        }
        return streak
    }
}
