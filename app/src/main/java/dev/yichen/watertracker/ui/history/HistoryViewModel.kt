package dev.yichen.watertracker.ui.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.yichen.watertracker.WaterTrackerApp
import dev.yichen.watertracker.domain.GoalCalculator
import dev.yichen.watertracker.domain.model.DrinkEntry
import dev.yichen.watertracker.domain.model.Settings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class DayHistory(
    val dayStartMs: Long,
    val dateLabel: String,
    val totalMl: Int,
    val entries: List<DrinkEntry>
)

data class WeekStats(val avgMl: Int, val goalMetDays: Int, val totalDays: Int)

class HistoryViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = (app as WaterTrackerApp).container.repository

    val settings: StateFlow<Settings> = repo.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Settings())

    val days: StateFlow<List<DayHistory>> = repo.entriesFrom(sevenDaysAgoMs())
        .map { entries -> groupByDay(entries) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val weekStats: StateFlow<WeekStats> = combine(days, settings) { dayList, settings ->
        val goalMet = dayList.count { it.totalMl >= settings.goalMl }
        val avg = if (dayList.isEmpty()) 0 else dayList.sumOf { it.totalMl } / dayList.size
        WeekStats(avgMl = avg, goalMetDays = goalMet, totalDays = dayList.size)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), WeekStats(0, 0, 0))

    private fun sevenDaysAgoMs(): Long = GoalCalculator.todayStartMs() - 6L * 86_400_000L

    private fun dayStartMs(ms: Long): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = ms
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    private fun groupByDay(entries: List<DrinkEntry>): List<DayHistory> {
        val fmt = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
        return entries
            .groupBy { dayStartMs(it.timestampMs) }
            .entries
            .sortedByDescending { it.key }
            .map { (dayMs, dayEntries) ->
                DayHistory(
                    dayStartMs = dayMs,
                    dateLabel = fmt.format(Date(dayMs)),
                    totalMl = dayEntries.sumOf { it.effectiveMl },
                    entries = dayEntries.sortedByDescending { it.timestampMs }
                )
            }
    }
}
