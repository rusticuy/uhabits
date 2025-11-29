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

class DeleteGoalsCommandTest : BaseUnitTest() {
    private lateinit var goalList: GoalList
    private lateinit var command: DeleteGoalsCommand
    private val goals = mutableListOf<Goal>()

    @Before
    override fun setUp() {
        super.setUp()
        goalList = MemoryGoalList()
        for (i in 0..2) {
            val goal = Goal(
                name = "Goal $i",
                description = "Description $i",
                targetValue = (i * 25).toDouble()
            )
            goalList.add(goal)
            goals.add(goal)
        }
        command = DeleteGoalsCommand(goalList, listOf(goals[0], goals[1]))
    }

    @Test
    fun testExecute() {
        assertThat(goalList.size(), equalTo(3))
        command.run()
        assertThat(goalList.size(), equalTo(1))
        val remaining = goalList.iterator().next()
        assertThat(remaining.name, equalTo("Goal 2"))
    }

    @Test
    fun testDeletedGoalsNotFound() {
        command.run()
        assertThat(goalList.getById(goals[0].id!!), equalTo(null))
        assertThat(goalList.getById(goals[1].id!!), equalTo(null))
    }

    @Test
    fun testRemainingGoalUnchanged() {
        command.run()
        val remaining = goalList.getById(goals[2].id!!)
        assertThat(remaining?.name, equalTo(goals[2].name))
        assertThat(remaining?.description, equalTo(goals[2].description))
    }
}
