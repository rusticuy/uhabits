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

import org.isoron.uhabits.core.models.Goal
import org.isoron.uhabits.core.models.GoalProgressCalculator
import org.isoron.uhabits.core.models.PaletteColor

class GoalCardMapper(private val progressCalculator: GoalProgressCalculator) {
    fun toGoalCardState(goal: Goal): GoalCardState {
        val progress = progressCalculator.calculateProgressToday(goal)
        val nextMilestone = goal.milestones.firstOrNull()?.title ?: ""
        val nextMilestoneProgress = if (goal.milestones.isNotEmpty()) {
            progressCalculator.calculateMilestoneProgress(goal, goal.milestones.first()).toInt()
        } else {
            0
        }

        return GoalCardState(
            id = goal.id,
            name = goal.name,
            description = goal.description,
            progressPercent = progress,
            color = PaletteColor(1),
            daysUntilDeadline = goal.daysUntilDeadline(),
            nextMilestone = nextMilestone,
            nextMilestoneProgress = nextMilestoneProgress,
            archived = goal.archived,
            isOverdue = goal.isOverdue(),
            linkedHabitCount = goal.linkedHabits.size
        )
    }
}
