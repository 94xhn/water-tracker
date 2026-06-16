package dev.yichen.watertracker.domain

import dev.yichen.watertracker.domain.model.Settings
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class SettingsDefaultTest {
    @Test
    fun `default goal is 2000ml`() = assertEquals(2000, Settings().goalMl)

    @Test
    fun `reminders off by default`() = assertFalse(Settings().reminderEnabled)

    @Test
    fun `default reminder window is 8 to 22`() {
        val s = Settings()
        assertEquals(8, s.reminderStartHour)
        assertEquals(22, s.reminderEndHour)
    }

    @Test
    fun `default interval is 1 hour`() = assertEquals(1, Settings().reminderIntervalHours)

    @Test
    fun `interval to ms conversion is correct`() {
        val s = Settings(reminderIntervalHours = 2)
        assertEquals(7_200_000L, s.reminderIntervalHours * 3_600_000L)
    }
}
