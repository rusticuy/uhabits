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
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.isoron.uhabits.core.BaseUnitTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertTrue

class GoalProgressCalculatorTest : BaseUnitTest() {
    private lateinit var calculator: GoalProgressCalculator
    private lateinit var goalList: GoalList
    private var goalId: Long = 0
    private var habitId: Long = 0

    @Before
    override fun setUp() {
        super.setUp()
        goalList = MemoryGoalList()
        calculator = GoalProgressCalculator(goalList, habitList)

        val goal = Goal(name = "Test Goal", targetValue = 100.0)
        goalList.add(goal)
        goalId = goal.id ?: throw IllegalStateException()

        val habit = fixtures.createEmptyHabit()
        habitList.add(habit)
        habitId = habit.id ?: throw IllegalStateException()
    }

    @Test
    fun testCalculateWithoutLinkedHabits() {
        val progress = calculator.calculate(goalId)
        assertThat(progress?.percentageComplete, equalTo(0.0))
        assertTrue(progress?.linkedHabits?.isEmpty() ?: true)
    }

    @Test
    fun testCalculateWithLinkedHabit() {
        val link = GoalHabitLink(goalId = goalId, habitId = habitId, weight = 1.0)
        goalList.addHabitLink(link)

        val progress = calculator.calculate(goalId)
        assertThat(progress != null, equalTo(true))
        assertThat(progress?.linkedHabits?.size, equalTo(1))
        assertThat(progress?.goalId, equalTo(goalId))
    }

    @Test
    fun testCalculateNonExistentGoal() {
        val progress = calculator.calculate(999L)
        assertThat(progress, nullValue())
    }

    @Test
    fun testCalculateWithWeights() {
        val habit2 = fixtures.createEmptyHabit("Exercise")
        habitList.add(habit2)
        val habitId2 = habit2.id ?: throw IllegalStateException()

        val link1 = GoalHabitLink(goalId = goalId, habitId = habitId, weight = 2.0)
        val link2 = GoalHabitLink(goalId = goalId, habitId = habitId2, weight = 3.0)
        goalList.addHabitLink(link1)
        goalList.addHabitLink(link2)

        val progress = calculator.calculate(goalId)
        assertThat(progress?.linkedHabits?.size, equalTo(2))
    }

    @Test
    fun testDeadlineOverdue() {
        val goal = goalList.getById(goalId) ?: throw IllegalStateException()
        goal.deadline = System.currentTimeMillis() - 1000 * 60 * 60 * 24

        val progress = calculator.calculate(goalId)
        assertThat(progress?.deadlineOverdue, equalTo(true))
    }

    @Test
    fun testDeadlineNotOverdue() {
        val goal = goalList.getById(goalId) ?: throw IllegalStateException()
        goal.deadline = System.currentTimeMillis() + 1000 * 60 * 60 * 24

        val progress = calculator.calculate(goalId)
        assertThat(progress?.deadlineOverdue, equalTo(false))
    }

    @Test
    fun testDeadlineNull() {
        val goal = goalList.getById(goalId) ?: throw IllegalStateException()
        goal.deadline = null

        val progress = calculator.calculate(goalId)
        assertThat(progress?.deadlineOverdue, equalTo(false))
    }

    @Test
    fun testMilestoneStatus() {
        val milestone1 = GoalMilestone(goalId = goalId, targetPercentage = 50.0)
        val milestone2 = GoalMilestone(goalId = goalId, targetPercentage = 100.0)
        goalList.addMilestone(milestone1)
        goalList.addMilestone(milestone2)

        val progress = calculator.calculate(goalId)
        assertThat(progress?.milestoneStatus?.size, equalTo(2))
    }

    @Test
    fun testProgressPercentageNormalized() {
        val link = GoalHabitLink(goalId = goalId, habitId = habitId, weight = 1.0)
        goalList.addHabitLink(link)

        val progress = calculator.calculate(goalId)
        assertTrue(progress?.percentageComplete ?: 0.0 >= 0.0)
        assertTrue(progress?.percentageComplete ?: 100.0 <= 100.0)
    }
}
