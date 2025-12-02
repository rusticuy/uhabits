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

package org.isoron.uhabits.activities.achievements.history

import org.isoron.uhabits.core.models.Achievement
import org.isoron.uhabits.core.models.AchievementType
import org.isoron.uhabits.core.models.Habit
import org.isoron.uhabits.core.models.PaletteColor
import org.isoron.uhabits.core.models.Timestamp

/**
 * Utility class for creating sample achievements for testing and demonstration.
 */
object AchievementSampleData {

    fun createSampleHabits(): List<Habit> {
        return listOf(
            Habit(
                name = "Morning Meditation",
                color = PaletteColor(0),
                description = "Start the day with mindfulness"
            ),
            Habit(
                name = "Daily Exercise",
                color = PaletteColor(5),
                description = "Stay active and healthy"
            ),
            Habit(
                name = "Read Books",
                color = PaletteColor(10),
                description = "Expand knowledge through reading"
            )
        )
    }

    fun createSampleAchievements(): List<Achievement> {
        val today = Timestamp.getToday()
        return listOf(
            Achievement(
                habitId = 1L,
                type = AchievementType.STREAK,
                title = "7 Day Streak!",
                description = "Completed Morning Meditation for 7 days straight",
                timestamp = today,
                value = 7
            ),
            Achievement(
                habitId = 2L,
                type = AchievementType.PERFECT_WEEK,
                title = "Perfect Week",
                description = "Completed Daily Exercise every day this week",
                timestamp = today.minus(1),
                value = 7
            ),
            Achievement(
                habitId = 3L,
                type = AchievementType.TOTAL_CHECKS,
                title = "100 Checkmarks",
                description = "Reached 100 total checkmarks for Read Books",
                timestamp = today.minus(2),
                value = 100
            ),
            Achievement(
                habitId = 1L,
                type = AchievementType.LONGEST_STREAK,
                title = "Personal Best: 30 Days",
                description = "Longest streak for Morning Meditation",
                timestamp = today.minus(3),
                value = 30
            ),
            Achievement(
                habitId = 2L,
                type = AchievementType.EARLY_BIRD,
                title = "Early Bird",
                description = "Completed Daily Exercise before 6 AM",
                timestamp = today.minus(4),
                value = 1
            ),
            Achievement(
                habitId = 3L,
                type = AchievementType.MILESTONE,
                title = "Reading Enthusiast",
                description = "Read for 50 consecutive days",
                timestamp = today.minus(5),
                value = 50
            )
        )
    }

    fun populateWithSampleData(
        achievementList: org.isoron.uhabits.core.models.AchievementList,
        habitList: org.isoron.uhabits.core.models.HabitList
    ) {
        // Add sample habits first
        createSampleHabits().forEach { habit ->
            habitList.add(habit)
        }
        
        // Add sample achievements
        createSampleAchievements().forEach { achievement ->
            achievementList.add(achievement)
        }
    }
}