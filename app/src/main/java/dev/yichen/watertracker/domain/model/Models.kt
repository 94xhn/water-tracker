package dev.yichen.watertracker.domain.model

data class DrinkEntry(
    val id: Long = 0,
    val amountMl: Int,
    val timestampMs: Long
)

data class Settings(
    val goalMl: Int = 2000,
    val weightKg: Float = 0f,
    val reminderEnabled: Boolean = false,
    val reminderStartHour: Int = 8,
    val reminderEndHour: Int = 22,
    val reminderIntervalHours: Int = 1,
    val cupSizes: List<Int> = listOf(150, 200, 250, 300)
)
