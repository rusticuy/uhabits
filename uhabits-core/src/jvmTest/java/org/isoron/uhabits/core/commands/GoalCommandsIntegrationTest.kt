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
import org.isoron.uhabits.core.models.goals.GoalProgressCalculator
import org.isoron.uhabits.core.models.goals.MemoryGoalList
import org.junit.Before
import org.junit.Test

class GoalCommandsIntegrationTest : BaseUnitTest() {
    private lateinit var goalList: GoalList
    private var goalId: Long = 0
    private var habitId: Long = 0

    @Before
    override fun setUp() {
        super.setUp()
        goalList = MemoryGoalList()

        val goal = Goal(name = "Test Goal", targetValue = 100.0)
        goalList.add(goal)
        goalId = goal.id ?: throw IllegalStateException()

        val habit = fixtures.createEmptyHabit()
        habitList.add(habit)
        habitId = habit.id ?: throw IllegalStateException()
    }

    @Test
    fun testCreateGoalThroughCommandRunner() {
        val goal = Goal(name = "Goal via CommandRunner", targetValue = 75.0)
        val createCommand = CreateGoalCommand(goalList, goal)
        commandRunner.run(createCommand)

        Thread.sleep(100)
        assertThat(goalList.size(), equalTo(2))
    }

    @Test
    fun testEditGoalThroughCommandRunner() {
        val modified = Goal(name = "Modified Goal", targetValue = 80.0)
        val editCommand = EditGoalCommand(goalList, goalId, modified)
        commandRunner.run(editCommand)

        Thread.sleep(100)
        val goal = goalList.getById(goalId)
        assertThat(goal?.name, equalTo("Modified Goal"))
    }

    @Test
    fun testDeleteGoalThroughCommandRunner() {
        val goal = goalList.getById(goalId) ?: throw IllegalStateException()
        val deleteCommand = DeleteGoalsCommand(goalList, listOf(goal))
        commandRunner.run(deleteCommand)

        Thread.sleep(100)
        assertThat(goalList.isEmpty, equalTo(true))
    }

    @Test
    fun testArchiveGoalThroughCommandRunner() {
        val goal = goalList.getById(goalId) ?: throw IllegalStateException()
        assertThat(goal.isArchived, equalTo(false))

        val archiveCommand = ArchiveGoalsCommand(goalList, listOf(goal))
        commandRunner.run(archiveCommand)

        Thread.sleep(100)
        val archived = goalList.getById(goalId)
        assertThat(archived?.isArchived, equalTo(true))
    }

    @Test
    fun testLinkHabitThroughCommandRunner() {
        val linkCommand = LinkHabitToGoalCommand(goalList, habitList, goalId, habitId, 1.0)
        commandRunner.run(linkCommand)

        Thread.sleep(100)
        val links = goalList.getLinkedHabits(goalId)
        assertThat(links.size, equalTo(1))
    }

    @Test
    fun testAddMilestoneThroughCommandRunner() {
        val milestone = GoalMilestone(goalId = goalId, targetPercentage = 50.0)
        val addCommand = AddGoalMilestoneCommand(goalList, milestone)
        commandRunner.run(addCommand)

        Thread.sleep(100)
        val milestones = goalList.getMilestones(goalId)
        assertThat(milestones.size, equalTo(1))
    }

    @Test
    fun testProgressCalculatorWithLinkedHabits() {
        val calculator = GoalProgressCalculator(goalList, habitList)

        val linkCommand = LinkHabitToGoalCommand(goalList, habitList, goalId, habitId, 1.0)
        commandRunner.run(linkCommand)

        Thread.sleep(100)

        val progress = calculator.calculate(goalId)
        assertThat(progress != null, equalTo(true))
        assertThat(progress?.linkedHabits?.size, equalTo(1))
    }

    @Test
    fun testComplexGoalWorkflow() {
        val habit1 = fixtures.createEmptyHabit("Meditate")
        val habit2 = fixtures.createEmptyHabit("Exercise")
        habitList.add(habit1)
        habitList.add(habit2)

        val habit1Id = habit1.id ?: throw IllegalStateException()
        val habit2Id = habit2.id ?: throw IllegalStateException()

        val linkCmd1 = LinkHabitToGoalCommand(goalList, habitList, goalId, habit1Id, 1.5)
        val linkCmd2 = LinkHabitToGoalCommand(goalList, habitList, goalId, habit2Id, 2.0)

        commandRunner.run(linkCmd1)
        commandRunner.run(linkCmd2)

        Thread.sleep(100)

        val milestone1 = GoalMilestone(goalId = goalId, targetPercentage = 25.0)
        val milestone2 = GoalMilestone(goalId = goalId, targetPercentage = 50.0)

        val addM1 = AddGoalMilestoneCommand(goalList, milestone1)
        val addM2 = AddGoalMilestoneCommand(goalList, milestone2)

        commandRunner.run(addM1)
        commandRunner.run(addM2)

        Thread.sleep(100)

        val links = goalList.getLinkedHabits(goalId)
        val milestones = goalList.getMilestones(goalId)

        assertThat(links.size, equalTo(2))
        assertThat(milestones.size, equalTo(2))

        val calculator = GoalProgressCalculator(goalList, habitList)
        val progress = calculator.calculate(goalId)

        assertThat(progress != null, equalTo(true))
        assertThat(progress?.linkedHabits?.size, equalTo(2))
        assertThat(progress?.milestoneStatus?.size, equalTo(2))
    }
}
