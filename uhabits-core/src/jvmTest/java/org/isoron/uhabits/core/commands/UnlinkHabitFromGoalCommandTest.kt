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
import kotlin.test.assertTrue

class UnlinkHabitFromGoalCommandTest : BaseUnitTest() {
    private lateinit var goalList: GoalList
    private lateinit var command: UnlinkHabitFromGoalCommand
    private lateinit var link: GoalHabitLink
    private var goalId: Long = 0

    @Before
    override fun setUp() {
        super.setUp()
        goalList = MemoryGoalList()
        val goal = Goal(name = "Test Goal", targetValue = 100.0)
        goalList.add(goal)
        goalId = goal.id ?: throw IllegalStateException()

        val habit = fixtures.createEmptyHabit()
        habitList.add(habit)
        val habitId = habit.id ?: throw IllegalStateException()

        link = GoalHabitLink(goalId = goalId, habitId = habitId, weight = 1.0)
        goalList.addHabitLink(link)

        command = UnlinkHabitFromGoalCommand(goalList, link)
    }

    @Test
    fun testExecute() {
        assertThat(goalList.getLinkedHabits(goalId).size, equalTo(1))
        command.run()
        assertTrue(goalList.getLinkedHabits(goalId).isEmpty())
    }

    @Test
    fun testMultipleLinksPreserved() {
        val habit2 = fixtures.createEmptyHabit("Exercise")
        habitList.add(habit2)
        val habitId2 = habit2.id ?: throw IllegalStateException()

        val link2 = GoalHabitLink(goalId = goalId, habitId = habitId2, weight = 2.0)
        goalList.addHabitLink(link2)

        command.run()

        val links = goalList.getLinkedHabits(goalId)
        assertThat(links.size, equalTo(1))
        assertThat(links[0].habitId, equalTo(habitId2))
    }
}
