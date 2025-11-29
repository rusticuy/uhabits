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

import java.util.Objects

data class Milestone(
    var id: Long? = null,
    var goalId: Long? = null,
    var targetValue: Double = 0.0,
    var isCompleted: Boolean = false
)

data class Goal(
    var id: Long? = null,
    var name: String = "",
    var description: String = "",
    var color: PaletteColor = PaletteColor(8),
    var deadline: Timestamp = Timestamp(System.currentTimeMillis()),
    var isArchived: Boolean = false,
    val linkedHabits: MutableList<Long> = mutableListOf(),
    val milestones: MutableList<Milestone> = mutableListOf()
) {
    val uriString: String
        get() = "content://org.isoron.uhabits.goals/$id"

    override fun hashCode() = Objects.hash(id)

    override fun equals(other: Any?) = when (other) {
        !is Goal -> false
        else -> id == other.id
    }
}
