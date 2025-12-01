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
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.isoron.uhabits.core.models.achievements

import org.isoron.uhabits.core.models.Habit

/**
 * Represents an achievement that can be unlocked by users.
 */
data class Achievement(
    val id: String,
    val type: AchievementType,
    val title: String,
    val description: String,
    val iconResource: String,
    val habitId: Long? = null,
    val unlockedAt: Long? = null
)

/**
 * Types of achievements that can be unlocked.
 */
enum class AchievementType {
    STREAK_DAYS_7,
    STREAK_DAYS_21,
    STREAK_DAYS_50,
    STREAK_DAYS_100,
    STREAK_DAYS_365,
    PERFECT_WEEK,
    PERFECT_MONTH,
    TOTAL_CHECKMARKS_100,
    TOTAL_CHECKMARKS_500,
    TOTAL_CHECKMARKS_1000,
    EARLY_BIRD,
    NIGHT_OWL
}

/**
 * Event emitted when an achievement is unlocked.
 */
data class AchievementUnlockedEvent(
    val achievement: Achievement,
    val habit: Habit? = null,
    val timestamp: Long = System.currentTimeMillis()
)