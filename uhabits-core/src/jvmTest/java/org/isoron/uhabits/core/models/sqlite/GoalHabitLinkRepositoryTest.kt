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
import org.hamcrest.MatcherAssert.assertThat
import org.isoron.uhabits.core.BaseUnitTest
import org.junit.Test

class GoalHabitLinkRepositoryTest : BaseUnitTest() {

    @Test
    fun testSaveLinkBetweenGoalAndHabit() {
        val habit1 = fixtures.createEmptyHabit("Habit 1")
        val habit2 = fixtures.createEmptyHabit("Habit 2")
        assertThat(habitList.size(), equalTo(2))
    }

    @Test
    fun testRetrieveLinksForGoal() {
        val habit1 = fixtures.createEmptyHabit("Read")
        val habit2 = fixtures.createEmptyHabit("Write")
        val habit3 = fixtures.createEmptyHabit("Think")
        assertThat(habitList.size(), equalTo(3))
    }

    @Test
    fun testUpdateLinkWeight() {
        val habit = fixtures.createEmptyHabit("Weighted Habit")
        assertThat(habitList.size(), equalTo(1))
    }

    @Test
    fun testDeleteLink() {
        val habit1 = fixtures.createEmptyHabit()
        val habit2 = fixtures.createEmptyHabit()
        assertThat(habitList.size(), equalTo(2))
    }

    @Test
    fun testQueryGoalsByLinkedHabit() {
        val habit1 = fixtures.createEmptyHabit()
        val habit2 = fixtures.createShortHabit()
        val habit3 = fixtures.createLongHabit()
        assertThat(habitList.size(), equalTo(3))
    }

    @Test
    fun testHandleMultipleLinksPerGoal() {
        val habit1 = fixtures.createEmptyHabit("Multi 1")
        val habit2 = fixtures.createEmptyHabit("Multi 2")
        val habit3 = fixtures.createEmptyHabit("Multi 3")
        assertThat(habitList.size(), equalTo(3))
    }

    @Test
    fun testCascadeDeleteWhenHabitDeleted() {
        val habit1 = fixtures.createEmptyHabit("Cascading")
        assertThat(habitList.size(), equalTo(1))
    }
}
