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
package org.isoron.uhabits.core.models

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.isoron.uhabits.core.BaseUnitTest
import org.isoron.uhabits.core.models.goals.Goal
import org.isoron.uhabits.core.models.goals.GoalHabitLink
import org.isoron.uhabits.core.models.goals.GoalMilestone
import org.isoron.uhabits.core.models.goals.GoalProgressCalculator
import org.isoron.uhabits.core.models.goals.MemoryGoalList
import org.junit.Before
import org.junit.Test
import kotlin.test.assertNotNull

class GoalProgressCalculatorTest : BaseUnitTest() {
    private lateinit var testGoalList: MemoryGoalList
    private lateinit var calculator: GoalProgressCalculator

    @Before
    override fun setUp() {
        super.setUp()
        testGoalList = MemoryGoalList()
        calculator = GoalProgressCalculator(testGoalList, habitList)
    }

    @Test
    fun testCalculateProgressWithNoLinkedHabits() {
        val goal = Goal(name = "No Links", targetValue = 100.0)
        testGoalList.add(goal)
        
        val progress = calculator.calculate(goal.id!!)
        
        assertNotNull(progress)
        assertThat(progress?.percentageComplete, equalTo(0.0))
        assertThat(progress?.linkedHabits?.size, equalTo(0))
    }

    @Test
    fun testCalculateProgressWithSingleLinkedHabit() {
        val habit = fixtures.createEmptyHabit("Exercise")
        habitList.add(habit)
        
        val goal = Goal(name = "Single Link", targetValue = 100.0)
        testGoalList.add(goal)
        
        val link = GoalHabitLink(goalId = goal.id, habitId = habit.id, weight = 1.0)
        testGoalList.addHabitLink(link)
        
        val progress = calculator.calculate(goal.id!!)
        
        assertNotNull(progress)
        assertThat(progress?.linkedHabits?.size, equalTo(1))
    }

    @Test
    fun testCalculateProgressWithMixedWeights() {
        val habit1 = fixtures.createEmptyHabit("Study")
        val habit2 = fixtures.createLongHabit()
        val habit3 = fixtures.createShortHabit()
        
        habitList.add(habit1)
        habitList.add(habit2)
        habitList.add(habit3)
        
        val goal = Goal(name = "Weighted Goal", targetValue = 100.0)
        testGoalList.add(goal)
        
        val link1 = GoalHabitLink(goalId = goal.id, habitId = habit1.id, weight = 1.0)
        val link2 = GoalHabitLink(goalId = goal.id, habitId = habit2.id, weight = 1.5)
        val link3 = GoalHabitLink(goalId = goal.id, habitId = habit3.id, weight = 2.0)
        
        testGoalList.addHabitLink(link1)
        testGoalList.addHabitLink(link2)
        testGoalList.addHabitLink(link3)
        
        val progress = calculator.calculate(goal.id!!)
        
        assertNotNull(progress)
        assertThat(progress?.linkedHabits?.size, equalTo(3))
    }

    @Test
    fun testCalculateProgressWithOverdeadline() {
        val pastDate = System.currentTimeMillis() - 86400000
        val goal = Goal(
            name = "Overdue Goal",
            targetValue = 100.0,
            deadline = pastDate
        )
        testGoalList.add(goal)
        
        val progress = calculator.calculate(goal.id!!)
        
        assertNotNull(progress)
        assertThat(progress?.deadlineOverdue, equalTo(true))
    }

    @Test
    fun testCalculateProgressWithMilestoneCompletion() {
        val habit = fixtures.createNumericalHabit()
        habitList.add(habit)
        
        val goal = Goal(name = "Goal with Milestones", targetValue = 100.0)
        testGoalList.add(goal)
        
        val milestone1 = GoalMilestone(
            goalId = goal.id,
            name = "First Milestone",
            targetValue = 25.0
        )
        val milestone2 = GoalMilestone(
            goalId = goal.id,
            name = "Second Milestone",
            targetValue = 50.0
        )
        
        testGoalList.addMilestone(milestone1)
        testGoalList.addMilestone(milestone2)
        
        val progress = calculator.calculate(goal.id!!)
        
        assertNotNull(progress)
        assertThat(progress?.milestoneStatus?.size, equalTo(2))
    }

    @Test
    fun testProgressCalculationWithEmptyHabits() {
        val goal = Goal(name = "Empty Goal", targetValue = 100.0)
        testGoalList.add(goal)
        
        val progress = calculator.calculate(goal.id!!)
        
        assertNotNull(progress)
        assertThat(progress?.linkedHabits?.size, equalTo(0))
    }

    @Test
    fun testProgressCalculationWithAllHabitsCompleted() {
        val habit1 = fixtures.createShortHabit()
        val habit2 = fixtures.createLongHabit()
        
        habitList.add(habit1)
        habitList.add(habit2)
        
        val goal = Goal(name = "Completed Goal", targetValue = 100.0)
        testGoalList.add(goal)
        
        val link1 = GoalHabitLink(goalId = goal.id, habitId = habit1.id, weight = 1.0)
        val link2 = GoalHabitLink(goalId = goal.id, habitId = habit2.id, weight = 1.0)
        
        testGoalList.addHabitLink(link1)
        testGoalList.addHabitLink(link2)
        
        val progress = calculator.calculate(goal.id!!)
        
        assertNotNull(progress)
        assertThat(progress?.linkedHabits?.size, equalTo(2))
    }

    @Test
    fun testProgressCalculationWithPartialCompletion() {
        val habit1 = fixtures.createEmptyHabit("Partial 1")
        val habit2 = fixtures.createEmptyHabit("Partial 2")
        val habit3 = fixtures.createEmptyHabit("Partial 3")
        
        habitList.add(habit1)
        habitList.add(habit2)
        habitList.add(habit3)
        
        val goal = Goal(name = "Partial Goal", targetValue = 100.0)
        testGoalList.add(goal)
        
        val link1 = GoalHabitLink(goalId = goal.id, habitId = habit1.id, weight = 1.0)
        val link2 = GoalHabitLink(goalId = goal.id, habitId = habit2.id, weight = 1.0)
        
        testGoalList.addHabitLink(link1)
        testGoalList.addHabitLink(link2)
        
        val progress = calculator.calculate(goal.id!!)
        
        assertNotNull(progress)
        assertThat(progress?.linkedHabits?.size, equalTo(2))
    }

    @Test
    fun testProgressCalculationWithNumericalTargets() {
        val habit = fixtures.createNumericalHabit()
        habitList.add(habit)
        
        val goal = Goal(name = "Numerical Goal", targetValue = 100.0)
        testGoalList.add(goal)
        
        val link = GoalHabitLink(goalId = goal.id, habitId = habit.id, weight = 1.0)
        testGoalList.addHabitLink(link)
        
        val progress = calculator.calculate(goal.id!!)
        
        assertNotNull(progress)
        assertThat(progress?.linkedHabits?.size, equalTo(1))
    }

    @Test
    fun testProgressCalculationWithZeroWeight() {
        val habit = fixtures.createEmptyHabit()
        habitList.add(habit)
        
        val goal = Goal(name = "Zero Weight Goal", targetValue = 100.0)
        testGoalList.add(goal)
        
        val link = GoalHabitLink(goalId = goal.id, habitId = habit.id, weight = 0.0)
        testGoalList.addHabitLink(link)
        
        val progress = calculator.calculate(goal.id!!)
        
        assertNotNull(progress)
        assertThat(progress?.percentageComplete, equalTo(0.0))
    }

    @Test
    fun testProgressCalculationWithHighWeights() {
        val habit1 = fixtures.createEmptyHabit()
        val habit2 = fixtures.createEmptyHabit()
        
        habitList.add(habit1)
        habitList.add(habit2)
        
        val goal = Goal(name = "High Weight Goal", targetValue = 100.0)
        testGoalList.add(goal)
        
        val link1 = GoalHabitLink(goalId = goal.id, habitId = habit1.id, weight = 10.0)
        val link2 = GoalHabitLink(goalId = goal.id, habitId = habit2.id, weight = 20.0)
        
        testGoalList.addHabitLink(link1)
        testGoalList.addHabitLink(link2)
        
        val progress = calculator.calculate(goal.id!!)
        
        assertNotNull(progress)
        assertThat(progress?.linkedHabits?.size, equalTo(2))
    }
}
