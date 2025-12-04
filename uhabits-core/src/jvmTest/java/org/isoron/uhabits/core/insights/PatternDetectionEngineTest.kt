/*
 * Copyright (C) 2016-2025 Álinson Santos Xavier <git@axavier.org>
 *
 * This file is part of Loop Habit Tracker.
 *
 * Loop Habit Tracker is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * Loop Habit Tracker is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.isoron.uhabits.core.insights

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.isoron.uhabits.core.BaseUnitTest
import org.isoron.uhabits.core.models.Entry
import org.isoron.uhabits.core.models.NumericalHabitType
import org.isoron.uhabits.core.models.Reminder
import org.isoron.uhabits.core.models.WeekdayList
import org.isoron.uhabits.core.utils.DateUtils
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PatternDetectionEngineTest : BaseUnitTest() {

    private lateinit var engine: PatternDetectionEngine

    @Before
    override fun setUp() {
        super.setUp()
        engine = PatternDetectionEngine()
    }

    @Test
    fun testComputeWeekdayStats_withBooleanHabit() {
        val habit = fixtures.createEmptyHabit()
        val today = DateUtils.getTodayWithOffset()

        for (i in 0 until 60) {
            val timestamp = today.minus(i)
            val weekday = timestamp.weekday
            val value = if (weekday == 2 || weekday == 3) Entry.YES_MANUAL else Entry.NO
            habit.originalEntries.add(Entry(timestamp, value))
        }
        habit.recompute()

        val stats = engine.computeWeekdayStats(habit, 60)

        assertThat(stats.size, equalTo(7))

        val mondayStats = stats.find { it.weekday == 2 }
        assertNotNull(mondayStats)
        assertTrue(mondayStats.successRate > 0.8)
        assertTrue(mondayStats.totalDays >= 8)

        val tuesdayStats = stats.find { it.weekday == 3 }
        assertNotNull(tuesdayStats)
        assertTrue(tuesdayStats.successRate > 0.8)
    }

    @Test
    fun testComputeWeekdayStats_withNumericalHabit() {
        val habit = fixtures.createEmptyNumericalHabit(NumericalHabitType.AT_LEAST)
        habit.targetValue = 5.0
        val today = DateUtils.getTodayWithOffset()

        for (i in 0 until 60) {
            val timestamp = today.minus(i)
            val weekday = timestamp.weekday
            val value = if (weekday == 2 || weekday == 3) 6000 else 3000
            habit.originalEntries.add(Entry(timestamp, value))
        }
        habit.recompute()

        val stats = engine.computeWeekdayStats(habit, 60)

        assertThat(stats.size, equalTo(7))

        val mondayStats = stats.find { it.weekday == 2 }
        assertNotNull(mondayStats)
        assertTrue(mondayStats.successRate > 0.8)
    }

    @Test
    fun testComputeWeekdayStats_emptyHabit() {
        val habit = fixtures.createEmptyHabit()

        val stats = engine.computeWeekdayStats(habit, 30)

        assertThat(stats.size, equalTo(7))
        stats.forEach { stat ->
            assertThat(stat.totalDays, equalTo(0))
            assertThat(stat.successRate, equalTo(0.0))
        }
    }

    @Test
    fun testDetectStreakTrend_improving() {
        val habit = fixtures.createEmptyHabit()
        val today = DateUtils.getTodayWithOffset()

        for (i in 0..9) {
            habit.originalEntries.add(Entry(today.minus(i), Entry.YES_MANUAL))
        }

        for (i in 15..19) {
            habit.originalEntries.add(Entry(today.minus(i), Entry.YES_MANUAL))
        }

        for (i in 25..27) {
            habit.originalEntries.add(Entry(today.minus(i), Entry.YES_MANUAL))
        }

        habit.recompute()

        val trend = engine.detectStreakTrend(habit)

        assertThat(trend, equalTo("IMPROVING"))
    }

    @Test
    fun testDetectStreakTrend_declining() {
        val habit = fixtures.createEmptyHabit()
        val today = DateUtils.getTodayWithOffset()

        for (i in 0..2) {
            habit.originalEntries.add(Entry(today.minus(i), Entry.YES_MANUAL))
        }

        for (i in 8..12) {
            habit.originalEntries.add(Entry(today.minus(i), Entry.YES_MANUAL))
        }

        for (i in 20..29) {
            habit.originalEntries.add(Entry(today.minus(i), Entry.YES_MANUAL))
        }

        habit.recompute()

        val trend = engine.detectStreakTrend(habit)

        assertThat(trend, equalTo("DECLINING"))
    }

    @Test
    fun testDetectStreakTrend_stable() {
        val habit = fixtures.createEmptyHabit()
        val today = DateUtils.getTodayWithOffset()

        for (i in 0..4) {
            habit.originalEntries.add(Entry(today.minus(i), Entry.YES_MANUAL))
        }

        for (i in 10..14) {
            habit.originalEntries.add(Entry(today.minus(i), Entry.YES_MANUAL))
        }

        for (i in 20..24) {
            habit.originalEntries.add(Entry(today.minus(i), Entry.YES_MANUAL))
        }

        habit.recompute()

        val trend = engine.detectStreakTrend(habit)

        assertThat(trend, equalTo("STABLE"))
    }

    @Test
    fun testDetectStreakTrend_noData() {
        val habit = fixtures.createEmptyHabit()

        val trend = engine.detectStreakTrend(habit)

        assertThat(trend, equalTo("NO_DATA"))
    }

    @Test
    fun testDetectStreakTrend_insufficientData() {
        val habit = fixtures.createEmptyHabit()
        val today = DateUtils.getTodayWithOffset()

        for (i in 0..4) {
            habit.originalEntries.add(Entry(today.minus(i), Entry.YES_MANUAL))
        }
        habit.recompute()

        val trend = engine.detectStreakTrend(habit)

        assertThat(trend, equalTo("INSUFFICIENT_DATA"))
    }

    @Test
    fun testFindBestConsistencyWindow_withReminder() {
        val habit = fixtures.createEmptyHabit()
        habit.reminder = Reminder(hour = 9, minute = 0, days = WeekdayList.EVERY_DAY)
        val today = DateUtils.getTodayWithOffset()

        for (i in 0 until 30) {
            val value = if (i % 2 == 0) Entry.YES_MANUAL else Entry.NO
            habit.originalEntries.add(Entry(today.minus(i), value))
        }
        habit.recompute()

        val window = engine.findBestConsistencyWindow(habit, 30)

        assertNotNull(window)
        assertThat(window.occurrences, greaterThanOrEqualTo(5))
        assertThat(window.startHour, equalTo(6))
        assertThat(window.endHour, equalTo(12))
        assertThat(window.label, equalTo("Morning"))
    }

    @Test
    fun testFindBestConsistencyWindow_noReminder() {
        val habit = fixtures.createEmptyHabit()
        val today = DateUtils.getTodayWithOffset()

        for (i in 0 until 30) {
            habit.originalEntries.add(Entry(today.minus(i), Entry.YES_MANUAL))
        }
        habit.recompute()

        val window = engine.findBestConsistencyWindow(habit, 30)

        assertNull(window)
    }

    @Test
    fun testGenerateInsights_bestDay() {
        val habit = fixtures.createEmptyHabit()
        val today = DateUtils.getTodayWithOffset()

        for (i in 0 until 90) {
            val timestamp = today.minus(i)
            val weekday = timestamp.weekday
            val value = if (weekday == 2) Entry.YES_MANUAL else Entry.NO
            habit.originalEntries.add(Entry(timestamp, value))
        }
        habit.recompute()

        val insights = engine.generateInsights(habit, 90)

        val bestDayInsight = insights.find { it.type == InsightType.BEST_DAY }
        assertNotNull(bestDayInsight)
        assertThat(bestDayInsight.message, containsString("Monday"))
    }

    @Test
    fun testGenerateInsights_worstDay() {
        val habit = fixtures.createEmptyHabit()
        val today = DateUtils.getTodayWithOffset()

        for (i in 0 until 90) {
            val timestamp = today.minus(i)
            val weekday = timestamp.weekday
            val value = if (weekday == 5) Entry.NO else Entry.YES_MANUAL
            habit.originalEntries.add(Entry(timestamp, value))
        }
        habit.recompute()

        val insights = engine.generateInsights(habit, 90)

        val worstDayInsight = insights.find { it.type == InsightType.WORST_DAY }
        assertNotNull(worstDayInsight)
        assertThat(worstDayInsight.message, containsString("Thursday"))
    }

    @Test
    fun testGenerateInsights_checkInTiming() {
        val habit = fixtures.createEmptyHabit()
        habit.reminder = Reminder(hour = 14, minute = 30, days = WeekdayList.EVERY_DAY)
        val today = DateUtils.getTodayWithOffset()

        for (i in 0 until 30) {
            habit.originalEntries.add(Entry(today.minus(i), Entry.YES_MANUAL))
        }
        habit.recompute()

        val insights = engine.generateInsights(habit, 30)

        val timingInsight = insights.find { it.type == InsightType.CHECK_IN_TIMING }
        assertNotNull(timingInsight)
        assertThat(timingInsight.message, containsString("afternoon"))
        val hour = timingInsight.metadata["hour"] as Int
        assertThat(hour, equalTo(14))
    }

    @Test
    fun testGenerateInsights_streakTrend() {
        val habit = fixtures.createEmptyHabit()
        val today = DateUtils.getTodayWithOffset()

        for (i in 0..9) {
            habit.originalEntries.add(Entry(today.minus(i), Entry.YES_MANUAL))
        }

        for (i in 15..19) {
            habit.originalEntries.add(Entry(today.minus(i), Entry.YES_MANUAL))
        }

        for (i in 25..27) {
            habit.originalEntries.add(Entry(today.minus(i), Entry.YES_MANUAL))
        }

        habit.recompute()

        val insights = engine.generateInsights(habit, 90)

        val trendInsight = insights.find { it.type == InsightType.STREAK_TREND }
        assertNotNull(trendInsight)
        assertThat(trendInsight.metadata["trend"], equalTo("IMPROVING"))
    }

    @Test
    fun testWeekdayStatsNames() {
        val stats = WeekdayStats(weekday = 0, successRate = 0.5, totalDays = 10, successfulDays = 5)
        assertEquals("Saturday", stats.weekdayName)

        val stats2 = WeekdayStats(weekday = 2, successRate = 0.5, totalDays = 10, successfulDays = 5)
        assertEquals("Monday", stats2.weekdayName)

        val stats3 = WeekdayStats(weekday = 6, successRate = 0.5, totalDays = 10, successfulDays = 5)
        assertEquals("Friday", stats3.weekdayName)
    }

    @Test
    fun testConsistencyWindowLabels() {
        val early = ConsistencyWindow(startHour = 5, endHour = 6, successRate = 0.8, occurrences = 10)
        assertEquals("Early Morning", early.label)

        val morning = ConsistencyWindow(startHour = 9, endHour = 12, successRate = 0.8, occurrences = 10)
        assertEquals("Morning", morning.label)

        val afternoon = ConsistencyWindow(startHour = 14, endHour = 17, successRate = 0.8, occurrences = 10)
        assertEquals("Afternoon", afternoon.label)

        val evening = ConsistencyWindow(startHour = 18, endHour = 21, successRate = 0.8, occurrences = 10)
        assertEquals("Evening", evening.label)

        val night = ConsistencyWindow(startHour = 22, endHour = 24, successRate = 0.8, occurrences = 10)
        assertEquals("Night", night.label)
    }
}
