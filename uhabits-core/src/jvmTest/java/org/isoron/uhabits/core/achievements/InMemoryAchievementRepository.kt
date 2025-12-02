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
package org.isoron.uhabits.core.achievements

class InMemoryAchievementRepository : AchievementRepository {
    private val unlocks = mutableListOf<AchievementUnlockRecord>()

    @Synchronized
    override fun save(unlock: AchievementUnlockRecord) {
        unlocks.add(unlock)
    }

    @Synchronized
    override fun getUnlocks(): List<AchievementUnlockRecord> = unlocks.toList()

    @Synchronized
    override fun getUnlocksForHabit(habitId: Long?, habitUuid: String?): List<AchievementUnlockRecord> {
        return unlocks.filter { it.belongsTo(habitId, habitUuid) }
    }

    @Synchronized
    override fun isUnlocked(definitionId: String, habitId: Long?, habitUuid: String?): Boolean {
        return unlocks.any { it.definitionId == definitionId && it.belongsTo(habitId, habitUuid) }
    }

    @Synchronized
    override fun clear() {
        unlocks.clear()
    }

    private fun AchievementUnlockRecord.belongsTo(habitId: Long?, habitUuid: String?): Boolean {
        if (habitId != null && this.habitId != null && habitId == this.habitId) return true
        if (habitUuid != null && this.habitUuid != null && habitUuid == this.habitUuid) return true
        return false
    }
}
