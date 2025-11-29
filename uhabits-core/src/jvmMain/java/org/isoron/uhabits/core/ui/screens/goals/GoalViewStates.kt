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

import org.isoron.uhabits.core.models.PaletteColor

data class GoalCardState(
    val id: Long = 0,
    val name: String = "",
    val description: String = "",
    val progressPercent: Int = 0,
    val color: PaletteColor = PaletteColor(1),
    val daysUntilDeadline: Long = 0,
    val nextMilestone: String = "",
    val nextMilestoneProgress: Int = 0,
    val archived: Boolean = false,
    val isOverdue: Boolean = false,
    val linkedHabitCount: Int = 0
)

data class GoalDetailState(
    val id: Long = 0,
    val title: String = "",
    val description: String = "",
    val daysUntilDeadline: Long = 0,
    val progressPercent: Int = 0,
    val progressChartData: List<ProgressDataPoint> = emptyList(),
    val linkedHabits: List<HabitSummary> = emptyList(),
    val milestones: List<MilestoneChip> = emptyList(),
    val archived: Boolean = false,
    val isOverdue: Boolean = false,
    val color: PaletteColor = PaletteColor(1)
)

data class ProgressDataPoint(
    val date: String = "",
    val progress: Int = 0
)

data class HabitSummary(
    val habitId: Long = 0,
    val name: String = "",
    val progress: Int = 0,
    val color: PaletteColor = PaletteColor(1)
)

data class MilestoneChip(
    val id: Long = 0,
    val title: String = "",
    val targetValue: Double = 0.0,
    val progress: Double = 0.0,
    val daysUntilDeadline: Long = 0,
    val completed: Boolean = false
)
