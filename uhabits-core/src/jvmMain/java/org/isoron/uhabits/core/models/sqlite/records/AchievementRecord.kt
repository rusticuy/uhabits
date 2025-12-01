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
import org.isoron.uhabits.core.models.achievements.AchievementDefinition
import org.isoron.uhabits.core.models.achievements.AchievementType

@Table(name = "Achievements")
class AchievementRecord {
    @field:Column
    var id: Long? = null

    @field:Column(name = "key")
    var key: String? = null

    @field:Column
    var name: String? = null

    @field:Column
    var description: String? = null

    @field:Column(name = "type")
    var type: Int? = null

    @field:Column(name = "streak_target")
    var streakTarget: Int? = null

    @field:Column(name = "habit_uuid")
    var habitUuid: String? = null

    @field:Column
    var icon: String? = null

    fun copyFrom(model: AchievementDefinition) {
        id = model.id
        key = model.key
        name = model.name
        description = model.description
        type = model.type.ordinal
        streakTarget = model.streakTarget
        habitUuid = model.habitUuid
        icon = model.icon
    }

    fun toModel(): AchievementDefinition {
        return AchievementDefinition(
            id = id,
            key = key!!,
            name = name!!,
            description = description!!,
            type = AchievementType.values()[type ?: 0],
            streakTarget = streakTarget,
            habitUuid = habitUuid,
            icon = icon
        )
    }
}
