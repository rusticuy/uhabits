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
package org.isoron.uhabits.core.models

import javax.annotation.concurrent.ThreadSafe

@ThreadSafe
abstract class AchievementList {
    abstract fun add(achievement: Achievement): Long
    abstract fun update(achievement: Achievement)
    abstract fun remove(achievement: Achievement)
    abstract fun getAll(): List<Achievement>
    abstract fun getByHabit(habitId: Long): List<Achievement>
    abstract fun getByStreakTier(streakTier: Int): List<Achievement>
    abstract fun getUnlocked(): List<Achievement>
    abstract fun getLocked(): List<Achievement>
}
