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
package org.isoron.uhabits.core.models.memory

import org.isoron.uhabits.core.models.Achievement
import org.isoron.uhabits.core.models.AchievementList

class MemoryAchievementList : AchievementList() {
    private val achievements = ArrayList<Achievement>()
    private var nextId = 1L

    @Synchronized
    override fun add(achievement: Achievement): Long {
        achievement.id = nextId++
        achievements.add(achievement)
        return achievement.id!!
    }

    @Synchronized
    override fun update(achievement: Achievement) {
        val index = achievements.indexOfFirst { it.id == achievement.id }
        if (index >= 0) {
            achievements[index] = achievement
        }
    }

    @Synchronized
    override fun remove(achievement: Achievement) {
        achievements.removeIf { it.id == achievement.id }
    }

    @Synchronized
    override fun getAll(): List<Achievement> {
        return achievements.sortedByDescending { it.unlockedAt ?: Long.MAX_VALUE }
    }

    @Synchronized
    override fun getByHabit(habitId: Long): List<Achievement> {
        return achievements
            .filter { it.habitId == habitId }
            .sortedByDescending { it.unlockedAt ?: Long.MAX_VALUE }
    }

    @Synchronized
    override fun getByStreakTier(streakTier: Int): List<Achievement> {
        return achievements
            .filter { it.streakTier == streakTier }
            .sortedByDescending { it.unlockedAt ?: Long.MAX_VALUE }
    }

    @Synchronized
    override fun getUnlocked(): List<Achievement> {
        return achievements
            .filter { it.isUnlocked }
            .sortedByDescending { it.unlockedAt }
    }

    @Synchronized
    override fun getLocked(): List<Achievement> {
        return achievements
            .filter { !it.isUnlocked }
            .sortedBy { it.streakTier }
    }
}
