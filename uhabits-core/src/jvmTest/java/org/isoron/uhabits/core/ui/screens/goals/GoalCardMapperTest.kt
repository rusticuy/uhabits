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
package org.isoron.uhabits.core.ui.screens.goals

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual.equalTo
import org.isoron.uhabits.core.BaseUnitTest
import org.isoron.uhabits.core.models.GoalProgressCalculator
import org.junit.Before
import org.junit.Test

class GoalCardMapperTest : BaseUnitTest() {
    private lateinit var mapper: GoalCardMapper
    private lateinit var progressCalculator: GoalProgressCalculator

    @Before
    override fun setUp() {
        super.setUp()
        progressCalculator = GoalProgressCalculator(habitList)
        mapper = GoalCardMapper(progressCalculator)
    }

    @Test
    fun testMapGoalToCardState() {
        val goal = fixtures.createGoal("Learn Guitar", daysUntilDeadline = 30)
        val state = mapper.toGoalCardState(goal)

        assertThat(state.name, equalTo("Learn Guitar"))
        assertThat(state.description, equalTo("Learn to play 10 songs"))
        assertThat(state.archived, equalTo(false))
    }

    @Test
    fun testMapArchivedGoalToCardState() {
        val goal = fixtures.createArchivedGoal()
        val state = mapper.toGoalCardState(goal)

        assertThat(state.archived, equalTo(true))
    }

    @Test
    fun testMapOverdueGoalToCardState() {
        val goal = fixtures.createOverdueGoal()
        val state = mapper.toGoalCardState(goal)

        assertThat(state.isOverdue, equalTo(true))
    }

    @Test
    fun testMapGoalWithLinkedHabits() {
        val habit = fixtures.createEmptyHabit()
        habitList.add(habit)
        val goal = fixtures.createGoal(
            "Learn Guitar",
            linkedHabits = listOf(habit.id)
        )
        val state = mapper.toGoalCardState(goal)

        assertThat(state.linkedHabitCount, equalTo(1))
    }

    @Test
    fun testMapGoalWithMultipleLinkedHabits() {
        val habit1 = fixtures.createEmptyHabit("Habit 1")
        val habit2 = fixtures.createEmptyHabit("Habit 2")
        habitList.add(habit1)
        habitList.add(habit2)
        val goal = fixtures.createGoal(
            "Learn Guitar",
            linkedHabits = listOf(habit1.id, habit2.id)
        )
        val state = mapper.toGoalCardState(goal)

        assertThat(state.linkedHabitCount, equalTo(2))
    }

    @Test
    fun testDaysUntilDeadlineCalculation() {
        val goal = fixtures.createGoal("Learn Guitar", daysUntilDeadline = 15)
        val state = mapper.toGoalCardState(goal)

        assertThat(state.daysUntilDeadline >= 14L, equalTo(true))
        assertThat(state.daysUntilDeadline <= 16L, equalTo(true))
    }

    @Test
    fun testProgressPercentCalculation() {
        val habit = fixtures.createNumericalHabit()
        habitList.add(habit)
        val goal = fixtures.createGoal(
            "Learn Guitar",
            linkedHabits = listOf(habit.id)
        )
        val state = mapper.toGoalCardState(goal)

        assertThat(state.progressPercent >= 0, equalTo(true))
        assertThat(state.progressPercent <= 100, equalTo(true))
    }
}
