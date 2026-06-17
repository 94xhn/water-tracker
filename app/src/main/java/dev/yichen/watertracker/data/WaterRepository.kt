package dev.yichen.watertracker.data

import android.content.Context
import android.content.Intent
import dev.yichen.watertracker.data.db.CustomDrinkEntity
import dev.yichen.watertracker.data.db.DrinkEntryEntity
import dev.yichen.watertracker.data.db.SettingsEntity
import dev.yichen.watertracker.data.db.WaterDatabase
import dev.yichen.watertracker.domain.GoalCalculator
import dev.yichen.watertracker.domain.model.DrinkEntry
import dev.yichen.watertracker.domain.model.DrinkType
import dev.yichen.watertracker.domain.model.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

const val ACTION_DATA_CHANGED = "dev.yichen.watertracker.DATA_CHANGED"

class WaterRepository(db: WaterDatabase, private val context: Context) {
    private val drinkDao = db.drinkEntryDao()
    private val settingsDao = db.settingsDao()
    private val customDrinkDao = db.customDrinkDao()

    private val typeMapFlow: Flow<Map<String, DrinkType>> =
        customDrinkDao.all().map { customs ->
            (DrinkType.PRESETS + customs.map { it.toDomain() }).associateBy { it.key }
        }

    val allDrinkTypes: Flow<List<DrinkType>> =
        customDrinkDao.all().map { customs ->
            DrinkType.PRESETS + customs.map { it.toDomain() }
        }

    val todayEntries: Flow<List<DrinkEntry>> = combine(
        drinkDao.entriesFrom(GoalCalculator.todayStartMs()),
        typeMapFlow
    ) { entries, typeMap ->
        entries.map { it.toDomain(typeMap) }
    }

    val settings: Flow<Settings> = settingsDao.observe()
        .map { it?.toDomain() ?: Settings() }

    fun entriesFrom(startMs: Long): Flow<List<DrinkEntry>> = combine(
        drinkDao.entriesFrom(startMs),
        typeMapFlow
    ) { entries, typeMap ->
        entries.map { it.toDomain(typeMap) }
    }

    suspend fun addDrink(amountMl: Int, drinkType: DrinkType = DrinkType.WATER) {
        drinkDao.insert(
            DrinkEntryEntity(
                amountMl = amountMl,
                timestampMs = System.currentTimeMillis(),
                drinkTypeName = drinkType.key
            )
        )
        notifyWidget()
    }

    suspend fun updateDrink(id: Long, amountMl: Int, drinkType: DrinkType) {
        drinkDao.updateEntry(id, amountMl, drinkType.key)
        notifyWidget()
    }

    suspend fun deleteDrink(id: Long) {
        drinkDao.deleteById(id)
        notifyWidget()
    }

    suspend fun saveSettings(settings: Settings) {
        settingsDao.save(settings.toEntity())
    }

    suspend fun addCustomDrink(name: String, emoji: String, hydrationFactor: Float) {
        customDrinkDao.insert(CustomDrinkEntity(name = name, emoji = emoji, hydrationFactor = hydrationFactor))
    }

    suspend fun deleteCustomDrink(id: Long) {
        customDrinkDao.deleteById(id)
    }

    suspend fun getTodayStats(): Pair<Int, Int> {
        val today = GoalCalculator.todayStartMs()
        val entries = drinkDao.entriesFromSuspend(today)
        val customs = customDrinkDao.allSuspend()
        val typeMap = (DrinkType.PRESETS + customs.map { it.toDomain() }).associateBy { it.key }
        val totalMl = entries.sumOf { e ->
            val type = typeMap[e.drinkTypeName] ?: DrinkType.WATER
            (e.amountMl * type.hydrationFactor).toInt()
        }
        val settings = settingsDao.getSuspend()
        return Pair(totalMl, settings?.goalMl ?: 2000)
    }

    suspend fun exportCsv(): String {
        val customs = customDrinkDao.allSuspend()
        val typeMap = (DrinkType.PRESETS + customs.map { it.toDomain() }).associateBy { it.key }
        val fmt = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val sb = StringBuilder("date,amount_ml,drink_type,effective_ml\n")
        drinkDao.allEntries().forEach { e ->
            val type = typeMap[e.drinkTypeName] ?: DrinkType.WATER
            sb.appendLine("${fmt.format(Date(e.timestampMs))},${e.amountMl},${type.displayName},${(e.amountMl * type.hydrationFactor).toInt()}")
        }
        return sb.toString()
    }

    private fun notifyWidget() {
        try {
            context.sendBroadcast(Intent(ACTION_DATA_CHANGED).apply {
                setPackage(context.packageName)
            })
        } catch (_: Exception) {}
    }

    private fun CustomDrinkEntity.toDomain() = DrinkType(
        key = "custom_$id",
        displayName = name,
        emoji = emoji,
        hydrationFactor = hydrationFactor,
        isCustom = true,
        customId = id
    )

    private fun DrinkEntryEntity.toDomain(typeMap: Map<String, DrinkType>): DrinkEntry {
        val type = typeMap[drinkTypeName] ?: DrinkType.WATER
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
