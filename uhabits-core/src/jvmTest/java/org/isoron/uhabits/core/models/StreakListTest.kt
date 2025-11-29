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
package org.isoron.uhabits.core.models

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.greaterThan
import org.hamcrest.core.IsEqual.equalTo
import org.isoron.uhabits.core.BaseUnitTest
import org.isoron.uhabits.core.utils.DateUtils
import org.junit.Test
import kotlin.test.assertTrue

class StreakListTest : BaseUnitTest() {

    @Test
    fun testGetTimelineReturnsAllStreaks() {
        val habit = fixtures.createEmptyHabit()
        val today = DateUtils.getTodayWithOffset()

        for (i in 0..4) {
            habit.originalEntries.add(Entry(today.minus(i), Entry.YES_MANUAL))
        }
        for (i in 10..14) {
            habit.originalEntries.add(Entry(today.minus(i), Entry.YES_MANUAL))
        }
        habit.recompute()

        val timeline = habit.streaks.getTimeline()

        assertThat(timeline.size, greaterThan(0))
        assertTrue(timeline.any { it.length == 5 })
    }

    @Test
    fun testGetTimelineOrderedByRecency() {
        val habit = fixtures.createEmptyHabit()
        val today = DateUtils.getTodayWithOffset()

        for (i in 0..4) {
            habit.originalEntries.add(Entry(today.minus(i), Entry.YES_MANUAL))
        }
        for (i in 10..14) {
            habit.originalEntries.add(Entry(today.minus(i), Entry.YES_MANUAL))
        }
        habit.recompute()

        val timeline = habit.streaks.getTimeline()

        assertTrue(timeline.isNotEmpty())
        for (i in 0 until timeline.size - 1) {
            assertTrue(timeline[i].end.compareTo(timeline[i + 1].end) >= 0)
        }
    }

    @Test
    fun testGetTimelineBeyond30Days() {
        val habit = fixtures.createLongHabit()

        val timeline = habit.streaks.getTimeline()

        assertTrue(timeline.isNotEmpty())
        assertTrue(timeline.any { it.length > 30 })
    }

    @Test
    fun testGetTimelineWithSingleStreak() {
        val habit = fixtures.createEmptyHabit()
        val today = DateUtils.getTodayWithOffset()

        for (i in 0..9) {
            habit.originalEntries.add(Entry(today.minus(i), Entry.YES_MANUAL))
        }
        habit.recompute()

        val timeline = habit.streaks.getTimeline()

        assertThat(timeline.size, equalTo(1))
        assertThat(timeline[0].length, equalTo(10))
    }

    @Test
    fun testGetTimelineEmptyHabit() {
        val habit = fixtures.createEmptyHabit()

        val timeline = habit.streaks.getTimeline()

        assertThat(timeline.size, equalTo(0))
    }

    @Test
    fun testGetTimelinePreservesAllStreaks() {
        val habit = fixtures.createEmptyHabit()
        val today = DateUtils.getTodayWithOffset()

        for (i in 0..2) {
            habit.originalEntries.add(Entry(today.minus(i), Entry.YES_MANUAL))
        }
        for (i in 5..7) {
            habit.originalEntries.add(Entry(today.minus(i), Entry.YES_MANUAL))
        }
        for (i in 10..12) {
            habit.originalEntries.add(Entry(today.minus(i), Entry.YES_MANUAL))
        }
        habit.recompute()

        val timeline = habit.streaks.getTimeline()

        assertThat(timeline.size, greaterThan(2))
    }

    @Test
    fun testGetTimelineVsGetBest() {
        val habit = fixtures.createLongHabit()

        val timeline = habit.streaks.getTimeline()
        val best = habit.streaks.getBest(5)

        assertTrue(timeline.isNotEmpty())
        assertTrue(best.isNotEmpty())
    }
}
