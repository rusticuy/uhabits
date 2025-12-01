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

import org.isoron.uhabits.core.models.ModelObservable
import org.isoron.uhabits.core.models.PaletteColor
import org.isoron.uhabits.core.models.memory.MemoryGoalMilestoneList
import java.util.UUID

data class Goal(
    var color: PaletteColor = PaletteColor(8),
    var description: String = "",
    var id: Long? = null,
    var isArchived: Boolean = false,
    var name: String = "",
    var position: Int = 0,
    var uuid: String? = null,
    var dueDate: Long? = null,
    val milestones: GoalMilestoneList = MemoryGoalMilestoneList()
) {
    init {
        if (uuid == null) this.uuid = UUID.randomUUID().toString().replace("-", "")
    }

    var observable = ModelObservable()

    fun copyFrom(other: Goal) {
        this.color = other.color
        this.description = other.description
        this.isArchived = other.isArchived
        this.name = other.name
        this.position = other.position
        this.uuid = other.uuid
        this.dueDate = other.dueDate
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Goal) return false

        if (color != other.color) return false
        if (description != other.description) return false
        if (id != other.id) return false
        if (isArchived != other.isArchived) return false
        if (name != other.name) return false
        if (position != other.position) return false
        if (uuid != other.uuid) return false
        if (dueDate != other.dueDate) return false

        return true
    }

    override fun hashCode(): Int {
        var result = color.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + (id?.hashCode() ?: 0)
        result = 31 * result + isArchived.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + position
        result = 31 * result + (uuid?.hashCode() ?: 0)
        result = 31 * result + (dueDate?.hashCode() ?: 0)
        return result
    }
}
