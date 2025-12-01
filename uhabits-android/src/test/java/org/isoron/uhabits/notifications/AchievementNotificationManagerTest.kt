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

package org.isoron.uhabits.notifications

import android.app.Notification
import android.content.Context
import androidx.core.app.NotificationManagerCompat
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import org.isoron.uhabits.core.models.Habit
import org.isoron.uhabits.core.models.Timestamp
import org.isoron.uhabits.core.models.achievements.Achievement
import org.isoron.uhabits.core.models.achievements.AchievementType
import org.isoron.uhabits.core.models.achievements.AchievementUnlockedEvent
import org.isoron.uhabits.core.preferences.Preferences
import org.isoron.uhabits.intents.PendingIntentFactory
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class AchievementNotificationManagerTest {

    private lateinit var context: Context
    private lateinit var pendingIntents: PendingIntentFactory
    private lateinit var preferences: Preferences
    private lateinit var achievementDetector: org.isoron.uhabits.core.models.achievements.AchievementDetector
    private lateinit var notificationManager: AchievementNotificationManager

    @Before
    fun setUp() {
        context = mockk(relaxed = true)
        pendingIntents = mockk(relaxed = true)
        preferences = mockk(relaxed = true)
        achievementDetector = mockk(relaxed = true)
        
        every { preferences.areAchievementNotificationsEnabled() } returns true
        
        notificationManager = AchievementNotificationManager(
            context,
            pendingIntents,
            preferences,
            achievementDetector
        )
    }

    @Test
    fun `should not show notification when achievement notifications are disabled`() {
        // Given
        every { preferences.areAchievementNotificationsEnabled() } returns false
        val achievement = createTestAchievement()
        val event = AchievementUnlockedEvent(achievement)
        
        mockkStatic(NotificationManagerCompat::class)
        val notificationManagerCompat = mockk<NotificationManagerCompat>(relaxed = true)
        every { NotificationManagerCompat.from(any()) } returns notificationManagerCompat
        
        // When
        notificationManager.onAchievementUnlocked(event)
        
        // Then
        verify(exactly = 0) { notificationManagerCompat.notify(any(), any<Notification>()) }
    }

    @Test
    fun `should show notification when achievement is unlocked`() {
        // Given
        val achievement = createTestAchievement()
        val habit = createTestHabit()
        val event = AchievementUnlockedEvent(achievement, habit)
        
        mockkStatic(NotificationManagerCompat::class)
        val notificationManagerCompat = mockk<NotificationManagerCompat>(relaxed = true)
        every { NotificationManagerCompat.from(any()) } returns notificationManagerCompat
        
        // When
        notificationManager.onAchievementUnlocked(event)
        
        // Then
        verify { notificationManagerCompat.notify(any(), any<Notification>()) }
    }

    @Test
    fun `should build notification with correct content`() {
        // Given
        val achievement = createTestAchievement()
        val habit = createTestHabit()
        val event = AchievementUnlockedEvent(achievement, habit)
        
        mockkStatic(NotificationManagerCompat::class)
        val notificationManagerCompat = mockk<NotificationManagerCompat>(relaxed = true)
        every { NotificationManagerCompat.from(any()) } returns notificationManagerCompat
        
        // When
        notificationManager.onAchievementUnlocked(event)
        
        // Then
        verify { 
            pendingIntents.showHabit(habit)
            notificationManagerCompat.notify(any(), withArg { notification ->
                assert(notification.contentTitle.toString().contains("Achievement unlocked"))
                assert(notification.contentText.toString().contains("7 Day Streak"))
                assert(notification.contentText.toString().contains("Test Habit"))
            })
        }
    }

    @Test
    fun `should start and stop listening correctly`() {
        // Given
        notificationManager.startListening()
        
        // When
        notificationManager.stopListening()
        
        // Then
        // Should not throw any exceptions
    }

    private fun createTestAchievement(): Achievement {
        return Achievement(
            id = "test_achievement",
            type = AchievementType.STREAK_DAYS_7,
            title = "7 Day Streak",
            description = "Maintained a habit for 7 consecutive days",
            iconResource = "ic_achievement_streak_days_7"
        )
    }

    private fun createTestHabit(): Habit {
        val habit = Habit()
        habit.name = "Test Habit"
        habit.id = 1L
        return habit
    }
}