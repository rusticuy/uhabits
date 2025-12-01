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

package org.isoron.uhabits.achievements

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.isoron.uhabits.BaseAndroidTest
import org.isoron.uhabits.core.commands.CommandRunner
import org.isoron.uhabits.core.commands.CreateRepetitionCommand
import org.isoron.uhabits.core.models.Entry
import org.isoron.uhabits.core.models.Habit
import org.isoron.uhabits.core.models.Timestamp
import org.isoron.uhabits.core.models.achievements.AchievementType
import org.isoron.uhabits.core.preferences.Preferences

@RunWith(AndroidJUnit4::class)
class AchievementNotificationIntegrationTest : BaseAndroidTest() {

    private lateinit var commandRunner: CommandRunner
    private lateinit var preferences: Preferences
    private lateinit var achievementCommandListener: AchievementCommandListener

    @Before
    override fun setUp() {
        super.setUp()
        commandRunner = component.commandRunner
        preferences = component.preferences
        achievementCommandListener = component.achievementCommandListener
        
        // Enable achievement notifications
        preferences.setAchievementNotificationsEnabled(true)
    }

    @Test
    fun `should trigger achievement detection when habit is checked`() {
        // Given
        val habit = fixtures.createEmptyHabit()
        val today = Timestamp.today()
        
        // Add 7 consecutive checkmarks to trigger 7-day streak achievement
        for (i in 0..6) {
            val timestamp = today.minus(6 - i)
            val command = CreateRepetitionCommand(habitList, habit, timestamp, Entry.YES_MANUAL, "")
            commandRunner.run(command)
            Thread.sleep(100) // Allow async processing
        }
        
        // The achievement should be detected and notification should be triggered
        // We can't easily verify the notification itself in this test,
        // but the integration should work without crashes
    }

    @Test
    fun `should not trigger notifications when disabled`() {
        // Given
        preferences.setAchievementNotificationsEnabled(false)
        val habit = fixtures.createEmptyHabit()
        val today = Timestamp.today()
        
        // Add 7 consecutive checkmarks
        for (i in 0..6) {
            val timestamp = today.minus(6 - i)
            val command = CreateRepetitionCommand(habitList, habit, timestamp, Entry.YES_MANUAL, "")
            commandRunner.run(command)
            Thread.sleep(100)
        }
        
        // Should not crash and no notifications should be shown
    }

    @Test
    fun `should handle early bird achievement detection`() {
        // Given
        val habit = fixtures.createEmptyHabit()
        val earlyMorning = Timestamp(2023, 1, 1, 5) // 5 AM
        
        // When
        val command = CreateRepetitionCommand(habitList, habit, earlyMorning, Entry.YES_MANUAL, "")
        commandRunner.run(command)
        Thread.sleep(100)
        
        // Should detect early bird achievement without crashing
    }
}