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
package org.isoron.uhabits.core.models.achievements

import org.isoron.uhabits.core.database.Repository
import org.isoron.uhabits.core.models.sqlite.records.AchievementRecord
import org.isoron.uhabits.core.models.sqlite.records.AchievementUnlockRecord
import javax.inject.Inject

class AchievementRepository @Inject constructor(
    private val achievementRepo: Repository<AchievementRecord>,
    private val unlockRepo: Repository<AchievementUnlockRecord>
) {
    fun getAllDefinitions(): List<AchievementDefinition> {
        return achievementRepo.findAll("order by coalesce(streak_target, 0), id").map { it.toModel() }
    }

    fun getDefinitionByKey(key: String): AchievementDefinition? {
        return achievementRepo.findFirst("where key=?", key)?.toModel()
    }

    fun getAllUnlocks(): List<AchievementUnlock> {
        return unlockRepo.findAll("order by unlocked_at desc").map { it.toModel() }
    }

    fun getUnlocksForAchievement(achievementId: Long): List<AchievementUnlock> {
        return unlockRepo.findAll("where achievement_id=?", achievementId.toString()).map { it.toModel() }
    }

    fun isUnlocked(achievementId: Long, habitUuid: String? = null): Boolean {
        val query = if (habitUuid != null) {
            "where achievement_id=? and habit_uuid=?"
        } else {
            "where achievement_id=? and habit_uuid is null"
        }
        val params = if (habitUuid != null) {
            arrayOf(achievementId.toString(), habitUuid)
        } else {
            arrayOf(achievementId.toString())
        }
        return unlockRepo.findFirst(query, *params) != null
    }

    fun unlock(achievementId: Long, habitUuid: String? = null, timestamp: Long = System.currentTimeMillis()) {
        if (isUnlocked(achievementId, habitUuid)) return

        val unlock = AchievementUnlock(
            achievementId = achievementId,
            unlockedAt = timestamp,
            habitUuid = habitUuid
        )
        val record = AchievementUnlockRecord()
        record.copyFrom(unlock)
        unlockRepo.save(record)
    }

    fun saveDefinition(definition: AchievementDefinition) {
        val record = AchievementRecord()
        record.copyFrom(definition)
        achievementRepo.save(record)
        definition.id = record.id
    }
import kotlinx.coroutines.flow.Flow

interface AchievementRepository {
    fun observeUnlocks(): Flow<Achievement>
    
    fun markAsShown(achievementId: String)
    
    fun getLastShownId(): String?
}
