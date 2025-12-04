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
import org.isoron.uhabits.core.models.Reminder
import org.isoron.uhabits.core.models.WeekdayList
import org.isoron.uhabits.core.utils.DateUtils
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class InsightsInteractorTest : BaseUnitTest() {

    private lateinit var interactor: InsightsInteractor

    @Before
    override fun setUp() {
        super.setUp()
        interactor = InsightsInteractor()
    }

    @Test
    fun testGetInsightsForHabit_basic() {
        val habit = fixtures.createEmptyHabit()
        habit.name = "Exercise"
        habit.id = 1L
        val today = DateUtils.getTodayWithOffset()

        for (i in 0 until 60) {
            val value = if (i % 2 == 0) Entry.YES_MANUAL else Entry.NO
            habit.originalEntries.add(Entry(today.minus(i), value))
        }
        habit.recompute()

        val summary = interactor.getInsightsForHabit(habit, 60)

        assertThat(summary.habitId, equalTo(1L))
        assertThat(summary.habitName, equalTo("Exercise"))
        assertThat(summary.insights, not(empty()))
        assertThat(summary.weekdayStats.size, equalTo(7))
        assertThat(summary.streakTrend, not(equalTo("NO_DATA")))
    }

    @Test
    fun testGetInsightsForHabit_withReminder() {
        val habit = fixtures.createEmptyHabit()
        habit.name = "Meditate"
        habit.id = 1L
        habit.reminder = Reminder(hour = 8, minute = 0, days = WeekdayList.EVERY_DAY)
        val today = DateUtils.getTodayWithOffset()

        for (i in 0 until 30) {
            habit.originalEntries.add(Entry(today.minus(i), Entry.YES_MANUAL))
        }
        habit.recompute()

        val summary = interactor.getInsightsForHabit(habit, 30)

        val checkInInsight = summary.insights.find { it.type == InsightType.CHECK_IN_TIMING }
        assertNotNull(checkInInsight)
        assertThat(checkInInsight.metadata["hour"], equalTo(8))
    }

    @Test
    fun testGetInsightsForHabit_emptyHabit() {
        val habit = fixtures.createEmptyHabit()
        habit.name = "Empty"
        habit.id = 1L

        val summary = interactor.getInsightsForHabit(habit, 30)

        assertThat(summary.habitId, equalTo(1L))
        assertThat(summary.weekdayStats.size, equalTo(7))
        assertThat(summary.streakTrend, equalTo("NO_DATA"))
    }

    @Test
    fun testGetInsightsForAllHabits_multipleHabits() {
        val habit1 = fixtures.createEmptyHabit()
        habit1.name = "Exercise"
        habit1.id = 1L
        val habit2 = fixtures.createEmptyHabit()
        habit2.name = "Read"
        habit2.id = 2L

        habitList.add(habit1)
        habitList.add(habit2)

        val today = DateUtils.getTodayWithOffset()
        for (i in 0 until 50) {
            val value = if (i % 2 == 0) Entry.YES_MANUAL else Entry.NO
            habit1.originalEntries.add(Entry(today.minus(i), value))
            habit2.originalEntries.add(Entry(today.minus(i), value))
        }
        habit1.recompute()
        habit2.recompute()

        val result = interactor.getInsightsForAllHabits(habitList, 50)

        assertThat(result.habitSummaries.size, equalTo(2))
        assertThat(result.insights, not(empty()))
        assertTrue(result.correlations.isNotEmpty())
        assertTrue(result.suggestions.isNotEmpty())
    }

    @Test
    fun testGetInsightsForAllHabits_withCorrelations() {
        val habit1 = fixtures.createEmptyHabit()
        habit1.name = "Exercise"
        habit1.id = 1L
        val habit2 = fixtures.createEmptyHabit()
        habit2.name = "Sleep"
        habit2.id = 2L

        habitList.add(habit1)
        habitList.add(habit2)

        val today = DateUtils.getTodayWithOffset()
        for (i in 0 until 50) {
            val value = if (i % 2 == 0) Entry.YES_MANUAL else Entry.NO
            habit1.originalEntries.add(Entry(today.minus(i), value))
            habit2.originalEntries.add(Entry(today.minus(i), value))
        }
        habit1.recompute()
        habit2.recompute()

        val result = interactor.getInsightsForAllHabits(habitList, 50)

        assertTrue(result.correlations.isNotEmpty())
        val correlationInsight = result.insights.find { it.type == InsightType.HABIT_CORRELATION }
        assertNotNull(correlationInsight)
    }

    @Test
    fun testGetInsightsForAllHabits_excludesArchivedHabits() {
        val activeHabit = fixtures.createEmptyHabit()
        activeHabit.name = "Active"
        activeHabit.id = 1L
        activeHabit.isArchived = false

        val archivedHabit = fixtures.createEmptyHabit()
        archivedHabit.name = "Archived"
        archivedHabit.id = 2L
        archivedHabit.isArchived = true

        habitList.add(activeHabit)
        habitList.add(archivedHabit)

        val today = DateUtils.getTodayWithOffset()
        for (i in 0 until 30) {
            activeHabit.originalEntries.add(Entry(today.minus(i), Entry.YES_MANUAL))
            archivedHabit.originalEntries.add(Entry(today.minus(i), Entry.YES_MANUAL))
        }
        activeHabit.recompute()
        archivedHabit.recompute()

        val result = interactor.getInsightsForAllHabits(habitList, 30)

        assertThat(result.habitSummaries.size, equalTo(1))
        assertThat(result.habitSummaries[0].habitName, equalTo("Active"))
    }

    @Test
    fun testGetCorrelations() {
        val habit1 = fixtures.createEmptyHabit()
        habit1.name = "Exercise"
        habit1.id = 1L
        val habit2 = fixtures.createEmptyHabit()
        habit2.name = "Sleep"
        habit2.id = 2L

        habitList.add(habit1)
        habitList.add(habit2)

        val today = DateUtils.getTodayWithOffset()
        for (i in 0 until 50) {
            val value = if (i % 2 == 0) Entry.YES_MANUAL else Entry.NO
            habit1.originalEntries.add(Entry(today.minus(i), value))
            habit2.originalEntries.add(Entry(today.minus(i), value))
        }
        habit1.recompute()
        habit2.recompute()

        val correlations = interactor.getCorrelations(habitList, minSampleSize = 30, minCorrelation = 0.5)

        assertTrue(correlations.isNotEmpty())
        assertThat(correlations[0].coefficient, greaterThan(0.9))
    }

    @Test
    fun testGetSuggestions() {
        val habit = fixtures.createEmptyHabit()
        habit.name = "Exercise"
        habitList.add(habit)

        val suggestions = interactor.getSuggestions(habitList)

        assertTrue(suggestions.isNotEmpty())
    }

    @Test
    fun testGetWeekdayStats() {
        val habit = fixtures.createEmptyHabit()
        val today = DateUtils.getTodayWithOffset()

        for (i in 0 until 60) {
            val timestamp = today.minus(i)
            val weekday = timestamp.weekday
            val value = if (weekday == 2) Entry.YES_MANUAL else Entry.NO
            habit.originalEntries.add(Entry(timestamp, value))
        }
        habit.recompute()

        val stats = interactor.getWeekdayStats(habit, 60)

        assertThat(stats.size, equalTo(7))

        val mondayStats = stats.find { it.weekday == 2 }
        assertNotNull(mondayStats)
        assertTrue(mondayStats.successRate > 0.8)
    }

    @Test
    fun testDeterministicResults() {
        val habit = fixtures.createEmptyHabit()
        habit.name = "Test"
        habit.id = 1L
        val today = DateUtils.getTodayWithOffset()

        for (i in 0 until 30) {
            val value = if (i % 3 == 0) Entry.YES_MANUAL else Entry.NO
            habit.originalEntries.add(Entry(today.minus(i), value))
        }
        habit.recompute()

        val summary1 = interactor.getInsightsForHabit(habit, 30)
        val summary2 = interactor.getInsightsForHabit(habit, 30)

        assertEquals(summary1.habitId, summary2.habitId)
        assertEquals(summary1.weekdayStats.size, summary2.weekdayStats.size)
        assertEquals(summary1.streakTrend, summary2.streakTrend)

        for (i in summary1.weekdayStats.indices) {
            assertEquals(summary1.weekdayStats[i].weekday, summary2.weekdayStats[i].weekday)
            assertEquals(summary1.weekdayStats[i].successRate, summary2.weekdayStats[i].successRate)
            assertEquals(summary1.weekdayStats[i].totalDays, summary2.weekdayStats[i].totalDays)
        }
    }

    @Test
    fun testInsightsSummaryStructure() {
        val habit = fixtures.createEmptyHabit()
        habit.name = "Test Habit"
        habit.id = 42L
        val today = DateUtils.getTodayWithOffset()

        for (i in 0 until 30) {
            habit.originalEntries.add(Entry(today.minus(i), Entry.YES_MANUAL))
        }
        habit.recompute()

        val summary = interactor.getInsightsForHabit(habit, 30)

        assertThat(summary.habitId, equalTo(42L))
        assertThat(summary.habitName, equalTo("Test Habit"))
        assertThat(summary.weekdayStats, not(empty()))
        assertNotNull(summary.streakTrend)
    }

    @Test
    fun testAllInsightsResultStructure() {
        val habit1 = fixtures.createEmptyHabit()
        habit1.name = "Habit 1"
        habit1.id = 1L
        val habit2 = fixtures.createEmptyHabit()
        habit2.name = "Habit 2"
        habit2.id = 2L

        habitList.add(habit1)
        habitList.add(habit2)

        val today = DateUtils.getTodayWithOffset()
        for (i in 0 until 50) {
            habit1.originalEntries.add(Entry(today.minus(i), Entry.YES_MANUAL))
            habit2.originalEntries.add(Entry(today.minus(i), Entry.NO))
        }
        habit1.recompute()
        habit2.recompute()

        val result = interactor.getInsightsForAllHabits(habitList, 50)

        assertNotNull(result.insights)
        assertNotNull(result.habitSummaries)
        assertNotNull(result.correlations)
        assertNotNull(result.suggestions)
        assertThat(result.habitSummaries.size, equalTo(2))
    }
}
