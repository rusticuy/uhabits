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
import org.isoron.uhabits.core.models.goals.GoalMilestone

@Table(name = "GoalMilestones")
class GoalMilestoneRecord {
    @field:Column
    var id: Long? = null

    @field:Column
    var uuid: String? = null

    @field:Column(name = "goal_id")
    var goalId: Long? = null

    @field:Column
    var name: String? = null

    @field:Column
    var description: String? = null

    @field:Column(name = "target_value")
    var targetValue: Double? = null

    @field:Column(name = "due_date")
    var dueDate: Long? = null

    @field:Column
    var position: Int? = null

    @field:Column(name = "is_completed")
    var isCompleted: Int? = null

    fun copyFrom(model: GoalMilestone) {
        id = model.id
        uuid = model.uuid
        goalId = model.goalId
        name = model.name
        description = model.description
        targetValue = model.targetValue
        dueDate = model.dueDate
        position = model.position
        isCompleted = if (model.isCompleted) 1 else 0
    }

    fun copyTo(milestone: GoalMilestone) {
        milestone.id = id
        milestone.uuid = uuid
        milestone.goalId = goalId
        milestone.name = name!!
        milestone.description = description!!
        milestone.targetValue = targetValue!!
        milestone.dueDate = dueDate
        milestone.position = position!!
        milestone.isCompleted = isCompleted != 0
    }
}
