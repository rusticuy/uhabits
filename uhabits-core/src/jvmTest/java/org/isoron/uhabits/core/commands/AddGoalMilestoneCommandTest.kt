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
import org.isoron.uhabits.core.models.goals.GoalMilestone
import org.isoron.uhabits.core.models.goals.MemoryGoalList
import org.junit.Before
import org.junit.Test
import kotlin.test.assertTrue

class AddGoalMilestoneCommandTest : BaseUnitTest() {
    private lateinit var goalList: GoalList
    private lateinit var command: AddGoalMilestoneCommand
    private lateinit var milestone: GoalMilestone
    private var goalId: Long = 0

    @Before
    override fun setUp() {
        super.setUp()
        goalList = MemoryGoalList()
        val goal = Goal(name = "Test Goal", targetValue = 100.0)
        goalList.add(goal)
        goalId = goal.id ?: throw IllegalStateException()

        milestone = GoalMilestone(
            goalId = goalId,
            targetPercentage = 50.0,
            isComplete = false
        )
        command = AddGoalMilestoneCommand(goalList, milestone)
    }

    @Test
    fun testExecute() {
        assertTrue(goalList.getMilestones(goalId).isEmpty())
        command.run()
        val milestones = goalList.getMilestones(goalId)
        assertThat(milestones.size, equalTo(1))
        val addedMilestone = milestones[0]
        assertThat(addedMilestone.targetPercentage, equalTo(50.0))
        assertThat(addedMilestone.isComplete, equalTo(false))
    }

    @Test
    fun testMilestoneIdAssigned() {
        command.run()
        val milestones = goalList.getMilestones(goalId)
        assertThat(milestones[0].id != null, equalTo(true))
    }

    @Test(expected = IllegalArgumentException::class)
    fun testAddMilestoneNonExistentGoal() {
        val invalidMilestone = GoalMilestone(goalId = 999L, targetPercentage = 50.0)
        val invalidCommand = AddGoalMilestoneCommand(goalList, invalidMilestone)
        invalidCommand.run()
    }

    @Test
    fun testMultipleMilestonesAdded() {
        command.run()
        val milestone2 = GoalMilestone(
            goalId = goalId,
            targetPercentage = 75.0,
            isComplete = false
        )
        val command2 = AddGoalMilestoneCommand(goalList, milestone2)
        command2.run()

        val milestones = goalList.getMilestones(goalId)
        assertThat(milestones.size, equalTo(2))
    }
}
