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

class UpdateGoalMilestoneCommandTest : BaseUnitTest() {
    private lateinit var goalList: GoalList
    private lateinit var command: UpdateGoalMilestoneCommand
    private lateinit var milestone: GoalMilestone
    private var goalId: Long = 0
    private var milestoneId: Long = 0

    @Before
    override fun setUp() {
        super.setUp()
        goalList = MemoryGoalList()
        val goal = Goal(name = "Test Goal", targetValue = 100.0)
        goalList.add(goal)
        goalId = goal.id ?: throw IllegalStateException()

        milestone = GoalMilestone(
            goalId = goalId,
            name = "Test Milestone",
            targetValue = 50.0,
            isCompleted = false
        )
        goalList.addMilestone(milestone)
        milestoneId = milestone.id ?: throw IllegalStateException()

        milestone.isCompleted = true
        command = UpdateGoalMilestoneCommand(goalList, milestone)
    }

    @Test
    fun testExecute() {
        val originalMilestones = goalList.getMilestones(goalId)
        val original = originalMilestones[0]
        assertThat(original.isCompleted, equalTo(true))
        command.run()
        val updated = goalList.getMilestones(goalId)[0]
        assertThat(updated.isCompleted, equalTo(true))
    }

    @Test
    fun testMilestoneCompletionStatus() {
        val before = goalList.getMilestones(goalId)[0]
        before.isCompleted = false
        goalList.updateMilestone(before)

        val incomplete = goalList.getMilestones(goalId)[0]
        assertThat(incomplete.isCompleted, equalTo(false))

        incomplete.isCompleted = true
        val updateCmd = UpdateGoalMilestoneCommand(goalList, incomplete)
        updateCmd.run()

        val completed = goalList.getMilestones(goalId)[0]
        assertThat(completed.isCompleted, equalTo(true))
    }
}
