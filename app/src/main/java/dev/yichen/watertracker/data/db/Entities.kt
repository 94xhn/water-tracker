package dev.yichen.watertracker.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "drink_entries")
data class DrinkEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amountMl: Int,
    val timestampMs: Long,
    val drinkTypeName: String = "WATER"
)

@Entity(tableName = "settings")
data class SettingsEntity(
    @PrimaryKey val id: Int = 1,
    val goalMl: Int = 2000,
    val weightKg: Float = 0f,
    val reminderEnabled: Boolean = false,
    val reminderStartHour: Int = 8,
    val reminderEndHour: Int = 22,
    val reminderIntervalHours: Int = 1,
    val cupSizesJson: String = "150,200,250,300"
)

@Entity(tableName = "custom_drinks")
data class CustomDrinkEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val emoji: String = "💧",
    val hydrationFactor: Float = 1.0f
)
