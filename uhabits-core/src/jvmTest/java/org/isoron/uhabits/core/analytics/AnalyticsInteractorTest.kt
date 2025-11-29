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
package org.isoron.uhabits.core.analytics

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.greaterThan
import org.hamcrest.Matchers.greaterThanOrEqualTo
import org.hamcrest.core.IsEqual.equalTo
import org.isoron.uhabits.core.BaseUnitTest
import org.isoron.uhabits.core.models.Entry
import org.isoron.uhabits.core.models.NumericalHabitType
import org.isoron.uhabits.core.utils.DateUtils
import org.junit.Test
import kotlin.test.assertTrue

class AnalyticsInteractorTest : BaseUnitTest() {

    @Test
    fun testBuildDashboardState() {
        val habit = fixtures.createEmptyHabit()
        val today = DateUtils.getTodayWithOffset()

        for (i in 0..30) {
            val value = if (i % 2 == 0) Entry.YES_MANUAL else Entry.NO
            habit.originalEntries.add(Entry(today.minus(i), value))
        }
        habit.recompute()

        val interactor = AnalyticsInteractor()
        val state = interactor.buildDashboardState(habit)

        assertTrue(state.heatmapData.isNotEmpty())
        assertTrue(state.trendData.isNotEmpty())
        assertTrue(state.chainTimeline.isNotEmpty())
    }

    @Test
    fun testBuildDashboardStateWithCustomParameters() {
        val habit = fixtures.createLongHabit()

        val interactor = AnalyticsInteractor()
        val state = interactor.buildDashboardState(
            habit = habit,
            heatmapDaysBack = 100,
            trendWindowSize = 14,
            trendDaysBack = 100,
            chainMaxDaysBack = 500
        )

        assertThat(state.heatmapData.size, greaterThan(0))
        assertThat(state.trendData.size, greaterThan(0))
    }

    @Test
    fun testBuildDashboardStateStreakCalculation() {
        val habit = fixtures.createEmptyHabit()
        val today = DateUtils.getTodayWithOffset()

        for (i in 0..14) {
            habit.originalEntries.add(Entry(today.minus(i), Entry.YES_MANUAL))
        }
        habit.recompute()

        val interactor = AnalyticsInteractor()
        val state = interactor.buildDashboardState(habit)

        assertTrue(state.currentStreakLength > 0)
        assertTrue(state.longestStreakLength >= state.currentStreakLength)
    }

    @Test
    fun testGetHeatmapPageFirstPage() {
        val habit = fixtures.createLongHabit()

        val interactor = AnalyticsInteractor()
        val page = interactor.getHeatmapPage(habit, page = 0, pageSize = 365)

        assertTrue(page.isNotEmpty())
    }

    @Test
    fun testGetHeatmapPageMultiplePages() {
        val habit = fixtures.createLongHabit()

        val interactor = AnalyticsInteractor()
        val page0 = interactor.getHeatmapPage(habit, page = 0, pageSize = 365)
        val page1 = interactor.getHeatmapPage(habit, page = 1, pageSize = 365)

        assertTrue(page0.isNotEmpty())
        if (page1.isNotEmpty()) {
            assertTrue(page0.none { it.timestamp == page1.first().timestamp })
        }
    }

    @Test
    fun testBuildDashboardStateWithNumericalHabit() {
        val habit = fixtures.createEmptyNumericalHabit(NumericalHabitType.AT_LEAST)
        habit.targetValue = 5.0
        val today = DateUtils.getTodayWithOffset()

        for (i in 0..20) {
            habit.originalEntries.add(Entry(today.minus(i), (i * 500).toInt()))
        }
        habit.recompute()

        val interactor = AnalyticsInteractor()
        val state = interactor.buildDashboardState(habit)

        assertTrue(state.heatmapData.isNotEmpty())
        assertTrue(state.trendData.isNotEmpty())
    }

    @Test
    fun testBuildDashboardStateEmptyHabit() {
        val habit = fixtures.createEmptyHabit()

        val interactor = AnalyticsInteractor()
        val state = interactor.buildDashboardState(habit)

        assertThat(state.heatmapData.size, greaterThanOrEqualTo(0))
        assertThat(state.trendData.size, greaterThanOrEqualTo(0))
        assertThat(state.chainTimeline.size, equalTo(0))
    }

    @Test
    fun testBuildDashboardStateCurrentStreak() {
        val habit = fixtures.createEmptyHabit()
        val today = DateUtils.getTodayWithOffset()

        for (i in 0..9) {
            habit.originalEntries.add(Entry(today.minus(i), Entry.YES_MANUAL))
        }
        habit.recompute()

        val interactor = AnalyticsInteractor()
        val state = interactor.buildDashboardState(habit)

        assertThat(state.currentStreakLength, greaterThan(0))
    }

    @Test
    fun testBuildDashboardStateLongestStreak() {
        val habit = fixtures.createEmptyHabit()
        val today = DateUtils.getTodayWithOffset()

        for (i in 0..4) {
            habit.originalEntries.add(Entry(today.minus(i), Entry.YES_MANUAL))
        }
        for (i in 10..24) {
            habit.originalEntries.add(Entry(today.minus(i), Entry.YES_MANUAL))
        }
        habit.recompute()

        val interactor = AnalyticsInteractor()
        val state = interactor.buildDashboardState(habit)

        assertTrue(state.longestStreakLength >= 15)
    }

    @Test
    fun testGetHeatmapPageDifferentPageSizes() {
        val habit = fixtures.createLongHabit()

        val interactor = AnalyticsInteractor()
        val page100 = interactor.getHeatmapPage(habit, page = 0, pageSize = 100)
        val page365 = interactor.getHeatmapPage(habit, page = 0, pageSize = 365)

        assertTrue(page100.isNotEmpty())
        assertTrue(page365.isNotEmpty())
    }
}
