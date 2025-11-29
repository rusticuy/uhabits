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
import org.isoron.uhabits.core.models.Goal
import org.isoron.uhabits.core.models.GoalList
import org.isoron.uhabits.core.models.GoalProgressCalculator
import org.isoron.uhabits.core.models.HabitList
import org.isoron.uhabits.core.preferences.Preferences
import javax.inject.Inject

open class ShowGoalBehavior @Inject constructor(
    private val goal: Goal,
    private val goalList: GoalList,
    private val habitList: HabitList,
    private val progressCalculator: GoalProgressCalculator,
    private val preferences: Preferences,
    private val commandRunner: CommandRunner,
    private val screen: Screen
) {
    fun buildState(): GoalDetailState {
        val progress = progressCalculator.calculateProgressToday(goal).toInt()

        val linkedHabits = goal.linkedHabits.mapNotNull { habitId ->
            habitList.getById(habitId)?.let { habit ->
                val today = org.isoron.uhabits.core.models.Timestamp.today()
                HabitSummary(
                    habitId = habit.id!!,
                    name = habit.name,
                    progress = habit.scores.get(today).value.toInt(),
                    color = habit.color
                )
            }
        }

        val milestones = goal.milestones.map { milestone ->
            val milestoneProgress = progressCalculator.calculateMilestoneProgress(goal, milestone)
            MilestoneChip(
                id = milestone.id,
                title = milestone.title,
                targetValue = milestone.targetValue,
                progress = milestoneProgress,
                daysUntilDeadline = org.isoron.uhabits.core.models.Timestamp.today().daysUntil(milestone.deadlineDate).toLong(),
                completed = milestone.completed
            )
        }

        return GoalDetailState(
            id = goal.id,
            title = goal.name,
            description = goal.description,
            daysUntilDeadline = goal.daysUntilDeadline(),
            progressPercent = progress,
            progressChartData = buildProgressChart(),
            linkedHabits = linkedHabits,
            milestones = milestones,
            archived = goal.archived,
            isOverdue = goal.isOverdue(),
            color = org.isoron.uhabits.core.models.PaletteColor(1)
        )
    }

    fun onClickEditGoal() {
        screen.showEditGoalScreen(goal)
    }

    fun onClickDeleteGoal() {
        screen.showDeleteConfirmation()
    }

    fun onClickArchiveGoal() {
        screen.showArchiveConfirmation()
    }

    private fun buildProgressChart(): List<ProgressDataPoint> {
        return emptyList()
    }

    interface Screen {
        fun updateState(state: GoalDetailState)
        fun showEditGoalScreen(goal: Goal)
        fun showDeleteConfirmation()
        fun showArchiveConfirmation()
    }
}
