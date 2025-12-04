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
package org.isoron.uhabits.core.insights

import org.isoron.uhabits.core.models.Entry
import org.isoron.uhabits.core.models.Habit
import org.isoron.uhabits.core.models.NumericalHabitType
import org.isoron.uhabits.core.models.Timestamp
import org.isoron.uhabits.core.utils.DateUtils
import kotlin.math.max

class PatternDetectionEngine {

    fun computeWeekdayStats(habit: Habit, daysBack: Int = 90): List<WeekdayStats> {
        val today = DateUtils.getTodayWithOffset()
        val weekdayData = mutableMapOf<Int, MutableList<Boolean>>()

        for (i in 0 until daysBack) {
            val timestamp = today.minus(i)
            val weekday = timestamp.weekday
            val isSuccess = isEntrySuccessful(habit, timestamp)

            if (isSuccess != null) {
                weekdayData.getOrPut(weekday) { mutableListOf() }.add(isSuccess)
            }
        }

        return (0..6).map { weekday ->
            val entries = weekdayData[weekday] ?: emptyList()
            val successful = entries.count { it }
            val total = entries.size
            val rate = if (total > 0) successful.toDouble() / total else 0.0

            WeekdayStats(
                weekday = weekday,
                successRate = rate,
                totalDays = total,
                successfulDays = successful
            )
        }
    }

    fun detectStreakTrend(habit: Habit, windowSize: Int = 30): String {
        val timeline = habit.streaks.getTimeline()
        if (timeline.isEmpty()) return "NO_DATA"

        val recentStreaks = timeline.take(3)
        if (recentStreaks.size < 2) return "INSUFFICIENT_DATA"

        val avgRecent = recentStreaks.take(2).map { it.length }.average()
        val oldest = recentStreaks.last().length

        return when {
            avgRecent > oldest * 1.2 -> "IMPROVING"
            avgRecent < oldest * 0.8 -> "DECLINING"
            else -> "STABLE"
        }
    }

    fun findBestConsistencyWindow(habit: Habit, daysBack: Int = 60): ConsistencyWindow? {
        val reminder = habit.reminder ?: return null

        val today = DateUtils.getTodayWithOffset()
        val hour = reminder.hour
        val windowData = mutableMapOf<String, MutableList<Boolean>>()

        for (i in 0 until daysBack) {
            val timestamp = today.minus(i)
            val isSuccess = isEntrySuccessful(habit, timestamp)

            if (isSuccess != null) {
                val timeWindow = getTimeWindow(hour)
                windowData.getOrPut(timeWindow) { mutableListOf() }.add(isSuccess)
            }
        }

        val bestWindow = windowData.maxByOrNull { (_, successes) ->
            successes.count { it }.toDouble() / max(successes.size, 1)
        } ?: return null

        val entries = bestWindow.value
        val successful = entries.count { it }
        val rate = successful.toDouble() / max(entries.size, 1)

        val (start, end) = parseTimeWindow(bestWindow.key)
        return ConsistencyWindow(
            startHour = start,
            endHour = end,
            successRate = rate,
            occurrences = entries.size
        )
    }

    fun generateInsights(habit: Habit, daysBack: Int = 90): List<Insight> {
        val insights = mutableListOf<Insight>()

        val weekdayStats = computeWeekdayStats(habit, daysBack)
        val statsWithData = weekdayStats.filter { it.totalDays >= 3 }

        if (statsWithData.isNotEmpty()) {
            val bestDay = statsWithData.maxByOrNull { it.successRate }
            val worstDay = statsWithData.minByOrNull { it.successRate }

            if (bestDay != null && bestDay.successRate >= 0.7) {
                insights.add(
                    Insight(
                        type = InsightType.BEST_DAY,
                        title = "Best Day",
                        message = "Most consistent on ${bestDay.weekdayName}",
                        habitId = habit.id,
                        metadata = mapOf(
                            "weekday" to bestDay.weekday,
                            "successRate" to bestDay.successRate
                        )
                    )
                )
            }

            if (worstDay != null && worstDay.successRate < 0.4 && worstDay.totalDays >= 5) {
                insights.add(
                    Insight(
                        type = InsightType.WORST_DAY,
                        title = "Challenge Day",
                        message = "Struggles on ${worstDay.weekdayName}",
                        habitId = habit.id,
                        metadata = mapOf(
                            "weekday" to worstDay.weekday,
                            "successRate" to worstDay.successRate
                        )
                    )
                )
            }
        }

        val streakTrend = detectStreakTrend(habit)
        if (streakTrend != "NO_DATA" && streakTrend != "INSUFFICIENT_DATA") {
            insights.add(
                Insight(
                    type = InsightType.STREAK_TREND,
                    title = "Streak Trend",
                    message = "Your streaks are $streakTrend",
                    habitId = habit.id,
                    metadata = mapOf("trend" to streakTrend)
                )
            )
        }

        val consistencyWindow = findBestConsistencyWindow(habit)
        if (consistencyWindow != null && consistencyWindow.occurrences >= 5) {
            insights.add(
                Insight(
                    type = InsightType.CONSISTENCY_WINDOW,
                    title = "Best Time",
                    message = "Best streak window is ${consistencyWindow.label}",
                    habitId = habit.id,
                    metadata = mapOf(
                        "startHour" to consistencyWindow.startHour,
                        "endHour" to consistencyWindow.endHour,
                        "successRate" to consistencyWindow.successRate
                    )
                )
            )
        }

        if (habit.hasReminder()) {
            val reminder = habit.reminder!!
            val hour = reminder.hour
            val timeLabel = when {
                hour < 6 -> "early morning"
                hour < 12 -> "morning"
                hour < 17 -> "afternoon"
                hour < 21 -> "evening"
                else -> "night"
            }

            insights.add(
                Insight(
                    type = InsightType.CHECK_IN_TIMING,
                    title = "Check-in Time",
                    message = "Reminder set for $timeLabel",
                    habitId = habit.id,
                    metadata = mapOf(
                        "hour" to hour,
                        "minute" to reminder.minute
                    )
                )
            )
        }

        return insights
    }

    private fun isEntrySuccessful(habit: Habit, timestamp: Timestamp): Boolean? {
        val entry = habit.computedEntries.get(timestamp)

        if (entry.value == Entry.UNKNOWN) return null

        return if (habit.isNumerical) {
            val value = entry.value / 1000.0
            when (habit.targetType) {
                NumericalHabitType.AT_LEAST -> value >= habit.targetValue
                NumericalHabitType.AT_MOST -> value <= habit.targetValue
            }
        } else {
            entry.value == Entry.YES_MANUAL || entry.value == Entry.YES_AUTO
        }
    }

    private fun getTimeWindow(hour: Int): String {
        return when {
            hour < 6 -> "0-6"
            hour < 12 -> "6-12"
            hour < 17 -> "12-17"
            hour < 21 -> "17-21"
            else -> "21-24"
        }
    }

    private fun parseTimeWindow(window: String): Pair<Int, Int> {
        val parts = window.split("-")
        return Pair(parts[0].toInt(), parts[1].toInt())
    }
}
