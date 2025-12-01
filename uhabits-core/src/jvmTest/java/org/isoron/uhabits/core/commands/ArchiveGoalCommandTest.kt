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
import org.isoron.uhabits.core.models.goals.GoalList
import org.isoron.uhabits.core.models.goals.MemoryGoalList
import org.junit.Before
import org.junit.Test

class ArchiveGoalCommandTest : BaseUnitTest() {
    private lateinit var goalList: GoalList

    @Before
    override fun setUp() {
        super.setUp()
        goalList = MemoryGoalList()
    }

    @Test
    fun testArchiveGoal() {
        val goal = Goal(name = "Test Goal", targetValue = 100.0, isArchived = false)
        goalList.add(goal)
        
        assertThat(goal.isArchived, equalTo(false))
        
        val archiveCommand = ArchiveGoalsCommand(goalList, listOf(goal))
        archiveCommand.run()
        
        assertThat(goal.isArchived, equalTo(true))
    }

    @Test
    fun testMultipleGoalsArchived() {
        val goal1 = Goal(name = "Goal 1", targetValue = 100.0)
        val goal2 = Goal(name = "Goal 2", targetValue = 50.0)
        goalList.add(goal1)
        goalList.add(goal2)
        
        assertThat(goal1.isArchived, equalTo(false))
        assertThat(goal2.isArchived, equalTo(false))
        
        val archiveCommand = ArchiveGoalsCommand(goalList, listOf(goal1, goal2))
        archiveCommand.run()
        
        assertThat(goal1.isArchived, equalTo(true))
        assertThat(goal2.isArchived, equalTo(true))
    }

    @Test
    fun testSelectiveArchiveGoals() {
        val goal1 = Goal(name = "Archive Me", targetValue = 100.0)
        val goal2 = Goal(name = "Keep Me Active", targetValue = 100.0)
        goalList.add(goal1)
        goalList.add(goal2)
        
        val archiveCommand = ArchiveGoalsCommand(goalList, listOf(goal1))
        archiveCommand.run()
        
        assertThat(goal1.isArchived, equalTo(true))
        assertThat(goal2.isArchived, equalTo(false))
    }
}
