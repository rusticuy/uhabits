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

import org.isoron.uhabits.core.utils.DateUtils
import java.util.UUID

/**
 * Represents an achievement earned by the user for habit tracking milestones.
 */
data class Achievement(
    val id: Long? = null,
    val uuid: String = UUID.randomUUID().toString().replace("-", ""),
    val habitId: Long,
    val type: AchievementType,
    val title: String,
    val description: String,
    val timestamp: Timestamp,
    val value: Int = 0, // The value associated with the achievement (e.g., streak length)
    val isSeen: Boolean = false,
    val isCelebrated: Boolean = false
) {
    /**
     * Returns the epoch time in milliseconds for this achievement's timestamp.
     */
    fun epochTime(): Long = DateUtils.millisecondsFromLocalDate(timestamp.toLocalDate())

    /**
     * Returns the URI string for this achievement.
     */
    val uriString: String
        get() = "content://org.isoron.uhabits/achievements/$uuid"

    /**
     * Returns a copy of this achievement with the given habit.
     */
    fun withHabit(habit: Habit): Achievement {
        return copy(habitId = habit.id ?: throw IllegalStateException("Habit has no id"))
    }

    /**
     * Returns a copy of this achievement marked as seen.
     */
    fun markAsSeen(): Achievement = copy(isSeen = true)

    /**
     * Returns a copy of this achievement marked as celebrated.
     */
    fun markAsCelebrated(): Achievement = copy(isCelebrated = true)
}

/**
 * Enum representing different types of achievements.
 */
enum class AchievementType {
    STREAK,           // Consecutive days completed
    PERFECT_WEEK,     // Perfect week (7 days)
    PERFECT_MONTH,    // Perfect month (30/31 days)
    TOTAL_CHECKS,     // Total number of checkmarks
    LONGEST_STREAK,   // Longest streak achieved
    EARLY_BIRD,       // Early morning completions
    NIGHT_OWL,        // Late night completions
    CONSISTENCY,      // Consistent tracking over time
    MILESTONE         // Custom milestone achievements
}