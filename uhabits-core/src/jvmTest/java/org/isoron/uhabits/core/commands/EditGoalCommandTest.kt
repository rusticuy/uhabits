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

class EditGoalCommandTest : BaseUnitTest() {
    private lateinit var goalList: GoalList
    private lateinit var command: EditGoalCommand
    private lateinit var originalGoal: Goal
    private lateinit var modifiedGoal: Goal
    private var goalId: Long = 0

    @Before
    override fun setUp() {
        super.setUp()
        goalList = MemoryGoalList()
        originalGoal = Goal(
            name = "Original Goal",
            description = "Original Description",
            targetValue = 50.0
        )
        goalList.add(originalGoal)
        goalId = originalGoal.id ?: throw IllegalStateException("Goal id should not be null")

        modifiedGoal = Goal(
            name = "Modified Goal",
            description = "Modified Description",
            targetValue = 75.0
        )
        command = EditGoalCommand(goalList, goalId, modifiedGoal)
    }

    @Test
    fun testExecute() {
        command.run()
        val goal = goalList.getById(goalId)
        assertThat(goal?.name, equalTo(modifiedGoal.name))
        assertThat(goal?.description, equalTo(modifiedGoal.description))
        assertThat(goal?.targetValue, equalTo(modifiedGoal.targetValue))
    }

    @Test
    fun testGoalIdUnchanged() {
        command.run()
        val goal = goalList.getById(goalId)
        assertThat(goal?.id, equalTo(goalId))
    }

    @Test(expected = IllegalArgumentException::class)
    fun testEditNonExistentGoal() {
        val nonExistentId = 999L
        val editCommand = EditGoalCommand(goalList, nonExistentId, modifiedGoal)
        editCommand.run()
    }
}
