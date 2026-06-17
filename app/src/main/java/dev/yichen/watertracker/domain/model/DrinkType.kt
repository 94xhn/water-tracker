package dev.yichen.watertracker.domain.model

data class DrinkType(
    val key: String,
    val displayName: String,
    val emoji: String,
    val hydrationFactor: Float,
    val isCustom: Boolean = false,
    val customId: Long = 0L
) {
    companion object {
        val WATER  = DrinkType("WATER",  "Water",  "💧", 1.0f)
        val COFFEE = DrinkType("COFFEE", "Coffee", "☕", 0.8f)
        val TEA    = DrinkType("TEA",    "Tea",    "🍵", 0.9f)
        val JUICE  = DrinkType("JUICE",  "Juice",  "🍹", 0.9f)
        val MILK   = DrinkType("MILK",   "Milk",   "🥛", 0.9f)
        val SODA   = DrinkType("SODA",   "Soda",   "🥤", 0.75f)
        val PRESETS = listOf(WATER, COFFEE, TEA, JUICE, MILK, SODA)

        fun fromKey(key: String): DrinkType? = PRESETS.find { it.key == key }
    }
}
