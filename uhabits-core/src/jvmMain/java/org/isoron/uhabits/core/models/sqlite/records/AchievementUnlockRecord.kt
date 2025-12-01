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
import org.isoron.uhabits.core.models.achievements.AchievementUnlock

@Table(name = "AchievementUnlocks")
class AchievementUnlockRecord {
    @field:Column
    var id: Long? = null

    @field:Column(name = "achievement_id")
    var achievementId: Long? = null

    @field:Column(name = "unlocked_at")
    var unlockedAt: Long? = null

    @field:Column(name = "habit_uuid")
    var habitUuid: String? = null

    fun copyFrom(model: AchievementUnlock) {
        id = model.id
        achievementId = model.achievementId
        unlockedAt = model.unlockedAt
        habitUuid = model.habitUuid
    }

    fun toModel(): AchievementUnlock {
        return AchievementUnlock(
            id = id,
            achievementId = achievementId!!,
            unlockedAt = unlockedAt!!,
            habitUuid = habitUuid
        )
    }
}
