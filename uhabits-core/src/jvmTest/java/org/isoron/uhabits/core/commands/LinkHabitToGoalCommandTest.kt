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
import org.isoron.uhabits.core.models.Habit
import org.isoron.uhabits.core.models.goals.Goal
import org.isoron.uhabits.core.models.goals.GoalList
import org.isoron.uhabits.core.models.goals.MemoryGoalList
import org.junit.Before
import org.junit.Test
import kotlin.test.assertTrue

class LinkHabitToGoalCommandTest : BaseUnitTest() {
    private lateinit var goalList: GoalList
    private lateinit var command: LinkHabitToGoalCommand
    private lateinit var goal: Goal
    private lateinit var habit: Habit
    private var goalId: Long = 0
    private var habitId: Long = 0

    @Before
    override fun setUp() {
        super.setUp()
        goalList = MemoryGoalList()
        goal = Goal(name = "Test Goal", targetValue = 100.0)
        goalList.add(goal)
        goalId = goal.id ?: throw IllegalStateException("Goal id should not be null")

        habit = fixtures.createEmptyHabit()
        habitList.add(habit)
        habitId = habit.id ?: throw IllegalStateException("Habit id should not be null")

        command = LinkHabitToGoalCommand(goalList, habitList, goalId, habitId, 1.5)
    }

    @Test
    fun testExecute() {
        assertTrue(goalList.getLinkedHabits(goalId).isEmpty())
        command.run()
        val links = goalList.getLinkedHabits(goalId)
        assertThat(links.size, equalTo(1))
        val link = links[0]
        assertThat(link.goalId, equalTo(goalId))
        assertThat(link.habitId, equalTo(habitId))
        assertThat(link.weight, equalTo(1.5))
    }

    @Test(expected = IllegalArgumentException::class)
    fun testLinkNonExistentGoal() {
        val invalidCommand = LinkHabitToGoalCommand(goalList, habitList, 999L, habitId, 1.0)
        invalidCommand.run()
    }

    @Test(expected = IllegalArgumentException::class)
    fun testLinkNonExistentHabit() {
        val invalidCommand = LinkHabitToGoalCommand(goalList, habitList, goalId, 999L, 1.0)
        invalidCommand.run()
    }

    @Test
    fun testMultipleHabitsLinked() {
        val habit2 = fixtures.createEmptyHabit("Exercise")
        habitList.add(habit2)
        val habitId2 = habit2.id ?: throw IllegalStateException()

        command.run()
        val command2 = LinkHabitToGoalCommand(goalList, habitList, goalId, habitId2, 2.0)
        command2.run()

        val links = goalList.getLinkedHabits(goalId)
        assertThat(links.size, equalTo(2))
    }
}
