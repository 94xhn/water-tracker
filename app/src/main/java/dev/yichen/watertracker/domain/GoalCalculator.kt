package dev.yichen.watertracker.domain

import java.util.Calendar

object GoalCalculator {
    fun recommendedMl(weightKg: Float): Int =
        (weightKg * 30).toInt().coerceIn(1500, 4000)

    fun todayStartMs(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }
}
