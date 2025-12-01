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

import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import org.isoron.uhabits.R
import org.isoron.uhabits.core.AppScope
import org.isoron.uhabits.core.models.Habit
import org.isoron.uhabits.core.models.Timestamp
import org.isoron.uhabits.core.models.achievements.Achievement
import org.isoron.uhabits.core.models.achievements.AchievementDetector
import org.isoron.uhabits.core.models.achievements.AchievementUnlockedEvent
import org.isoron.uhabits.core.preferences.Preferences
import org.isoron.uhabits.inject.AppContext
import org.isoron.uhabits.intents.PendingIntentFactory
import javax.inject.Inject

/**
 * Manages notifications for achievement unlocks.
 */
@AppScope
class AchievementNotificationManager
@Inject constructor(
    @AppContext private val context: Context,
    private val pendingIntents: PendingIntentFactory,
    private val preferences: Preferences,
    private val achievementDetector: AchievementDetector
) : AchievementDetector.Listener {
    
    companion object {
        private const val ACHIEVEMENTS_CHANNEL_ID = "ACHIEVEMENTS"
        private const val ACHIEVEMENT_NOTIFICATION_BASE_ID = 20000
        
        fun createAchievementNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationManager = context.getSystemService(Activity.NOTIFICATION_SERVICE) as NotificationManager
                val channel = NotificationChannel(
                    ACHIEVEMENTS_CHANNEL_ID,
                    context.getString(R.string.achievements),
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = context.getString(R.string.achievement_notifications_description)
                    enableLights(true)
                    enableVibration(true)
                }
                notificationManager.createNotificationChannel(channel)
            }
        }
    }
    
    private var isListening = false
    
    fun startListening() {
        if (!isListening) {
            achievementDetector.addListener(this)
            isListening = true
        }
    }
    
    fun stopListening() {
        if (isListening) {
            achievementDetector.removeListener(this)
            isListening = false
        }
    }
    
    override fun onAchievementUnlocked(event: AchievementUnlockedEvent) {
        if (!preferences.areAchievementNotificationsEnabled()) {
            return
        }
        
        // Don't show notification if app is in foreground and confetti is shown
        if (isAppInForeground()) {
            return
        }
        
        showAchievementNotification(event.achievement, event.habit)
    }
    
    private fun showAchievementNotification(achievement: Achievement, habit: Habit?) {
        val notificationId = ACHIEVEMENT_NOTIFICATION_BASE_ID + achievement.type.ordinal
        
        val notification = buildAchievementNotification(achievement, habit)
        
        try {
            val notificationManager = NotificationManagerCompat.from(context)
            createAchievementNotificationChannel(context)
            notificationManager.notify(notificationId, notification)
        } catch (e: SecurityException) {
            // Handle cases where notification permission is not granted
            // This can happen on Android 13+ without POST_NOTIFICATIONS permission
        }
    }
    
    private fun buildAchievementNotification(achievement: Achievement, habit: Habit?): Notification {
        val contentText = if (habit != null) {
            context.getString(R.string.achievement_unlocked_for_habit, achievement.title, habit.name)
        } else {
            achievement.title
        }
        
        val bigText = if (habit != null) {
            "${achievement.description}\n\n${context.getString(R.string.habit)}: ${habit.name}"
        } else {
            achievement.description
        }
        
        val contentIntent = if (habit != null) {
            pendingIntents.showHabit(habit)
        } else {
            pendingIntents.showListHabits()
        }
        
        return NotificationCompat.Builder(context, ACHIEVEMENTS_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.achievement_unlocked))
            .setContentText(contentText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
            .setContentIntent(contentIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setColor(ContextCompat.getColor(context, R.color.primary))
            .setCategory(NotificationCompat.CATEGORY_SOCIAL)
            .setGroup("achievements")
            .build()
    }
    
    private fun isAppInForeground(): Boolean {
        // This is a simplified check. In a production app, you might want to use
        // ActivityLifecycleCallbacks or similar for more accurate foreground detection
        // For now, we'll assume we want to show notifications when app is backgrounded
        return false
    }
}