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

package org.isoron.uhabits.core.ui.screens.achievements.history

data class AchievementHistoryState(
    val sections: List<HistorySectionState> = emptyList(),
    val currentFilter: AchievementFilter = AchievementFilter.ALL,
    val currentGrouping: AchievementGrouping = AchievementGrouping.BY_CATEGORY,
    val searchQuery: String = ""
)

data class HistorySectionState(
    val title: String,
    val entries: List<HistoryEntryState> = emptyList()
)

data class HistoryEntryState(
    val id: Long,
    val name: String,
    val description: String,
    val icon: String? = null,
    val unlockedAt: Long? = null,
    val habitUuid: String? = null,
    val habitName: String? = null,
    val isUnlocked: Boolean = false,
    val progress: Int = 0
)

enum class AchievementFilter {
    ALL,
    UNLOCKED,
    LOCKED,
    IN_PROGRESS
}

enum class AchievementGrouping {
    BY_CATEGORY,
    BY_TIMEFRAME
}
