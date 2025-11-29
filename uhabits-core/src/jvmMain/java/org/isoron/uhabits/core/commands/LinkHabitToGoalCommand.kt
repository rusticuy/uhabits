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
package org.isoron.uhabits.core.commands

import org.isoron.uhabits.core.models.HabitList
import org.isoron.uhabits.core.models.goals.GoalHabitLink
import org.isoron.uhabits.core.models.goals.GoalList

data class LinkHabitToGoalCommand(
    val goalList: GoalList,
    val habitList: HabitList,
    val goalId: Long,
    val habitId: Long,
    val weight: Double = 1.0
) : Command {
    override fun run() {
        val goal = goalList.getById(goalId) ?: throw IllegalArgumentException("Goal not found")
        val habit = habitList.getById(habitId) ?: throw IllegalArgumentException("Habit not found")

        val link = GoalHabitLink(
            goalId = goalId,
            habitId = habitId,
            weight = weight
        )
        goalList.addHabitLink(link)
    }
}
