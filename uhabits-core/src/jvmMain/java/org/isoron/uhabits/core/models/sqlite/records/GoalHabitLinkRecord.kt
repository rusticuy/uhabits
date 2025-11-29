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
package org.isoron.uhabits.core.models.sqlite.records

import org.isoron.uhabits.core.database.Column
import org.isoron.uhabits.core.database.Table
import org.isoron.uhabits.core.models.goals.GoalHabitLink

@Table(name = "GoalHabitLinks")
class GoalHabitLinkRecord {
    @field:Column
    var id: Long? = null

    @field:Column(name = "goal_id")
    var goalId: Long? = null

    @field:Column(name = "habit_id")
    var habitId: Long? = null

    @field:Column
    var weight: Double? = null

    fun copyFrom(model: GoalHabitLink) {
        id = model.id
        goalId = model.goalId
        habitId = model.habitId
        weight = model.weight
    }

    fun copyTo(link: GoalHabitLink) {
        link.id = id
        link.goalId = goalId
        link.habitId = habitId
        link.weight = weight!!
    }
}
