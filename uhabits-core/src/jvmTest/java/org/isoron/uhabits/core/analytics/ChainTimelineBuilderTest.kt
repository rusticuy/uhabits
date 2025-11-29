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

class ChainTimelineBuilderTest : BaseUnitTest() {

    @Test
    fun testBuildChainForSimpleHabit() {
        val habit = fixtures.createEmptyHabit()
        val today = DateUtils.getTodayWithOffset()

        for (i in 0..9) {
            habit.originalEntries.add(Entry(today.minus(i), Entry.YES_MANUAL))
        }
        habit.recompute()

        val builder = ChainTimelineBuilder()
        val timeline = builder.build(habit)

        assertTrue(timeline.isNotEmpty())
        assertTrue(timeline.any { it.length == 10 })
    }

    @Test
    fun testBuildChainWithMultipleSegments() {
        val habit = fixtures.createEmptyHabit()
        val today = DateUtils.getTodayWithOffset()

        for (i in 0..4) {
            habit.originalEntries.add(Entry(today.minus(i), Entry.YES_MANUAL))
        }
        for (i in 10..14) {
            habit.originalEntries.add(Entry(today.minus(i), Entry.YES_MANUAL))
        }
        habit.recompute()

        val builder = ChainTimelineBuilder()
        val timeline = builder.build(habit)

        assertThat(timeline.size, greaterThan(1))
        assertTrue(timeline.any { it.length == 5 })
        assertTrue(timeline.any { it.length == 5 })
    }

    @Test
    fun testBuildChainBeyond30Days() {
        val habit = fixtures.createLongHabit()

        val builder = ChainTimelineBuilder()
        val timeline = builder.build(habit, maxDaysBack = 3650)

        assertTrue(timeline.isNotEmpty())
        assertTrue(timeline.any { it.length > 30 })
    }

    @Test
    fun testBuildChainForNumericalHabit() {
        val habit = fixtures.createEmptyNumericalHabit(NumericalHabitType.AT_LEAST)
        habit.targetValue = 2.0
        val today = DateUtils.getTodayWithOffset()

        for (i in 0..9) {
            habit.originalEntries.add(Entry(today.minus(i), (3000 + i * 100)))
        }
        habit.recompute()

        val builder = ChainTimelineBuilder()
        val timeline = builder.build(habit)

        assertTrue(timeline.isNotEmpty())
        assertTrue(timeline.any { it.length == 10 })
    }

    @Test
    fun testBuildChainForNumericalHabitAtMost() {
        val habit = fixtures.createEmptyNumericalHabit(NumericalHabitType.AT_MOST)
        habit.targetValue = 2.0
        val today = DateUtils.getTodayWithOffset()

        for (i in 0..9) {
            habit.originalEntries.add(Entry(today.minus(i), (1000 + i * 100)))
        }
        habit.recompute()

        val builder = ChainTimelineBuilder()
        val timeline = builder.build(habit)

        assertTrue(timeline.isNotEmpty())
    }

    @Test
    fun testBuildChainSkipsSkippedDays() {
        val habit = fixtures.createEmptyHabit()
        val today = DateUtils.getTodayWithOffset()

        for (i in 0..9) {
            if (i != 5) {
                habit.originalEntries.add(Entry(today.minus(i), Entry.YES_MANUAL))
            }
        }
        habit.recompute()

        val builder = ChainTimelineBuilder()
        val timeline = builder.build(habit)

        assertTrue(timeline.size >= 2)
    }

    @Test
    fun testBuildChainEmptyHabit() {
        val habit = fixtures.createEmptyHabit()

        val builder = ChainTimelineBuilder()
        val timeline = builder.build(habit)

        assertThat(timeline.size, equalTo(0))
    }

    @Test
    fun testBuildChainChronologicalOrder() {
        val habit = fixtures.createEmptyHabit()
        val today = DateUtils.getTodayWithOffset()

        for (i in 0..4) {
            habit.originalEntries.add(Entry(today.minus(i), Entry.YES_MANUAL))
        }
        for (i in 10..14) {
            habit.originalEntries.add(Entry(today.minus(i), Entry.YES_MANUAL))
        }
        habit.recompute()

        val builder = ChainTimelineBuilder()
        val timeline = builder.build(habit)

        assertTrue(timeline.isNotEmpty())
        for (i in 0 until timeline.size - 1) {
            assertTrue(timeline[i].end.compareTo(timeline[i + 1].end) >= 0)
        }
    }

    @Test
    fun testBuildChainMaxDaysBackLimit() {
        val habit = fixtures.createLongHabit()

        val builder = ChainTimelineBuilder()
        val timelineLimited = builder.build(habit, maxDaysBack = 100)
        val timelineUnlimited = builder.build(habit, maxDaysBack = 3650)

        assertTrue(timelineLimited.isNotEmpty())
        assertTrue(timelineUnlimited.isNotEmpty())
    }

    @Test
    fun testBuildChainSingleDay() {
        val habit = fixtures.createEmptyHabit()
        val today = DateUtils.getTodayWithOffset()

        habit.originalEntries.add(Entry(today.minus(0), Entry.YES_MANUAL))
        habit.recompute()

        val builder = ChainTimelineBuilder()
        val timeline = builder.build(habit)

        assertThat(timeline.size, equalTo(1))
        assertThat(timeline[0].length, equalTo(1))
    }

    @Test
    fun testBuildChainWithSkippedDaysAsBreak() {
        val habit = fixtures.createEmptyHabit()
        val today = DateUtils.getTodayWithOffset()

        habit.originalEntries.add(Entry(today.minus(0), Entry.YES_MANUAL))
        habit.originalEntries.add(Entry(today.minus(2), Entry.YES_MANUAL))
        habit.recompute()

        val builder = ChainTimelineBuilder()
        val timeline = builder.build(habit)

        assertTrue(timeline.size >= 2)
        assertTrue(timeline.any { it.length == 1 })
    }
}
