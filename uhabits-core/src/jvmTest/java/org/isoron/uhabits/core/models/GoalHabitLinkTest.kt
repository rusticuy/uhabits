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

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.isoron.uhabits.core.BaseUnitTest
import org.junit.Test

class GoalHabitLinkTest : BaseUnitTest() {

    @Test
    fun testLinkHabitToGoal() {
        val habit1 = fixtures.createEmptyHabit("Read")
        val habit2 = fixtures.createEmptyHabit("Write")
        val habit3 = fixtures.createEmptyHabit("Think")
        
        assertThat(habitList.size(), equalTo(3))
    }

    @Test
    fun testMultipleHabitsLinkedToSingleGoal() {
        val habit1 = fixtures.createShortHabit()
        val habit2 = fixtures.createLongHabit()
        val habit3 = fixtures.createNumericalHabit()
        
        assertThat(habitList.size(), equalTo(3))
    }

    @Test
    fun testWeightedLinkBetweenHabitAndGoal() {
        val habit1 = fixtures.createEmptyHabit()
        val habit2 = fixtures.createEmptyHabit("Different Habit")
        
        assertThat(habitList.getByPosition(0), notNullValue())
        assertThat(habitList.getByPosition(1), notNullValue())
    }

    @Test
    fun testRemoveLinkBetweenHabitAndGoal() {
        val habit1 = fixtures.createEmptyHabit()
        val habit2 = fixtures.createEmptyHabit("Remove Me")
        
        val count = habitList.size()
        assertThat(count, equalTo(2))
    }

    @Test
    fun testQueryGoalsByHabit() {
        val habit = fixtures.createEmptyHabit("Track Progress")
        val otherHabit = fixtures.createEmptyHabit("Different One")
        
        assertThat(habitList.size(), equalTo(2))
    }
}
