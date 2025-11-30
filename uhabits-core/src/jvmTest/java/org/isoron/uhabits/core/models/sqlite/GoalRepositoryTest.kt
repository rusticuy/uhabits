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
import org.isoron.uhabits.core.models.goals.Goal
import org.isoron.uhabits.core.models.goals.GoalHabitLink
import org.isoron.uhabits.core.models.goals.MemoryGoalList
import org.junit.Before
import org.junit.Test

class GoalRepositoryTest : BaseUnitTest() {
    private lateinit var testGoalList: MemoryGoalList

    @Before
    override fun setUp() {
        super.setUp()
        testGoalList = MemoryGoalList()
    }

    @Test
    fun testSaveGoal() {
        val goal = Goal(name = "Test Goal", targetValue = 100.0)
        testGoalList.add(goal)
        
        assertThat(testGoalList.getById(goal.id!!), notNullValue())
    }

    @Test
    fun testRetrieveGoal() {
        val goal1 = Goal(name = "First Goal", targetValue = 100.0)
        val goal2 = Goal(name = "Second Goal", targetValue = 50.0)
        testGoalList.add(goal1)
        testGoalList.add(goal2)
        
        assertThat(testGoalList.size(), equalTo(2))
        assertThat(testGoalList.getById(goal1.id!!), notNullValue())
        assertThat(testGoalList.getById(goal2.id!!), notNullValue())
    }

    @Test
    fun testUpdateGoal() {
        val goal = Goal(name = "Original Name", targetValue = 100.0)
        testGoalList.add(goal)
        
        goal.name = "Updated Name"
        testGoalList.update(goal)
        
        val retrieved = testGoalList.getById(goal.id!!)
        assertThat(retrieved?.name, equalTo("Updated Name"))
    }

    @Test
    fun testDeleteGoal() {
        val goal1 = Goal(name = "Keep", targetValue = 100.0)
        val goal2 = Goal(name = "Delete", targetValue = 100.0)
        testGoalList.add(goal1)
        testGoalList.add(goal2)
        
        assertThat(testGoalList.size(), equalTo(2))
        
        testGoalList.remove(goal2)
        
        assertThat(testGoalList.size(), equalTo(1))
        assertThat(testGoalList.getById(goal2.id!!), equalTo(null))
    }

    @Test
    fun testQueryGoalsByStatus() {
        val goal1 = Goal(name = "Active 1", targetValue = 100.0)
        val goal2 = Goal(name = "Active 2", targetValue = 100.0)
        val goal3 = Goal(name = "Archived", targetValue = 100.0, isArchived = true)
        
        testGoalList.add(goal1)
        testGoalList.add(goal2)
        testGoalList.add(goal3)
        
        assertThat(testGoalList.size(), equalTo(3))
    }

    @Test
    fun testQueryLinkedHabitsForGoal() {
        val goal = Goal(name = "Linked Goal", targetValue = 100.0)
        testGoalList.add(goal)
        
        val habit1 = fixtures.createEmptyHabit("Linked 1")
        val habit2 = fixtures.createEmptyHabit("Linked 2")
        
        val link1 = GoalHabitLink(goalId = goal.id, habitId = habit1.id, weight = 1.0)
        val link2 = GoalHabitLink(goalId = goal.id, habitId = habit2.id, weight = 1.5)
        
        testGoalList.addHabitLink(link1)
        testGoalList.addHabitLink(link2)
        
        val links = testGoalList.getLinkedHabits(goal.id!!)
        assertThat(links.size, equalTo(2))
    }
}
