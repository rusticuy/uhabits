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
package org.isoron.uhabits.core.insights

import org.isoron.uhabits.core.models.Habit

data class Insight(
    val id: String,
    val type: InsightType,
    val title: String,
    val body: String,
    val habit: Habit?,
    val suggestion: InsightSuggestion?,
    val priority: Int
)

enum class InsightType {
    STREAK_DECLINING,
    LOW_COMPLETION,
    REMINDER_SUGGESTION,
    CONSISTENCY_IMPROVING,
    HABIT_MILESTONE,
    WEEKLY_SUMMARY
}

sealed class InsightSuggestion {
    data class AdjustReminder(
        val habitId: Long,
        val suggestedHour: Int,
        val suggestedMinute: Int
    ) : InsightSuggestion()

    data class CreateHabit(
        val name: String,
        val description: String,
        val frequency: Int
    ) : InsightSuggestion()

    data class AdjustFrequency(
        val habitId: Long,
        val suggestedFrequency: Int
    ) : InsightSuggestion()
}
