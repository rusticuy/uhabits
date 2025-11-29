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
import org.hamcrest.Matchers.closeTo
import org.hamcrest.Matchers.greaterThan
import org.hamcrest.core.IsEqual.equalTo
import org.isoron.uhabits.core.BaseUnitTest
import org.isoron.uhabits.core.models.Entry
import org.isoron.uhabits.core.models.NumericalHabitType
import org.isoron.uhabits.core.utils.DateUtils
import org.junit.Test
import kotlin.test.assertTrue

class HeatmapGeneratorTest : BaseUnitTest() {

    @Test
    fun testGenerateHeatmapForBooleanHabit() {
        val habit = fixtures.createEmptyHabit()
        val today = DateUtils.getTodayWithOffset()

        habit.originalEntries.add(Entry(today.minus(10), Entry.YES_MANUAL))
        habit.originalEntries.add(Entry(today.minus(5), Entry.YES_MANUAL))
        habit.originalEntries.add(Entry(today.minus(0), Entry.NO))
        habit.recompute()

        val generator = HeatmapGenerator()
        val heatmap = generator.generate(habit, fromDaysAgo = 20, toDaysAgo = 0)

        assertThat(heatmap.size, equalTo(21))
        assertTrue(heatmap.any { it.value > 0.5 })
        assertTrue(heatmap.any { it.value == 0.0 })
    }

    @Test
    fun testGenerateHeatmapForNumericalHabit() {
        val habit = fixtures.createEmptyNumericalHabit(NumericalHabitType.AT_LEAST)
        habit.targetValue = 5.0
        val today = DateUtils.getTodayWithOffset()

        habit.originalEntries.add(Entry(today.minus(10), 3000))
        habit.originalEntries.add(Entry(today.minus(5), 6000))
        habit.originalEntries.add(Entry(today.minus(0), 0))
        habit.recompute()

        val generator = HeatmapGenerator()
        val heatmap = generator.generate(habit, fromDaysAgo = 20, toDaysAgo = 0)

        assertThat(heatmap.size, equalTo(21))
        val completedDay = heatmap.find { it.timestamp == today.minus(5) }
        assertThat(completedDay!!.value, closeTo(1.0, 0.01))
    }

    @Test
    fun testHeatmapValueNormalization() {
        val habit = fixtures.createEmptyNumericalHabit(NumericalHabitType.AT_LEAST)
        habit.targetValue = 10.0
        val today = DateUtils.getTodayWithOffset()

        habit.originalEntries.add(Entry(today.minus(10), 5000))
        habit.recompute()

        val generator = HeatmapGenerator()
        val heatmap = generator.generate(habit, fromDaysAgo = 20, toDaysAgo = 0)

        val cell = heatmap.find { it.timestamp == today.minus(10) }!!
        assertThat(cell.value, closeTo(0.5, 0.01))
    }

    @Test
    fun testHeatmapSkipMarksAsHalf() {
        val habit = fixtures.createEmptyHabit()
        val today = DateUtils.getTodayWithOffset()

        habit.originalEntries.add(Entry(today.minus(10), Entry.SKIP))
        habit.recompute()

        val generator = HeatmapGenerator()
        val heatmap = generator.generate(habit, fromDaysAgo = 20, toDaysAgo = 0)

        val cell = heatmap.find { it.timestamp == today.minus(10) }!!
        assertThat(cell.value, closeTo(0.5, 0.01))
    }

    @Test
    fun testHeatmapUnknownMarksAsZero() {
        val habit = fixtures.createEmptyHabit()
        val today = DateUtils.getTodayWithOffset()

        habit.originalEntries.add(Entry(today.minus(10), Entry.UNKNOWN))
        habit.recompute()

        val generator = HeatmapGenerator()
        val heatmap = generator.generate(habit, fromDaysAgo = 20, toDaysAgo = 0)

        val cell = heatmap.find { it.timestamp == today.minus(10) }!!
        assertThat(cell.value, equalTo(0.0))
    }

    @Test
    fun testGenerateHeatmapWithDateRange() {
        val habit = fixtures.createLongHabit()
        val generator = HeatmapGenerator()

        val heatmap1 = generator.generate(habit, fromDaysAgo = 100, toDaysAgo = 0)
        val heatmap2 = generator.generate(habit, fromDaysAgo = 365, toDaysAgo = 100)

        assertThat(heatmap1.size, greaterThan(0))
        assertThat(heatmap2.size, greaterThan(0))
    }
}
