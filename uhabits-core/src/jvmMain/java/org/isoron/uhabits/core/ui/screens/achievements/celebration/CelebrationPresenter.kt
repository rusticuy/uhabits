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

package org.isoron.uhabits.core.ui.screens.achievements.celebration

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.isoron.uhabits.core.models.Habit
import org.isoron.uhabits.core.models.PaletteColor
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CelebrationPresenter @Inject constructor() {

    private val _state = MutableStateFlow(CelebrationState())
    val state: StateFlow<CelebrationState> = _state.asStateFlow()

    fun showCelebration(
        habitName: String,
        achievementType: AchievementType,
        habitColor: PaletteColor,
        description: String
    ) {
        _state.value = CelebrationState(
            isVisible = true,
            habitName = habitName,
            achievementType = achievementType,
            habitColor = habitColor,
            description = description
        )
    }

    fun hideCelebration() {
        _state.value = _state.value.copy(isVisible = false)
    }

    fun onShareClicked() {
        // Share logic will be handled by the screen
        _state.value = _state.value.copy(shareRequested = true)
    }

    fun onViewHistoryClicked() {
        // History navigation will be handled by the screen
        _state.value = _state.value.copy(historyRequested = true)
    }

    fun onShareHandled() {
        _state.value = _state.value.copy(shareRequested = false)
    }

    fun onHistoryHandled() {
        _state.value = _state.value.copy(historyRequested = false)
    }
}

data class CelebrationState(
    val isVisible: Boolean = false,
    val habitName: String = "",
    val achievementType: AchievementType = AchievementType.STREAK,
    val habitColor: PaletteColor = PaletteColor.PURPLE,
    val description: String = "",
    val shareRequested: Boolean = false,
    val historyRequested: Boolean = false
)

enum class AchievementType {
    STREAK,
    PERFECT_WEEK,
    PERFECT_MONTH,
    MILESTONE
}