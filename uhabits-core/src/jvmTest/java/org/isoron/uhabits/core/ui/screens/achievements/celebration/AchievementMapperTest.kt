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

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.isoron.uhabits.core.BaseUnitTest
import org.isoron.uhabits.core.models.achievements.Achievement
import org.isoron.uhabits.core.models.achievements.AchievementType
import org.junit.Test

class AchievementMapperTest : BaseUnitTest() {
    
    @Test
    fun `toState maps achievement correctly with confetti enabled`() {
        val achievement = createTestAchievement()
        
        val state = AchievementMapper.toState(
            achievement = achievement,
            playConfetti = true
        )
        
        assertThat(state.achievementId, equalTo("test_achievement"))
        assertThat(state.title, equalTo("Achievement Unlocked!"))
        assertThat(state.subtitle, equalTo("Test Title"))
        assertThat(state.description, equalTo("Test Description"))
        assertThat(state.iconId, equalTo(123))
        assertThat(state.playConfetti, equalTo(true))
        assertThat(state.showShareButton, equalTo(true))
        assertThat(state.showHistoryButton, equalTo(true))
    }
    
    @Test
    fun `toState maps achievement correctly with confetti disabled`() {
        val achievement = createTestAchievement()
        
        val state = AchievementMapper.toState(
            achievement = achievement,
            playConfetti = false
        )
        
        assertThat(state.playConfetti, equalTo(false))
    }
    
    @Test
    fun `toState includes auto dismiss delay by default`() {
        val achievement = createTestAchievement()
        
        val state = AchievementMapper.toState(
            achievement = achievement,
            playConfetti = true
        )
        
        assertThat(state.autoDismissMillis, equalTo(5000L))
    }
    
    @Test
    fun `toState can disable auto dismiss`() {
        val achievement = createTestAchievement()
        
        val state = AchievementMapper.toState(
            achievement = achievement,
            playConfetti = true,
            autoDismiss = false
        )
        
        assertThat(state.autoDismissMillis, equalTo(null))
    }
    
    @Test
    fun `toState generates correct share text`() {
        val achievement = Achievement(
            id = "test",
            type = AchievementType.STREAK_30,
            title = "30 Day Streak",
            description = "Maintained a 30 day streak",
            iconId = 1,
            unlockedAt = System.currentTimeMillis()
        )
        
        val state = AchievementMapper.toState(
            achievement = achievement,
            playConfetti = true
        )
        
        assertThat(
            state.shareText,
            equalTo("I just unlocked the \"30 Day Streak\" achievement in Loop Habit Tracker! 🎉")
        )
    }
    
    @Test
    fun `toState includes confetti colors`() {
        val achievement = createTestAchievement()
        
        val state = AchievementMapper.toState(
            achievement = achievement,
            playConfetti = true
        )
        
        assertThat(state.confettiColors.isNotEmpty(), equalTo(true))
        assertThat(state.confettiColors.size, equalTo(6))
    }
    
    private fun createTestAchievement(): Achievement {
        return Achievement(
            id = "test_achievement",
            type = AchievementType.FIRST_HABIT,
            title = "Test Title",
            description = "Test Description",
            iconId = 123,
            unlockedAt = System.currentTimeMillis()
        )
    }
}
