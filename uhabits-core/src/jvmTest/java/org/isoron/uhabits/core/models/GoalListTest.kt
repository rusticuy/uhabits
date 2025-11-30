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
import org.isoron.uhabits.core.models.goals.Goal
import org.isoron.uhabits.core.models.goals.MemoryGoalList
import org.junit.Before
import org.junit.Test

class GoalListTest : BaseUnitTest() {
    private lateinit var testGoalList: MemoryGoalList

    @Before
    override fun setUp() {
        super.setUp()
        testGoalList = MemoryGoalList()
    }

    @Test
    fun testAddGoal() {
        val goal = Goal(name = "Test Goal", targetValue = 100.0)
        testGoalList.add(goal)
        
        assertThat(goal.id, notNullValue())
        assertThat(testGoalList.getById(goal.id!!), equalTo(goal))
    }

    @Test
    fun testCreateGoalWithName() {
        val goal = Goal(name = "Learn Guitar", targetValue = 100.0)
        testGoalList.add(goal)
        
        val retrieved = testGoalList.getById(goal.id!!)
        assertThat(retrieved?.name, equalTo("Learn Guitar"))
    }

    @Test
    fun testCreateMultipleGoals() {
        val goal1 = Goal(name = "Goal 1", targetValue = 100.0)
        val goal2 = Goal(name = "Goal 2", targetValue = 50.0)
        testGoalList.add(goal1)
        testGoalList.add(goal2)
        
        assertThat(testGoalList.size(), equalTo(2))
        assertThat(testGoalList.getById(goal1.id!!), equalTo(goal1))
        assertThat(testGoalList.getById(goal2.id!!), equalTo(goal2))
    }
}
