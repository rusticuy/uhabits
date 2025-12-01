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

import org.isoron.uhabits.core.commands.CommandRunner
import org.isoron.uhabits.core.commands.CreateGoalCommand
import org.isoron.uhabits.core.commands.EditGoalCommand
import org.isoron.uhabits.core.models.goals.Goal
import org.isoron.uhabits.core.models.goals.GoalList
import org.isoron.uhabits.core.models.HabitList
import org.isoron.uhabits.core.models.Timestamp
import org.isoron.uhabits.core.preferences.Preferences
import javax.inject.Inject

open class EditGoalBehavior @Inject constructor(
    private val goal: Goal?,
    private val goalList: GoalList,
    private val habitList: HabitList,
    private val preferences: Preferences,
    private val commandRunner: CommandRunner,
    private val screen: Screen
) {
    private var currentName: String = goal?.name ?: ""
    private var currentDescription: String = goal?.description ?: ""
    private var currentDeadline: Timestamp = goal?.deadlineDate ?: Timestamp(0)
    private var currentLinkedHabits: MutableList<Long> = goal?.linkedHabits?.toMutableList() ?: mutableListOf()

    fun onNameChanged(name: String) {
        currentName = name
        validateForm()
    }

    fun onDescriptionChanged(description: String) {
        currentDescription = description
    }

    fun onDeadlineChanged(deadline: Timestamp) {
        currentDeadline = deadline
        validateForm()
    }

    fun onAddHabit(habitId: Long) {
        if (!currentLinkedHabits.contains(habitId)) {
            currentLinkedHabits.add(habitId)
            validateForm()
        }
    }

    fun onRemoveHabit(habitId: Long) {
        currentLinkedHabits.remove(habitId)
        validateForm()
    }

    fun showAvailableHabits() {
        val allHabits = habitList.toList()
        val availableHabits = allHabits
            .filter { habit -> !currentLinkedHabits.contains(habit.id) }
            .map { habit -> HabitSummary(habitId = habit.id!!, name = habit.name, color = habit.color) }
        screen.showHabitSelector(availableHabits)
    }

    fun onSaveGoal() {
        if (!isFormValid()) {
            screen.showValidationError(ValidationError.EMPTY_NAME)
            return
        }

        if (currentLinkedHabits.isEmpty()) {
            screen.showValidationError(ValidationError.NO_LINKED_HABITS)
            return
        }

        val updatedGoal = if (goal != null) {
            goal.copy(
                name = currentName,
                description = currentDescription,
                deadlineDate = currentDeadline,
                linkedHabits = currentLinkedHabits
            )
        } else {
            Goal(
                id = System.currentTimeMillis(),
                name = currentName,
                description = currentDescription,
                deadlineDate = currentDeadline,
                linkedHabits = currentLinkedHabits,
                createdAt = Timestamp.today()
            )
        }

        if (goal != null) {
            commandRunner.run(EditGoalCommand(goalList, updatedGoal))
        } else {
            commandRunner.run(
                CreateGoalCommand(
                    goalList,
                    currentName,
                    currentDescription,
                    currentDeadline,
                    currentLinkedHabits
                )
            )
        }

        screen.showMessage(if (goal != null) "Goal updated" else "Goal created")
        screen.navigateBack()
    }

    fun onCancel() {
        screen.navigateBack()
    }

    private fun validateForm() {
        screen.setFormValid(isFormValid())
    }

    private fun isFormValid(): Boolean {
        return currentName.isNotBlank() && currentDeadline.toMillis() > 0
    }

    enum class ValidationError {
        EMPTY_NAME,
        NO_LINKED_HABITS,
        INVALID_DEADLINE
    }

    interface Screen {
        fun showValidationError(error: ValidationError)
        fun showMessage(message: String)
        fun navigateBack()
        fun setFormValid(valid: Boolean)
        fun showHabitSelector(availableHabits: List<HabitSummary>)
    }
}
