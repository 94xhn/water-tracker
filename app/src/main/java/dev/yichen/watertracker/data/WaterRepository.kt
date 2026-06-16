package dev.yichen.watertracker.data

import dev.yichen.watertracker.data.db.DrinkEntryEntity
import dev.yichen.watertracker.data.db.SettingsEntity
import dev.yichen.watertracker.data.db.WaterDatabase
import dev.yichen.watertracker.domain.GoalCalculator
import dev.yichen.watertracker.domain.model.DrinkEntry
import dev.yichen.watertracker.domain.model.DrinkType
import dev.yichen.watertracker.domain.model.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WaterRepository(db: WaterDatabase) {
    private val drinkDao = db.drinkEntryDao()
    private val settingsDao = db.settingsDao()

    val todayEntries: Flow<List<DrinkEntry>> = drinkDao
        .entriesFrom(GoalCalculator.todayStartMs())
        .map { list -> list.map { it.toDomain() } }

    val settings: Flow<Settings> = settingsDao.observe()
        .map { it?.toDomain() ?: Settings() }

    fun entriesFrom(startMs: Long): Flow<List<DrinkEntry>> =
        drinkDao.entriesFrom(startMs).map { list -> list.map { it.toDomain() } }

    suspend fun addDrink(amountMl: Int, drinkType: DrinkType = DrinkType.WATER) {
        drinkDao.insert(
            DrinkEntryEntity(
                amountMl = amountMl,
                timestampMs = System.currentTimeMillis(),
                drinkTypeName = drinkType.name
            )
        )
    }

    suspend fun deleteDrink(id: Long) {
        drinkDao.deleteById(id)
    }

    suspend fun saveSettings(settings: Settings) {
        settingsDao.save(settings.toEntity())
    }

    suspend fun exportCsv(): String {
        val fmt = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val sb = StringBuilder("date,amount_ml,drink_type,effective_ml\n")
        drinkDao.allEntries().forEach { e ->
            val type = runCatching { DrinkType.valueOf(e.drinkTypeName) }.getOrDefault(DrinkType.WATER)
            sb.appendLine("${fmt.format(Date(e.timestampMs))},${e.amountMl},${type.displayName},${(e.amountMl * type.hydrationFactor).toInt()}")
        }
        return sb.toString()
    }

    private fun DrinkEntryEntity.toDomain(): DrinkEntry {
        val type = runCatching { DrinkType.valueOf(drinkTypeName) }.getOrDefault(DrinkType.WATER)
        return DrinkEntry(id, amountMl, timestampMs, type)
    }

    private fun SettingsEntity.toDomain() = Settings(
        goalMl, weightKg, reminderEnabled, reminderStartHour, reminderEndHour, reminderIntervalHours,
        cupSizesJson.split(",").mapNotNull { it.trim().toIntOrNull() }.ifEmpty { listOf(150, 200, 250, 300) }
    )
    private fun Settings.toEntity() = SettingsEntity(
        goalMl = goalMl,
        weightKg = weightKg,
        reminderEnabled = reminderEnabled,
        reminderStartHour = reminderStartHour,
        reminderEndHour = reminderEndHour,
        reminderIntervalHours = reminderIntervalHours,
        cupSizesJson = cupSizes.joinToString(",")
    )
}
