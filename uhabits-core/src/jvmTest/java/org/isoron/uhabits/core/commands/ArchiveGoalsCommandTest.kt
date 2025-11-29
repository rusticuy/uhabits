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

class ArchiveGoalsCommandTest : BaseUnitTest() {
    private lateinit var goalList: GoalList
    private lateinit var command: ArchiveGoalsCommand
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
        command = ArchiveGoalsCommand(goalList, listOf(goals[0], goals[1]))
    }

    @Test
    fun testExecute() {
        assertThat(goals[0].isArchived, equalTo(false))
        assertThat(goals[1].isArchived, equalTo(false))
        command.run()
        assertThat(goals[0].isArchived, equalTo(true))
        assertThat(goals[1].isArchived, equalTo(true))
    }

    @Test
    fun testUnselectedGoalsNotArchived() {
        command.run()
        assertThat(goals[2].isArchived, equalTo(false))
    }

    @Test
    fun testArchivedGoalsStillInList() {
        command.run()
        assertThat(goalList.size(), equalTo(3))
        val archived = goalList.getById(goals[0].id!!)
        assertThat(archived?.isArchived, equalTo(true))
    }
}
