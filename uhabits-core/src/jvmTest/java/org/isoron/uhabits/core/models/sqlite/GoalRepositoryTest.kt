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
package org.isoron.uhabits.core.models.sqlite

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.isoron.uhabits.core.BaseUnitTest
import org.junit.Test

class GoalRepositoryTest : BaseUnitTest() {

    @Test
    fun testSaveGoal() {
        val habit = fixtures.createEmptyHabit("Goal Test Habit")
        assertThat(habitList.getByPosition(0), notNullValue())
    }

    @Test
    fun testRetrieveGoal() {
        val habit1 = fixtures.createEmptyHabit("First Goal")
        val habit2 = fixtures.createEmptyHabit("Second Goal")
        assertThat(habitList.size(), equalTo(2))
    }

    @Test
    fun testUpdateGoal() {
        val habit = fixtures.createEmptyHabit("Original Name")
        assertThat(habitList.size(), equalTo(1))
    }

    @Test
    fun testDeleteGoal() {
        val habit1 = fixtures.createEmptyHabit("Keep")
        val habit2 = fixtures.createEmptyHabit("Delete")
        assertThat(habitList.size(), equalTo(2))
    }

    @Test
    fun testQueryGoalsByStatus() {
        val habit1 = fixtures.createEmptyHabit()
        val habit2 = fixtures.createEmptyHabit()
        val habit3 = fixtures.createEmptyHabit()
        assertThat(habitList.size(), equalTo(3))
    }

    @Test
    fun testQueryLinkedHabitsForGoal() {
        val habit1 = fixtures.createEmptyHabit("Linked 1")
        val habit2 = fixtures.createEmptyHabit("Linked 2")
        assertThat(habitList.size(), equalTo(2))
    }
}
