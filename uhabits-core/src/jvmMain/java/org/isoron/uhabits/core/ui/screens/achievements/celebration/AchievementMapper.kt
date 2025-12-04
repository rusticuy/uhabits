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

import org.isoron.uhabits.core.models.achievements.Achievement

object AchievementMapper {
    private const val AUTO_DISMISS_DELAY_MILLIS = 5000L
    
    private val DEFAULT_CONFETTI_COLORS = listOf(
        0xFFFF6B6B.toInt(),
        0xFF4ECDC4.toInt(),
        0xFF45B7D1.toInt(),
        0xFFFFA07A.toInt(),
        0xFF98D8C8.toInt(),
        0xFFF7DC6F.toInt()
    )
    
    fun toState(
        achievement: Achievement,
        playConfetti: Boolean,
        autoDismiss: Boolean = true
    ): CelebrationState {
        return CelebrationState(
            achievementId = achievement.id,
            title = "Achievement Unlocked!",
            subtitle = achievement.title,
            description = achievement.description,
            iconId = achievement.iconId,
            shareText = buildShareText(achievement),
            showShareButton = true,
            showHistoryButton = true,
            playConfetti = playConfetti,
            confettiColors = DEFAULT_CONFETTI_COLORS,
            autoDismissMillis = if (autoDismiss) AUTO_DISMISS_DELAY_MILLIS else null
        )
    }
    
    private fun buildShareText(achievement: Achievement): String {
        return "I just unlocked the \"${achievement.title}\" achievement in Loop Habit Tracker! 🎉"
    }
}
