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

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.isoron.uhabits.core.models.achievements.Achievement
import org.isoron.uhabits.core.models.achievements.AchievementRepository
import org.isoron.uhabits.core.preferences.Preferences
import org.isoron.uhabits.core.utils.Clock

class CelebrationPresenter(
    private val achievementRepository: AchievementRepository,
    private val preferences: Preferences,
    private val clock: Clock,
    private val screen: Screen
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val _state = MutableStateFlow<CelebrationState?>(null)
    val state: StateFlow<CelebrationState?> = _state.asStateFlow()
    
    private var currentAchievementId: String? = null
    private val shownAchievements = mutableSetOf<String>()
    
    init {
        observeAchievementUnlocks()
    }
    
    private fun observeAchievementUnlocks() {
        scope.launch {
            achievementRepository.observeUnlocks().collect { achievement ->
                handleNewAchievement(achievement)
            }
        }
    }
    
    private fun handleNewAchievement(achievement: Achievement) {
        if (shownAchievements.contains(achievement.id)) {
            return
        }
        
        val lastShownId = achievementRepository.getLastShownId()
        if (lastShownId == achievement.id) {
            return
        }
        
        currentAchievementId = achievement.id
        shownAchievements.add(achievement.id)
        
        val playConfetti = !preferences.isConfettiAnimationDisabled
        val celebrationState = AchievementMapper.toState(
            achievement = achievement,
            playConfetti = playConfetti
        )
        
        _state.value = celebrationState
        
        celebrationState.autoDismissMillis?.let { autoDismissDelay ->
            scope.launch {
                delay(autoDismissDelay)
                if (_state.value?.achievementId == achievement.id) {
                    acknowledge()
                }
            }
        }
    }
    
    fun acknowledge() {
        currentAchievementId?.let { id ->
            achievementRepository.markAsShown(id)
        }
        _state.value = null
        currentAchievementId = null
        screen.dismiss()
    }
    
    fun onShareTapped() {
        _state.value?.let { state ->
            screen.shareText(state.shareText)
        }
    }
    
    fun onHistoryTapped() {
        screen.showAchievementHistory()
    }
    
    fun destroy() {
        scope.cancel()
    }
    
    interface Screen {
        fun dismiss()
        fun shareText(text: String)
        fun showAchievementHistory()
    }
}
