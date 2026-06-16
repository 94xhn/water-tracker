package dev.yichen.watertracker.domain.model

enum class DrinkType(
    val displayName: String,
    val emoji: String,
    val hydrationFactor: Float
) {
    WATER("Water", "💧", 1.0f),
    COFFEE("Coffee", "☕", 0.8f),
    TEA("Tea", "🍵", 0.9f),
    JUICE("Juice", "🍹", 0.9f),
    MILK("Milk", "🥛", 0.9f),
    SODA("Soda", "🥤", 0.75f)
}
