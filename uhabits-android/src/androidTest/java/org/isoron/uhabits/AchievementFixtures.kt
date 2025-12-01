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
package org.isoron.uhabits

import org.isoron.uhabits.core.models.Achievement
import org.isoron.uhabits.core.models.AchievementList

class AchievementFixtures(private val achievementList: AchievementList) {

    fun createSampleAchievements(habitId: Long) {
        // Create unlocked achievements
        val achievement7 = Achievement(
            habitId = habitId,
            streakTier = 7,
            unlockedAt = System.currentTimeMillis() - 86400000 * 30 // 30 days ago
        )
        achievementList.add(achievement7)

        val achievement30 = Achievement(
            habitId = habitId,
            streakTier = 30,
            unlockedAt = System.currentTimeMillis() - 86400000 * 20 // 20 days ago
        )
        achievementList.add(achievement30)

        val achievement100 = Achievement(
            habitId = habitId,
            streakTier = 100,
            unlockedAt = System.currentTimeMillis() - 86400000 * 10 // 10 days ago
        )
        achievementList.add(achievement100)

        // Create locked achievements
        val achievement50 = Achievement(
            habitId = habitId,
            streakTier = 50,
            unlockedAt = null
        )
        achievementList.add(achievement50)

        val achievement365 = Achievement(
            habitId = habitId,
            streakTier = 365,
            unlockedAt = null
        )
        achievementList.add(achievement365)
    }

    fun purgeAchievements(achievementList: AchievementList) {
        achievementList.getAll().forEach { achievement ->
            achievementList.remove(achievement)
        }
    }
}
