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
import java.util.UUID

data class Goal(
    var id: Long? = null,
    var name: String = "",
    var description: String = "",
    var targetValue: Double = 0.0,
    var isArchived: Boolean = false,
    var deadline: Long? = null,
    var uuid: String? = null
) {
    init {
        if (uuid == null) this.uuid = UUID.randomUUID().toString().replace("-", "")
    }

    var observable = ModelObservable()

    fun copyFrom(other: Goal) {
        this.name = other.name
        this.description = other.description
        this.targetValue = other.targetValue
        this.isArchived = other.isArchived
        this.deadline = other.deadline
        this.uuid = other.uuid
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Goal) return false

        if (id != other.id) return false
        if (name != other.name) return false
        if (description != other.description) return false
        if (targetValue != other.targetValue) return false
        if (isArchived != other.isArchived) return false
        if (deadline != other.deadline) return false
        if (uuid != other.uuid) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + name.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + targetValue.hashCode()
        result = 31 * result + isArchived.hashCode()
        result = 31 * result + (deadline?.hashCode() ?: 0)
        result = 31 * result + (uuid?.hashCode() ?: 0)
        return result
    }
}
