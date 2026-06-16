package dev.yichen.watertracker.data

import dev.yichen.watertracker.data.db.DrinkEntryEntity
import dev.yichen.watertracker.data.db.SettingsEntity
import dev.yichen.watertracker.data.db.WaterDatabase
import dev.yichen.watertracker.domain.GoalCalculator
import dev.yichen.watertracker.domain.model.DrinkEntry
import dev.yichen.watertracker.domain.model.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WaterRepository(db: WaterDatabase) {
    private val drinkDao = db.drinkEntryDao()
    private val settingsDao = db.settingsDao()

    val todayEntries: Flow<List<DrinkEntry>> = drinkDao
        .entriesFrom(GoalCalculator.todayStartMs())
        .map { list -> list.map { it.toDomain() } }

    val settings: Flow<Settings> = settingsDao.observe()
        .map { it?.toDomain() ?: Settings() }

    suspend fun addDrink(amountMl: Int) {
        drinkDao.insert(DrinkEntryEntity(amountMl = amountMl, timestampMs = System.currentTimeMillis()))
    }

    suspend fun deleteDrink(id: Long) {
        drinkDao.deleteById(id)
    }

    suspend fun saveSettings(settings: Settings) {
        settingsDao.save(settings.toEntity())
    }

    private fun DrinkEntryEntity.toDomain() = DrinkEntry(id, amountMl, timestampMs)
    private fun SettingsEntity.toDomain() =
        Settings(goalMl, weightKg, reminderEnabled, reminderStartHour, reminderEndHour, reminderIntervalHours)
    private fun Settings.toEntity() = SettingsEntity(
        goalMl = goalMl,
        weightKg = weightKg,
        reminderEnabled = reminderEnabled,
        reminderStartHour = reminderStartHour,
        reminderEndHour = reminderEndHour,
        reminderIntervalHours = reminderIntervalHours
    )
}
