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
import org.isoron.uhabits.core.models.PaletteColor
import org.isoron.uhabits.core.models.goals.Goal

@Table(name = "Goals")
class GoalRecord {
    @field:Column
    var id: Long? = null

    @field:Column
    var uuid: String? = null

    @field:Column
    var name: String? = null

    @field:Column
    var description: String? = null

    @field:Column
    var color: Int? = null

    @field:Column
    var archived: Int? = null

    @field:Column
    var position: Int? = null

    @field:Column(name = "due_date")
    var dueDate: Long? = null

    fun copyFrom(model: Goal) {
        id = model.id
        uuid = model.uuid
        name = model.name
        description = model.description
        color = model.color.paletteIndex
        archived = if (model.isArchived) 1 else 0
        position = model.position
        dueDate = model.dueDate
    }

    fun copyTo(goal: Goal) {
        goal.id = id
        goal.uuid = uuid
        goal.name = name!!
        goal.description = description!!
        goal.color = PaletteColor(color!!)
        goal.isArchived = archived != 0
        goal.position = position!!
        goal.dueDate = dueDate
    }
}
