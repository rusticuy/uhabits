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

import org.isoron.uhabits.core.models.Habit
import org.isoron.uhabits.core.models.HabitList
import org.isoron.uhabits.core.models.Timestamp
import kotlin.math.max

class InsightsInteractor(
    private val habitList: HabitList
) {

    fun generateInsights(): List<Insight> {
        val insights = mutableListOf<Insight>()
        val today = Timestamp.today()

        habitList.filter { !it.isArchived }.forEach { habit ->
            insights.addAll(analyzeHabit(habit, today))
        }

        return insights.sortedByDescending { it.priority }
    }

    private fun analyzeHabit(habit: Habit, today: Timestamp): List<Insight> {
        val insights = mutableListOf<Insight>()

        insights.addAll(checkStreakDecline(habit, today))
        insights.addAll(checkLowCompletion(habit, today))
        insights.addAll(checkMilestone(habit, today))
        insights.addAll(checkConsistencyImprovement(habit, today))
        insights.addAll(checkReminderSuggestion(habit, today))

        return insights
    }

    private fun checkStreakDecline(habit: Habit, today: Timestamp): List<Insight> {
        val timeline = habit.streaks.getTimeline()
        if (timeline.size < 2) return emptyList()

        val currentStreak = timeline.firstOrNull()?.length ?: 0
        val previousStreak = timeline.getOrNull(1)?.length ?: 0

        if (previousStreak > 7 && currentStreak == 0) {
            return listOf(
                Insight(
                    id = "streak_decline_${habit.id}_${today.unixTime}",
                    type = InsightType.STREAK_DECLINING,
                    title = "Streak Broken",
                    body = "You had a ${previousStreak}-day streak on \"${habit.name}\". Don't give up!",
                    habit = habit,
                    suggestion = null,
                    priority = 90
                )
            )
        }

        return emptyList()
    }

    private fun checkLowCompletion(habit: Habit, today: Timestamp): List<Insight> {
        val last30Days = (0 until 30).map { today.minus(it) }
        val completions = last30Days.count { habit.computedEntries.get(it).value > 0 }
        val completionRate = completions.toDouble() / 30.0

        if (completionRate < 0.3 && habit.reminder != null) {
            return emptyList()
        }

        if (completionRate < 0.3 && habit.reminder == null) {
            return listOf(
                Insight(
                    id = "low_completion_${habit.id}_${today.unixTime}",
                    type = InsightType.LOW_COMPLETION,
                    title = "Low Completion Rate",
                    body = "\"${habit.name}\" has a ${(completionRate * 100).toInt()}% completion rate. Try setting a reminder?",
                    habit = habit,
                    suggestion = InsightSuggestion.AdjustReminder(
                        habitId = habit.id!!,
                        suggestedHour = 9,
                        suggestedMinute = 0
                    ),
                    priority = 70
                )
            )
        }

        return emptyList()
    }

    private fun checkMilestone(habit: Habit, today: Timestamp): List<Insight> {
        val timeline = habit.streaks.getTimeline()
        val currentStreak = timeline.firstOrNull()?.length ?: 0

        val milestones = listOf(7, 30, 100, 365)
        for (milestone in milestones) {
            if (currentStreak == milestone) {
                return listOf(
                    Insight(
                        id = "milestone_${habit.id}_${milestone}_${today.unixTime}",
                        type = InsightType.HABIT_MILESTONE,
                        title = "Milestone Reached!",
                        body = "You've maintained \"${habit.name}\" for $milestone days in a row!",
                        habit = habit,
                        suggestion = null,
                        priority = 100
                    )
                )
            }
        }

        return emptyList()
    }

    private fun checkConsistencyImprovement(habit: Habit, today: Timestamp): List<Insight> {
        val last30Days = (0 until 30).map { today.minus(it) }
        val previous30Days = (30 until 60).map { today.minus(it) }

        val recentCompletions = last30Days.count { habit.computedEntries.get(it).value > 0 }
        val previousCompletions = previous30Days.count { habit.computedEntries.get(it).value > 0 }

        if (recentCompletions > previousCompletions + 5) {
            val improvement = recentCompletions - previousCompletions
            return listOf(
                Insight(
                    id = "consistency_improving_${habit.id}_${today.unixTime}",
                    type = InsightType.CONSISTENCY_IMPROVING,
                    title = "Great Progress!",
                    body = "\"${habit.name}\" improved by $improvement completions this month!",
                    habit = habit,
                    suggestion = null,
                    priority = 80
                )
            )
        }

        return emptyList()
    }

    private fun checkReminderSuggestion(habit: Habit, today: Timestamp): List<Insight> {
        if (habit.reminder != null) return emptyList()

        val last7Days = (0 until 7).map { today.minus(it) }
        val completions = last7Days.count { habit.computedEntries.get(it).value > 0 }

        if (completions < 3) {
            return listOf(
                Insight(
                    id = "reminder_suggestion_${habit.id}_${today.unixTime}",
                    type = InsightType.REMINDER_SUGGESTION,
                    title = "Need a Reminder?",
                    body = "\"${habit.name}\" hasn't been completed much lately. Would a reminder help?",
                    habit = habit,
                    suggestion = InsightSuggestion.AdjustReminder(
                        habitId = habit.id!!,
                        suggestedHour = 9,
                        suggestedMinute = 0
                    ),
                    priority = 60
                )
            )
        }

        return emptyList()
    }

    fun getWeeklySummary(): Insight? {
        val today = Timestamp.today()
        val last7Days = (0 until 7).map { today.minus(it) }
        val activeHabits = habitList.filter { !it.isArchived }

        if (activeHabits.isEmpty()) return null

        val totalCompletions = activeHabits.sumOf { habit ->
            last7Days.count { habit.computedEntries.get(it).value > 0 }
        }

        val maxPossible = activeHabits.size * 7
        val completionRate = (totalCompletions.toDouble() / maxPossible * 100).toInt()

        return Insight(
            id = "weekly_summary_${today.unixTime}",
            type = InsightType.WEEKLY_SUMMARY,
            title = "Weekly Summary",
            body = "You completed $totalCompletions out of $maxPossible possible habits this week ($completionRate%).",
            habit = null,
            suggestion = null,
            priority = 50
        )
    }
}
