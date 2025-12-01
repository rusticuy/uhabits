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
package org.isoron.uhabits.core.commands

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.isoron.uhabits.core.BaseUnitTest
import org.isoron.uhabits.core.models.goals.Goal
import org.isoron.uhabits.core.models.goals.GoalHabitLink
import org.isoron.uhabits.core.models.goals.GoalList
import org.isoron.uhabits.core.models.goals.MemoryGoalList
import org.junit.Before
import org.junit.Test

class DeleteGoalCommandTest : BaseUnitTest() {
    private lateinit var goalList: GoalList

    @Before
    override fun setUp() {
        super.setUp()
        goalList = MemoryGoalList()
    }

    @Test
    fun testDeleteGoal() {
        val goal = Goal(name = "Delete Me", targetValue = 100.0)
        goalList.add(goal)
        
        assertThat(goalList.size(), equalTo(1))
        
        val deleteCommand = DeleteGoalsCommand(goalList, listOf(goal))
        deleteCommand.run()
        
        assertThat(goalList.size(), equalTo(0))
    }

    @Test
    fun testDeleteGoalCleansUpLinks() {
        val goal = Goal(name = "Goal with Links", targetValue = 100.0)
        goalList.add(goal)
        
        val habit = fixtures.createEmptyHabit()
        habitList.add(habit)
        
        val link = GoalHabitLink(goalId = goal.id, habitId = habit.id, weight = 1.0)
        goalList.addHabitLink(link)
        
        assertThat(goalList.getLinkedHabits(goal.id!!).size, equalTo(1))
        
        val deleteCommand = DeleteGoalsCommand(goalList, listOf(goal))
        deleteCommand.run()
        
        assertThat(goalList.size(), equalTo(0))
        assertThat(goalList.getLinkedHabits(goal.id!!).size, equalTo(0))
    }
}
