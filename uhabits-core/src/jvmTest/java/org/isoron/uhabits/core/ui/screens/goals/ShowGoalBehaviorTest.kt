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
import org.hamcrest.core.IsNot.not
import org.hamcrest.core.IsNull.nullValue
import org.isoron.uhabits.core.BaseUnitTest
import org.isoron.uhabits.core.models.GoalProgressCalculator
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class ShowGoalBehaviorTest : BaseUnitTest() {
    private val screen: ShowGoalBehavior.Screen = mock()
    private lateinit var behavior: ShowGoalBehavior
    private lateinit var progressCalculator: GoalProgressCalculator

    @Before
    override fun setUp() {
        super.setUp()
        progressCalculator = GoalProgressCalculator(habitList)
    }

    @Test
    fun testBuildStateWithValidGoal() {
        val goal = fixtures.createGoal("Learn Guitar", daysUntilDeadline = 30)
        behavior = ShowGoalBehavior(
            goal,
            goalList,
            habitList,
            progressCalculator,
            mock(),
            commandRunner,
            screen
        )

        val state = behavior.buildState()
        assertThat(state.title, equalTo("Learn Guitar"))
        assertThat(state.archived, equalTo(false))
        assertThat(state.daysUntilDeadline, not(nullValue()))
    }

    @Test
    fun testBuildStateWithArchivedGoal() {
        val goal = fixtures.createArchivedGoal("Past Goal")
        behavior = ShowGoalBehavior(
            goal,
            goalList,
            habitList,
            progressCalculator,
            mock(),
            commandRunner,
            screen
        )

        val state = behavior.buildState()
        assertThat(state.archived, equalTo(true))
    }

    @Test
    fun testBuildStateWithOverdueGoal() {
        val goal = fixtures.createOverdueGoal("Overdue Goal")
        behavior = ShowGoalBehavior(
            goal,
            goalList,
            habitList,
            progressCalculator,
            mock(),
            commandRunner,
            screen
        )

        val state = behavior.buildState()
        assertThat(state.isOverdue, equalTo(true))
    }

    @Test
    fun testOnClickEditGoal() {
        val goal = fixtures.createGoal("Learn Guitar")
        behavior = ShowGoalBehavior(
            goal,
            goalList,
            habitList,
            progressCalculator,
            mock(),
            commandRunner,
            screen
        )

        behavior.onClickEditGoal()
        verify(screen).showEditGoalScreen(goal)
    }

    @Test
    fun testOnClickDeleteGoal() {
        val goal = fixtures.createGoal("Learn Guitar")
        behavior = ShowGoalBehavior(
            goal,
            goalList,
            habitList,
            progressCalculator,
            mock(),
            commandRunner,
            screen
        )

        behavior.onClickDeleteGoal()
        verify(screen).showDeleteConfirmation()
    }

    @Test
    fun testOnClickArchiveGoal() {
        val goal = fixtures.createGoal("Learn Guitar")
        behavior = ShowGoalBehavior(
            goal,
            goalList,
            habitList,
            progressCalculator,
            mock(),
            commandRunner,
            screen
        )

        behavior.onClickArchiveGoal()
        verify(screen).showArchiveConfirmation()
    }

    @Test
    fun testBuildStateWithLinkedHabits() {
        val habit = fixtures.createEmptyHabit()
        habitList.add(habit)
        val goal = fixtures.createGoal(
            "Learn Guitar",
            linkedHabits = listOf(habit.id)
        )
        behavior = ShowGoalBehavior(
            goal,
            goalList,
            habitList,
            progressCalculator,
            mock(),
            commandRunner,
            screen
        )

        val state = behavior.buildState()
        assertThat(state.linkedHabits.size, equalTo(1))
        assertThat(state.linkedHabits[0].name, equalTo("Meditate"))
    }

    @Test
    fun testProgressPercentageCalculation() {
        val habit = fixtures.createNumericalHabit()
        habitList.add(habit)
        val goal = fixtures.createGoal(
            "Learn Guitar",
            linkedHabits = listOf(habit.id)
        )
        behavior = ShowGoalBehavior(
            goal,
            goalList,
            habitList,
            progressCalculator,
            mock(),
            commandRunner,
            screen
        )

        val state = behavior.buildState()
        assertThat(state.progressPercent, not(equalTo(0)))
    }
}
