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

data class Goal(
    val id: Long = 0,
    val name: String = "",
    val description: String = "",
    val targetValue: Double = 0.0,
    val deadlineDate: Timestamp = Timestamp(0),
    val goalType: String = "CUMULATIVE",
    val archived: Boolean = false,
    val createdAt: Timestamp = Timestamp(0),
    val linkedHabits: List<Long> = emptyList(),
    val milestones: List<GoalMilestone> = emptyList()
) {
    fun isOverdue(): Boolean {
        val today = Timestamp.today()
        return deadlineDate < today && !isComplete()
    }

    fun isComplete(): Boolean {
        return targetValue <= 0.0
    }

    fun daysUntilDeadline(): Long {
        val today = Timestamp.today()
        return if (deadlineDate < today) 0L else (deadlineDate.toCalendar().timeInMillis - today.toCalendar().timeInMillis) / (1000 * 60 * 60 * 24)
    }
}

data class GoalMilestone(
    val id: Long = 0,
    val goalId: Long = 0,
    val title: String = "",
    val targetValue: Double = 0.0,
    val deadlineDate: Timestamp = Timestamp(0),
    val completed: Boolean = false
)

data class GoalHabitLink(
    val id: Long = 0,
    val goalId: Long = 0,
    val habitId: Long = 0,
    val weight: Double = 1.0
)
