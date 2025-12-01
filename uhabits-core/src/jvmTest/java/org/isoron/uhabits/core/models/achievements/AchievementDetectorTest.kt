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

package org.isoron.uhabits.core.models.achievements

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import org.isoron.uhabits.core.models.Entry
import org.isoron.uhabits.core.models.Habit
import org.isoron.uhabits.core.models.HabitList
import org.isoron.uhabits.core.models.Timestamp
import kotlin.test.assertEquals

class AchievementDetectorTest {

    private lateinit var detector: AchievementDetector
    private lateinit var listener: AchievementDetector.Listener
    private lateinit var habit: Habit
    private lateinit var habitList: HabitList

    @Before
    fun setUp() {
        detector = AchievementDetector()
        listener = mockk(relaxed = true)
        detector.addListener(listener)
        
        habit = Habit()
        habit.name = "Test Habit"
        habit.id = 1L
        
        habitList = HabitList()
        habitList.add(habit)
    }

    @Test
    fun `should detect 7 day streak achievement`() {
        // Given
        val today = Timestamp.today()
        val sevenDaysAgo = today.minus(6)
        
        // Add 7 consecutive checkmarks
        for (i in 0..6) {
            val timestamp = sevenDaysAgo.plus(i)
            habit.checkmarks.add(Entry(timestamp, Entry.YES_MANUAL))
        }
        
        // When
        detector.onHabitChecked(habit, today, habitList)
        
        // Then
        verify { 
            listener.onAchievementUnlocked(withArg { event ->
                assertEquals(AchievementType.STREAK_DAYS_7, event.achievement.type)
                assertEquals("7 Day Streak", event.achievement.title)
                assertEquals(habit, event.habit)
            })
        }
    }

    @Test
    fun `should detect 100 total checkmarks achievement`() {
        // Given
        val today = Timestamp.today()
        
        // Add 100 checkmarks across multiple habits
        for (i in 0 until 100) {
            val timestamp = today.minus(100 - i)
            habit.checkmarks.add(Entry(timestamp, Entry.YES_MANUAL))
        }
        
        // When
        detector.onHabitChecked(habit, today, habitList)
        
        // Then
        verify { 
            listener.onAchievementUnlocked(withArg { event ->
                assertEquals(AchievementType.TOTAL_CHECKMARKS_100, event.achievement.type)
                assertEquals("100 Checkmarks", event.achievement.title)
            })
        }
    }

    @Test
    fun `should detect early bird achievement`() {
        // Given
        val earlyMorning = Timestamp(2023, 1, 1, 5) // 5 AM
        habit.checkmarks.add(Entry(earlyMorning, Entry.YES_MANUAL))
        
        // When
        detector.onHabitChecked(habit, earlyMorning, habitList)
        
        // Then
        verify { 
            listener.onAchievementUnlocked(withArg { event ->
                assertEquals(AchievementType.EARLY_BIRD, event.achievement.type)
                assertEquals("Early Bird", event.achievement.title)
            })
        }
    }

    @Test
    fun `should detect night owl achievement`() {
        // Given
        val lateNight = Timestamp(2023, 1, 1, 23) // 11 PM
        habit.checkmarks.add(Entry(lateNight, Entry.YES_MANUAL))
        
        // When
        detector.onHabitChecked(habit, lateNight, habitList)
        
        // Then
        verify { 
            listener.onAchievementUnlocked(withArg { event ->
                assertEquals(AchievementType.NIGHT_OWL, event.achievement.type)
                assertEquals("Night Owl", event.achievement.title)
            })
        }
    }

    @Test
    fun `should not unlock same achievement twice`() {
        // Given
        val today = Timestamp.today()
        val sevenDaysAgo = today.minus(6)
        
        // Add 7 consecutive checkmarks
        for (i in 0..6) {
            val timestamp = sevenDaysAgo.plus(i)
            habit.checkmarks.add(Entry(timestamp, Entry.YES_MANUAL))
        }
        
        // Pre-mark the achievement as unlocked
        detector.setUnlockedAchievements(setOf("STREAK_DAYS_7_1"))
        
        // When
        detector.onHabitChecked(habit, today, habitList)
        
        // Then
        verify(exactly = 0) { listener.onAchievementUnlocked(any<AchievementUnlockedEvent>()) }
    }

    @Test
    fun `should add and remove listeners correctly`() {
        // Given
        val listener2 = mockk<AchievementDetector.Listener>(relaxed = true)
        
        // When
        detector.addListener(listener2)
        detector.onHabitChecked(habit, Timestamp.today(), habitList)
        
        // Then
        verify(atLeast = 1) { listener2.onAchievementUnlocked(any<AchievementUnlockedEvent>()) }
        
        // When
        detector.removeListener(listener2)
        detector.onHabitChecked(habit, Timestamp.today(), habitList)
        
        // Then - listener2 should not be called again
        verify(atMost = 1) { listener2.onAchievementUnlocked(any<AchievementUnlockedEvent>()) }
    }
}