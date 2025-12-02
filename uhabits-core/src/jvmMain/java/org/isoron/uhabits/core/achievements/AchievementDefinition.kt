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

import org.isoron.uhabits.core.AppScope
import javax.inject.Inject

/**
 * Static metadata describing a single achievement that can be unlocked by a habit.
 */
data class AchievementDefinition(
    val id: String,
    val title: String,
    val description: String,
    val requiredStreak: Int,
    val habitId: Long? = null,
    val habitUuid: String? = null
)

interface AchievementDefinitionProvider {
    fun getDefinitions(): List<AchievementDefinition>
}

@AppScope
class DefaultAchievementDefinitionProvider @Inject constructor() : AchievementDefinitionProvider {
    private val definitions = listOf(
        AchievementDefinition(
            id = "streak_7",
            title = "One Week Streak",
            description = "Complete any habit for seven consecutive days.",
            requiredStreak = 7
        ),
        AchievementDefinition(
            id = "streak_30",
            title = "30-Day Momentum",
            description = "Stay consistent with any habit for a full month.",
            requiredStreak = 30
        ),
        AchievementDefinition(
            id = "streak_100",
            title = "100 Days Strong",
            description = "Reach a one-hundred-day streak on any habit.",
            requiredStreak = 100
        ),
        AchievementDefinition(
            id = "streak_365",
            title = "Year of Habits",
            description = "Keep a habit alive for one full year without breaking the streak.",
            requiredStreak = 365
        )
    )

    override fun getDefinitions(): List<AchievementDefinition> = definitions
}
