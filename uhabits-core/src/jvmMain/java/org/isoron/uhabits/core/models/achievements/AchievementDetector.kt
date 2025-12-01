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

import org.isoron.uhabits.core.models.Entry
import org.isoron.uhabits.core.models.Habit
import org.isoron.uhabits.core.models.HabitList
import org.isoron.uhabits.core.models.Streak
import org.isoron.uhabits.core.models.StreakList
import org.isoron.uhabits.core.models.Timestamp
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Detects achievements and emits events when they are unlocked.
 */
class AchievementDetector {
    
    private val listeners = CopyOnWriteArrayList<Listener>()
    private val unlockedAchievements = mutableSetOf<String>()
    
    interface Listener {
        fun onAchievementUnlocked(event: AchievementUnlockedEvent)
    }
    
    fun addListener(listener: Listener) {
        listeners.add(listener)
    }
    
    fun removeListener(listener: Listener) {
        listeners.remove(listener)
    }
    
    /**
     * Checks for achievements when a habit is checked.
     */
    fun onHabitChecked(habit: Habit, entryTimestamp: Timestamp, allHabits: HabitList) {
        checkStreakAchievements(habit)
        checkTotalCheckmarkAchievements(habit, allHabits)
        checkPerfectPeriodAchievements(habit)
        checkTimeBasedAchievements(habit, entryTimestamp)
    }
    
    /**
     * Manually trigger achievement checking for all habits.
     */
    fun checkAllAchievements(habits: List<Habit>) {
        habits.forEach { habit ->
            checkStreakAchievements(habit)
            checkTotalCheckmarkAchievements(habit, HabitList(habits))
            checkPerfectPeriodAchievements(habit)
        }
    }
    
    private fun checkStreakAchievements(habit: Habit) {
        val streakList = StreakList()
        val entries = habit.checkmarks
        val today = Timestamp.today()
        val oneYearAgo = today.minus(365)
        
        streakList.recompute(
            entries,
            oneYearAgo,
            today,
            habit.isNumerical,
            habit.targetValue,
            habit.targetType
        )
        
        val bestStreaks = streakList.getBest(1)
        
        if (bestStreaks.isNotEmpty()) {
            val bestStreak = bestStreaks[0]
            val length = bestStreak.length()
            
            when {
                length >= 365 && !isUnlocked(AchievementType.STREAK_DAYS_365, habit) -> {
                    unlockAchievement(AchievementType.STREAK_DAYS_365, habit, "365 Day Streak", "Maintained a habit for 365 consecutive days")
                }
                length >= 100 && !isUnlocked(AchievementType.STREAK_DAYS_100, habit) -> {
                    unlockAchievement(AchievementType.STREAK_DAYS_100, habit, "100 Day Streak", "Maintained a habit for 100 consecutive days")
                }
                length >= 50 && !isUnlocked(AchievementType.STREAK_DAYS_50, habit) -> {
                    unlockAchievement(AchievementType.STREAK_DAYS_50, habit, "50 Day Streak", "Maintained a habit for 50 consecutive days")
                }
                length >= 21 && !isUnlocked(AchievementType.STREAK_DAYS_21, habit) -> {
                    unlockAchievement(AchievementType.STREAK_DAYS_21, habit, "21 Day Streak", "Maintained a habit for 21 consecutive days")
                }
                length >= 7 && !isUnlocked(AchievementType.STREAK_DAYS_7, habit) -> {
                    unlockAchievement(AchievementType.STREAK_DAYS_7, habit, "7 Day Streak", "Maintained a habit for 7 consecutive days")
                }
            }
        }
    }
    
    private fun checkTotalCheckmarkAchievements(habit: Habit, allHabits: HabitList) {
        val totalCheckmarks = allHabits.sumOf { it.checkmarks.total() }
        
        when {
            totalCheckmarks >= 1000 && !isUnlocked(AchievementType.TOTAL_CHECKMARKS_1000) -> {
                unlockAchievement(AchievementType.TOTAL_CHECKMARKS_1000, null, "1000 Checkmarks", "Logged 1000 habit checkmarks")
            }
            totalCheckmarks >= 500 && !isUnlocked(AchievementType.TOTAL_CHECKMARKS_500) -> {
                unlockAchievement(AchievementType.TOTAL_CHECKMARKS_500, null, "500 Checkmarks", "Logged 500 habit checkmarks")
            }
            totalCheckmarks >= 100 && !isUnlocked(AchievementType.TOTAL_CHECKMARKS_100) -> {
                unlockAchievement(AchievementType.TOTAL_CHECKMARKS_100, null, "100 Checkmarks", "Logged 100 habit checkmarks")
            }
        }
    }
    
    private fun checkPerfectPeriodAchievements(habit: Habit) {
        val entries = habit.checkmarks
        val today = Timestamp.today()
        
        // Check for perfect week
        val weekStart = today.minus(today.weekday)
        val weekEntries = entries.getByInterval(weekStart, today.plus(6 - today.weekday))
        val isPerfectWeek = weekEntries.all { it.value > 0 } && weekEntries.size == 7
        
        if (isPerfectWeek && !isUnlocked(AchievementType.PERFECT_WEEK, habit)) {
            unlockAchievement(AchievementType.PERFECT_WEEK, habit, "Perfect Week", "Completed a habit every day for a week")
        }
        
        // Check for perfect month (simplified - last 30 days)
        val monthStart = today.minus(30)
        val monthEntries = entries.getByInterval(monthStart, today)
        val isPerfectMonth = monthEntries.all { it.value > 0 } && monthEntries.size >= 28
        
        if (isPerfectMonth && !isUnlocked(AchievementType.PERFECT_MONTH, habit)) {
            unlockAchievement(AchievementType.PERFECT_MONTH, habit, "Perfect Month", "Completed a habit every day for a month")
        }
    }
    
    private fun checkTimeBasedAchievements(habit: Habit, entryTimestamp: Timestamp) {
        val hourOfDay = entryTimestamp.hourOfDay
        
        // Early bird: checked before 6 AM
        if (hourOfDay < 6 && !isUnlocked(AchievementType.EARLY_BIRD, habit)) {
            unlockAchievement(AchievementType.EARLY_BIRD, habit, "Early Bird", "Checked a habit before 6 AM")
        }
        
        // Night owl: checked after 11 PM
        if (hourOfDay >= 23 && !isUnlocked(AchievementType.NIGHT_OWL, habit)) {
            unlockAchievement(AchievementType.NIGHT_OWL, habit, "Night Owl", "Checked a habit after 11 PM")
        }
    }
    
    private fun isUnlocked(type: AchievementType, habit: Habit? = null): Boolean {
        val achievementId = if (habit != null) {
            "${type.name}_${habit.id}"
        } else {
            type.name
        }
        return unlockedAchievements.contains(achievementId)
    }
    
    private fun unlockAchievement(type: AchievementType, habit: Habit?, title: String, description: String) {
        val achievementId = if (habit != null) {
            "${type.name}_${habit.id}"
        } else {
            type.name
        }
        
        if (unlockedAchievements.add(achievementId)) {
            val achievement = Achievement(
                id = achievementId,
                type = type,
                title = title,
                description = description,
                iconResource = "ic_achievement_${type.name.lowercase()}",
                habitId = habit?.id,
                unlockedAt = System.currentTimeMillis()
            )
            
            val event = AchievementUnlockedEvent(achievement, habit)
            listeners.forEach { it.onAchievementUnlocked(event) }
        }
    }
    
    /**
     * For testing purposes - allows pre-populating unlocked achievements.
     */
    internal fun setUnlockedAchievements(achievements: Set<String>) {
        unlockedAchievements.clear()
        unlockedAchievements.addAll(achievements)
    }
}