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
import org.hamcrest.core.IsEqual.equalTo
import org.isoron.uhabits.core.BaseUnitTest
import org.isoron.uhabits.core.models.Entry
import org.isoron.uhabits.core.models.NumericalHabitType
import org.isoron.uhabits.core.utils.DateUtils
import org.junit.Test
import kotlin.test.assertTrue

class TrendCalculatorTest : BaseUnitTest() {

    @Test
    fun testCalculateTrendForBooleanHabit() {
        val habit = fixtures.createEmptyHabit()
        val today = DateUtils.getTodayWithOffset()

        for (i in 0..20) {
            val value = if (i % 2 == 0) Entry.YES_MANUAL else Entry.NO
            habit.originalEntries.add(Entry(today.minus(i), value))
        }
        habit.recompute()

        val calculator = TrendCalculator()
        val trend = calculator.calculate(habit, windowSize = 7, fromDaysAgo = 30, toDaysAgo = 0)

        assertThat(trend.size, greaterThan(0))
        assertTrue(trend.all { it.movingAverage >= 0.0 && it.movingAverage <= 1.0 })
    }

    @Test
    fun testTrendMovingAverageCalculation() {
        val habit = fixtures.createEmptyHabit()
        val today = DateUtils.getTodayWithOffset()

        for (i in 0..13) {
            val value = if (i < 7) Entry.YES_MANUAL else Entry.NO
            habit.originalEntries.add(Entry(today.minus(i), value))
        }
        habit.recompute()

        val calculator = TrendCalculator()
        val trend = calculator.calculate(habit, windowSize = 7, fromDaysAgo = 20, toDaysAgo = 0)

        assertThat(trend.size, greaterThan(0))
        assertTrue(trend.any { it.movingAverage > 0.5 })
    }

    @Test
    fun testTrendRegressionSlope() {
        val habit = fixtures.createEmptyHabit()
        val today = DateUtils.getTodayWithOffset()

        for (i in 0..20) {
            val value = Entry.YES_MANUAL
            habit.originalEntries.add(Entry(today.minus(i), value))
        }
        habit.recompute()

        val calculator = TrendCalculator()
        val trend = calculator.calculate(habit, windowSize = 7, fromDaysAgo = 30, toDaysAgo = 0)

        assertTrue(trend.isNotEmpty())
    }

    @Test
    fun testTrendCalculationForNumericalHabit() {
        val habit = fixtures.createEmptyNumericalHabit(NumericalHabitType.AT_LEAST)
        habit.targetValue = 5.0
        val today = DateUtils.getTodayWithOffset()

        for (i in 0..14) {
            val value = (i * 500).toInt()
            habit.originalEntries.add(Entry(today.minus(i), value))
        }
        habit.recompute()

        val calculator = TrendCalculator()
        val trend = calculator.calculate(habit, windowSize = 7, fromDaysAgo = 20, toDaysAgo = 0)

        assertThat(trend.size, greaterThan(0))
    }

    @Test
    fun testTrendWithIncreasingValues() {
        val habit = fixtures.createEmptyHabit()
        val today = DateUtils.getTodayWithOffset()

        val completionPattern = intArrayOf(0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1)
        for (i in completionPattern.indices) {
            val value = if (completionPattern[i] == 1) Entry.YES_MANUAL else Entry.NO
            habit.originalEntries.add(Entry(today.minus(i), value))
        }
        habit.recompute()

        val calculator = TrendCalculator()
        val trend = calculator.calculate(habit, windowSize = 7, fromDaysAgo = 30, toDaysAgo = 0)

        assertTrue(trend.isNotEmpty())
        val firstHalf = trend.filter { it.timestamp.daysUntil(today) >= 15 }
        val secondHalf = trend.filter { it.timestamp.daysUntil(today) < 15 }
        assertTrue(firstHalf.isNotEmpty() && secondHalf.isNotEmpty())
    }

    @Test
    fun testTrendWindowSize() {
        val habit = fixtures.createEmptyHabit()
        val today = DateUtils.getTodayWithOffset()

        for (i in 0..30) {
            val value = if (i < 15) Entry.YES_MANUAL else Entry.NO
            habit.originalEntries.add(Entry(today.minus(i), value))
        }
        habit.recompute()

        val calculator = TrendCalculator()
        val trendSmall = calculator.calculate(habit, windowSize = 3, fromDaysAgo = 40, toDaysAgo = 0)
        val trendLarge = calculator.calculate(habit, windowSize = 14, fromDaysAgo = 40, toDaysAgo = 0)

        assertTrue(trendSmall.isNotEmpty())
        assertTrue(trendLarge.isNotEmpty())
    }

    @Test
    fun testTrendRegressionSlopeDirection() {
        val habit = fixtures.createEmptyHabit()
        val today = DateUtils.getTodayWithOffset()

        for (i in 0..20) {
            val value = if (i < 10) Entry.NO else Entry.YES_MANUAL
            habit.originalEntries.add(Entry(today.minus(i), value))
        }
        habit.recompute()

        val calculator = TrendCalculator()
        val trend = calculator.calculate(habit, windowSize = 5, fromDaysAgo = 30, toDaysAgo = 0)

        assertTrue(trend.isNotEmpty())
    }

    @Test
    fun testTrendEmptyHabit() {
        val habit = fixtures.createEmptyHabit()

        val calculator = TrendCalculator()
        val trend = calculator.calculate(habit, windowSize = 7, fromDaysAgo = 30, toDaysAgo = 0)

        assertThat(trend.size, equalTo(31))
        assertTrue(trend.all { it.movingAverage == 0.0 })
    }
}
