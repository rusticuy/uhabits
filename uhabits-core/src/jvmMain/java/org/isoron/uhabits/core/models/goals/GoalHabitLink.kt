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
package org.isoron.uhabits.core.models.goals

data class GoalHabitLink(
    var id: Long? = null,
    var goalId: Long? = null,
    var habitId: Long? = null,
    var weight: Double = 1.0
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GoalHabitLink) return false

        if (id != other.id) return false
        if (goalId != other.goalId) return false
        if (habitId != other.habitId) return false
        if (weight != other.weight) return false

        return true
    }

    override fun hashCode(): Int {
        var result = (id?.hashCode() ?: 0)
        result = 31 * result + (goalId?.hashCode() ?: 0)
        result = 31 * result + (habitId?.hashCode() ?: 0)
        result = 31 * result + weight.hashCode()
        return result
    }
}
    var goalId: Long = 0,
    var habitId: Long = 0,
    var weight: Double = 1.0
)
