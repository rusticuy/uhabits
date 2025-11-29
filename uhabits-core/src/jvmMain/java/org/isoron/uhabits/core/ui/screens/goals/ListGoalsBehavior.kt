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
import org.isoron.uhabits.core.commands.DeleteGoalCommand
import org.isoron.uhabits.core.models.Goal
import org.isoron.uhabits.core.models.GoalList
import org.isoron.uhabits.core.preferences.Preferences
import javax.inject.Inject

open class ListGoalsBehavior @Inject constructor(
    private val goalList: GoalList,
    private val screen: Screen,
    private val commandRunner: CommandRunner,
    private val prefs: Preferences
) {
    private var sortOrder: SortOrder = SortOrder.NEWEST
    private var showArchived: Boolean = false

    fun onClickGoal(goal: Goal) {
        screen.showGoalScreen(goal)
    }

    fun onClickCreateGoal() {
        screen.showEditGoalScreen(null)
    }

    fun onDeleteGoal(goal: Goal) {
        commandRunner.run(DeleteGoalCommand(goalList, goal))
    }

    fun onArchiveGoal(goal: Goal) {
        screen.showArchiveConfirmation(goal)
    }

    fun setSortOrder(order: SortOrder) {
        sortOrder = order
        screen.updateGoalList(getFilteredGoals())
    }

    fun setShowArchived(archived: Boolean) {
        showArchived = archived
        screen.updateGoalList(getFilteredGoals())
    }

    fun onStartup() {
        screen.updateGoalList(getFilteredGoals())
    }

    private fun getFilteredGoals(): List<Goal> {
        val allGoals = goalList.getArchived(showArchived)
        return when (sortOrder) {
            SortOrder.NEWEST -> allGoals.sortedByDescending { it.createdAt.toMillis() }
            SortOrder.OLDEST -> allGoals.sortedBy { it.createdAt.toMillis() }
            SortOrder.NAME -> allGoals.sortedBy { it.name }
            SortOrder.PROGRESS -> allGoals.sortedByDescending { it.targetValue }
        }
    }

    enum class SortOrder {
        NEWEST,
        OLDEST,
        NAME,
        PROGRESS
    }

    enum class Message {
        GOAL_DELETED,
        GOAL_ARCHIVED,
        GOAL_UNARCHIVED
    }

    interface Screen {
        fun showGoalScreen(goal: Goal)
        fun showEditGoalScreen(goal: Goal?)
        fun updateGoalList(goals: List<Goal>)
        fun showArchiveConfirmation(goal: Goal)
        fun showMessage(message: Message)
    }
}
