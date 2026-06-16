package dev.yichen.watertracker.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GoalCalculatorTest {
    @Test
    fun `70kg gives 2100ml`() = assertEquals(2100, GoalCalculator.recommendedMl(70f))

    @Test
    fun `very light weight clamps to 1500`() = assertEquals(1500, GoalCalculator.recommendedMl(10f))

    @Test
    fun `very heavy weight clamps to 4000`() = assertEquals(4000, GoalCalculator.recommendedMl(200f))

    @Test
    fun `60kg gives 1800ml`() = assertEquals(1800, GoalCalculator.recommendedMl(60f))

    @Test
    fun `50kg clamps to 1500ml`() = assertEquals(1500, GoalCalculator.recommendedMl(50f))

    @Test
    fun `todayStartMs is earlier than now and within 24h`() {
        val ms = GoalCalculator.todayStartMs()
        val nowMs = System.currentTimeMillis()
        assertTrue(ms <= nowMs, "todayStart should be before now")
        assertTrue(nowMs - ms < 86_400_000L, "todayStart should be within 24h of now")
    }
}
