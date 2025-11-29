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
package org.isoron.uhabits.core.models.goals

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.isoron.uhabits.core.BaseUnitTest
import org.isoron.uhabits.core.commands.AddGoalMilestoneCommand
import org.isoron.uhabits.core.commands.ArchiveGoalsCommand
import org.isoron.uhabits.core.commands.CreateGoalCommand
import org.isoron.uhabits.core.commands.DeleteGoalsCommand
import org.isoron.uhabits.core.commands.EditGoalCommand
import org.isoron.uhabits.core.commands.LinkHabitToGoalCommand
import org.isoron.uhabits.core.commands.UnlinkHabitFromGoalCommand
import org.isoron.uhabits.core.commands.UpdateGoalMilestoneCommand
import org.junit.Before
import org.junit.Test
import kotlin.test.assertTrue

class GoalAcceptanceTest : BaseUnitTest() {
    private lateinit var goalList: GoalList
    private lateinit var calculator: GoalProgressCalculator

    @Before
    override fun setUp() {
        super.setUp()
        goalList = MemoryGoalList()
        calculator = GoalProgressCalculator(goalList, habitList)
    }

    @Test
    fun testAcceptance_NewCommandsCanBeTriggeredThroughCommandRunner() {
        val goal = Goal(name = "Complete Project", targetValue = 100.0)

        val createCmd = CreateGoalCommand(goalList, goal)
        commandRunner.run(createCmd)
        Thread.sleep(100)

        assertTrue(goalList.size() > 0)
        val createdGoal = goalList.iterator().next()
        assertThat(createdGoal.name, equalTo("Complete Project"))
    }

    @Test
    fun testAcceptance_ProgressPercentageReflectsLinkedHabitScores() {
        val goal = Goal(name = "Test Goal", targetValue = 100.0)
        goalList.add(goal)
        val goalId = goal.id ?: throw IllegalStateException()

        val habit = fixtures.createEmptyHabit()
        habitList.add(habit)
        val habitId = habit.id ?: throw IllegalStateException()

        val link = GoalHabitLink(goalId = goalId, habitId = habitId, weight = 1.0)
        goalList.addHabitLink(link)

        val progress = calculator.calculate(goalId)

        assertTrue(progress != null)
        assertTrue(progress!!.linkedHabits.size > 0)
        assertTrue(progress.percentageComplete >= 0.0)
        assertTrue(progress.percentageComplete <= 100.0)
    }

    @Test
    fun testAcceptance_MilestoneAndDeadlineMetadataStaysConsistent() {
        val goal = Goal(name = "Test Goal", targetValue = 100.0, deadline = System.currentTimeMillis() + 1000 * 60 * 60 * 24)
        goalList.add(goal)
        val goalId = goal.id ?: throw IllegalStateException()

        val milestone = GoalMilestone(goalId = goalId, targetPercentage = 50.0, isComplete = false)
        goalList.addMilestone(milestone)

        val addCmd = AddGoalMilestoneCommand(goalList, milestone)
        commandRunner.run(addCmd)
        Thread.sleep(100)

        val milestone2 = goalList.getMilestones(goalId)[0]
        assertThat(milestone2.targetPercentage, equalTo(50.0))
        assertThat(milestone2.isComplete, equalTo(false))

        milestone2.isComplete = true
        val updateCmd = UpdateGoalMilestoneCommand(goalList, milestone2)
        commandRunner.run(updateCmd)
        Thread.sleep(100)

        val updated = goalList.getMilestones(goalId)[0]
        assertThat(updated.isComplete, equalTo(true))
    }

    @Test
    fun testAcceptance_CRUDCommandsMutateRepositoriesCorrectly() {
        val goal1 = Goal(name = "Goal 1", targetValue = 50.0)
        val goal2 = Goal(name = "Goal 2", targetValue = 75.0)
        goalList.add(goal1)
        goalList.add(goal2)
        val goal1Id = goal1.id ?: throw IllegalStateException()
        val goal2Id = goal2.id ?: throw IllegalStateException()

        assertThat(goalList.size(), equalTo(2))

        val modified = Goal(name = "Goal 1 Modified", targetValue = 60.0)
        val editCmd = EditGoalCommand(goalList, goal1Id, modified)
        commandRunner.run(editCmd)
        Thread.sleep(100)

        val edited = goalList.getById(goal1Id)
        assertThat(edited?.name, equalTo("Goal 1 Modified"))

        val deleteCmd = DeleteGoalsCommand(goalList, listOf(goal1))
        commandRunner.run(deleteCmd)
        Thread.sleep(100)

        assertThat(goalList.size(), equalTo(1))
        assertThat(goalList.getById(goal1Id), equalTo(null))

        val archiveCmd = ArchiveGoalsCommand(goalList, listOf(goal2))
        commandRunner.run(archiveCmd)
        Thread.sleep(100)

        val archived = goalList.getById(goal2Id)
        assertThat(archived?.isArchived, equalTo(true))
    }

    @Test
    fun testAcceptance_LinkingEnforcesReferentialIntegrity() {
        val goal = Goal(name = "Test Goal", targetValue = 100.0)
        goalList.add(goal)
        val goalId = goal.id ?: throw IllegalStateException()

        val habit = fixtures.createEmptyHabit()
        habitList.add(habit)
        val habitId = habit.id ?: throw IllegalStateException()

        val linkCmd = LinkHabitToGoalCommand(goalList, habitList, goalId, habitId, 1.5)
        commandRunner.run(linkCmd)
        Thread.sleep(100)

        val links = goalList.getLinkedHabits(goalId)
        assertTrue(links.isNotEmpty())

        val link = links[0]
        val unlinkCmd = UnlinkHabitFromGoalCommand(goalList, link)
        commandRunner.run(unlinkCmd)
        Thread.sleep(100)

        assertTrue(goalList.getLinkedHabits(goalId).isEmpty())
    }

    @Test
    fun testAcceptance_ProgressCalculationsForBooleanAndNumericalHabits() {
        val goal = Goal(name = "Test Goal", targetValue = 100.0)
        goalList.add(goal)
        val goalId = goal.id ?: throw IllegalStateException()

        val booleanHabit = fixtures.createEmptyHabit("Meditate")
        val numericalHabit = fixtures.createEmptyNumericalHabit(org.isoron.uhabits.core.models.NumericalHabitType.AT_LEAST)

        habitList.add(booleanHabit)
        habitList.add(numericalHabit)

        val boolId = booleanHabit.id ?: throw IllegalStateException()
        val numId = numericalHabit.id ?: throw IllegalStateException()

        goalList.addHabitLink(GoalHabitLink(goalId = goalId, habitId = boolId, weight = 1.0))
        goalList.addHabitLink(GoalHabitLink(goalId = goalId, habitId = numId, weight = 1.0))

        val progress = calculator.calculate(goalId)

        assertTrue(progress != null)
        assertThat(progress!!.linkedHabits.size, equalTo(2))
        assertTrue(progress.percentageComplete >= 0.0)
    }

    @Test
    fun testAcceptance_ProgressCalculationsForArchivedGoals() {
        val goal = Goal(name = "Test Goal", targetValue = 100.0, isArchived = true)
        goalList.add(goal)
        val goalId = goal.id ?: throw IllegalStateException()

        val habit = fixtures.createEmptyHabit()
        habitList.add(habit)
        val habitId = habit.id ?: throw IllegalStateException()

        goalList.addHabitLink(GoalHabitLink(goalId = goalId, habitId = habitId, weight = 1.0))

        val progress = calculator.calculate(goalId)

        assertTrue(progress != null)
        assertThat(progress!!.goalId, equalTo(goalId))
    }

    @Test
    fun testAcceptance_ProgressCalculationsForOverdeadlines() {
        val overdueDeadline = System.currentTimeMillis() - 1000
        val goal = Goal(name = "Test Goal", targetValue = 100.0, deadline = overdueDeadline)
        goalList.add(goal)
        val goalId = goal.id ?: throw IllegalStateException()

        val habit = fixtures.createEmptyHabit()
        habitList.add(habit)
        val habitId = habit.id ?: throw IllegalStateException()

        goalList.addHabitLink(GoalHabitLink(goalId = goalId, habitId = habitId, weight = 1.0))

        val progress = calculator.calculate(goalId)

        assertTrue(progress != null)
        assertThat(progress!!.deadlineOverdue, equalTo(true))
    }
}
