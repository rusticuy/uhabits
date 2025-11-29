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
import org.isoron.uhabits.core.models.Timestamp
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class EditGoalBehaviorTest : BaseUnitTest() {
    private val screen: EditGoalBehavior.Screen = mock()
    private lateinit var behavior: EditGoalBehavior

    @Before
    override fun setUp() {
        super.setUp()
    }

    @Test
    fun testCreateNewGoalWithValidData() {
        behavior = EditGoalBehavior(
            goal = null,
            goalList,
            habitList,
            mock(),
            commandRunner,
            screen
        )

        val habit = fixtures.createEmptyHabit()
        habitList.add(habit)

        behavior.onNameChanged("Learn Guitar")
        behavior.onDeadlineChanged(Timestamp.today().plus(30))
        behavior.onAddHabit(habit.id!!)
        behavior.onSaveGoal()

        assertThat(goalList.getAll().size, equalTo(1))
        verify(screen).navigateBack()
    }

    @Test
    fun testValidationRejectsEmptyName() {
        behavior = EditGoalBehavior(
            goal = null,
            goalList,
            habitList,
            mock(),
            commandRunner,
            screen
        )

        behavior.onNameChanged("")
        behavior.onSaveGoal()

        verify(screen).showValidationError(EditGoalBehavior.ValidationError.EMPTY_NAME)
    }

    @Test
    fun testValidationRejectsNoLinkedHabits() {
        behavior = EditGoalBehavior(
            goal = null,
            goalList,
            habitList,
            mock(),
            commandRunner,
            screen
        )

        behavior.onNameChanged("Learn Guitar")
        behavior.onDeadlineChanged(Timestamp.today().plus(30))
        behavior.onSaveGoal()

        verify(screen).showValidationError(EditGoalBehavior.ValidationError.NO_LINKED_HABITS)
    }

    @Test
    fun testEditExistingGoal() {
        val goal = fixtures.createGoal("Learn Guitar")
        goalList.add(goal)

        behavior = EditGoalBehavior(
            goal = goal,
            goalList,
            habitList,
            mock(),
            commandRunner,
            screen
        )

        val habit = fixtures.createEmptyHabit()
        habitList.add(habit)

        behavior.onNameChanged("Advanced Guitar")
        behavior.onAddHabit(habit.id!!)
        behavior.onSaveGoal()

        verify(screen).navigateBack()
    }

    @Test
    fun testAddHabitUpdatesForm() {
        behavior = EditGoalBehavior(
            goal = null,
            goalList,
            habitList,
            mock(),
            commandRunner,
            screen
        )

        val habit = fixtures.createEmptyHabit()
        habitList.add(habit)

        behavior.onNameChanged("Learn Guitar")
        behavior.onDeadlineChanged(Timestamp.today().plus(30))
        behavior.onAddHabit(habit.id!!)

        verify(screen).setFormValid(true)
    }

    @Test
    fun testRemoveHabitUpdatesForm() {
        val habit = fixtures.createEmptyHabit()
        habitList.add(habit)

        val goal = fixtures.createGoal(
            "Learn Guitar",
            linkedHabits = listOf(habit.id)
        )

        behavior = EditGoalBehavior(
            goal = goal,
            goalList,
            habitList,
            mock(),
            commandRunner,
            screen
        )

        behavior.onRemoveHabit(habit.id)

        verify(screen).setFormValid(false)
    }

    @Test
    fun testShowAvailableHabits() {
        behavior = EditGoalBehavior(
            goal = null,
            goalList,
            habitList,
            mock(),
            commandRunner,
            screen
        )

        val habit1 = fixtures.createEmptyHabit("Habit 1")
        val habit2 = fixtures.createEmptyHabit("Habit 2")
        habitList.add(habit1)
        habitList.add(habit2)

        behavior.showAvailableHabits()

        verify(screen).showHabitSelector(org.hamcrest.Matchers.any())
    }

    @Test
    fun testFormValidationOnNameChange() {
        behavior = EditGoalBehavior(
            goal = null,
            goalList,
            habitList,
            mock(),
            commandRunner,
            screen
        )

        behavior.onNameChanged("Valid Name")

        verify(screen).setFormValid(false)
    }

    @Test
    fun testDescriptionCanBeEmpty() {
        behavior = EditGoalBehavior(
            goal = null,
            goalList,
            habitList,
            mock(),
            commandRunner,
            screen
        )

        behavior.onDescriptionChanged("")

        val habit = fixtures.createEmptyHabit()
        habitList.add(habit)

        behavior.onNameChanged("Learn Guitar")
        behavior.onDeadlineChanged(Timestamp.today().plus(30))
        behavior.onAddHabit(habit.id)
        behavior.onSaveGoal()

        assertThat(goalList.getAll().size, equalTo(1))
    }

    @Test
    fun testCancel() {
        behavior = EditGoalBehavior(
            goal = null,
            goalList,
            habitList,
            mock(),
            commandRunner,
            screen
        )

        behavior.onCancel()

        verify(screen).navigateBack()
    }
}
