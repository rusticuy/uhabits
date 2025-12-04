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

class InsightsInteractor(
    private val patternEngine: PatternDetectionEngine = PatternDetectionEngine(),
    private val correlationAnalyzer: CorrelationAnalyzer = CorrelationAnalyzer(),
    private val suggestionGenerator: SuggestionGenerator = SuggestionGenerator()
) {

    fun getInsightsForHabit(habit: Habit, daysBack: Int = 90): InsightsSummary {
        val patternInsights = patternEngine.generateInsights(habit, daysBack)
        val weekdayStats = patternEngine.computeWeekdayStats(habit, daysBack)
        val streakTrend = patternEngine.detectStreakTrend(habit)

        return InsightsSummary(
            habitId = habit.id ?: -1,
            habitName = habit.name,
            insights = patternInsights,
            weekdayStats = weekdayStats,
            streakTrend = streakTrend,
            consistencyWindow = patternEngine.findBestConsistencyWindow(habit)
        )
    }

    fun getInsightsForAllHabits(habitList: HabitList, daysBack: Int = 90): AllInsightsResult {
        val allInsights = mutableListOf<Insight>()
        val habitSummaries = mutableListOf<InsightsSummary>()

        val activeHabits = habitList.toList().filter { !it.isArchived }

        activeHabits.forEach { habit ->
            val summary = getInsightsForHabit(habit, daysBack)
            habitSummaries.add(summary)
            allInsights.addAll(summary.insights)
        }

        val correlations = correlationAnalyzer.findCorrelations(habitList, minSampleSize = 30, minCorrelation = 0.3)
        val correlationInsights = correlationAnalyzer.generateCorrelationInsights(correlations)
        allInsights.addAll(correlationInsights)

        val suggestions = suggestionGenerator.generateSuggestions(habitList)

        return AllInsightsResult(
            insights = allInsights,
            habitSummaries = habitSummaries,
            correlations = correlations,
            suggestions = suggestions
        )
    }

    fun getCorrelations(habitList: HabitList, minSampleSize: Int = 30, minCorrelation: Double = 0.3): List<CorrelationResult> {
        return correlationAnalyzer.findCorrelations(habitList, minSampleSize, minCorrelation)
    }

    fun getSuggestions(habitList: HabitList): List<HabitSuggestion> {
        return suggestionGenerator.generateSuggestions(habitList)
    }

    fun getWeekdayStats(habit: Habit, daysBack: Int = 90): List<WeekdayStats> {
        return patternEngine.computeWeekdayStats(habit, daysBack)
    }

    data class InsightsSummary(
        val habitId: Long,
        val habitName: String,
        val insights: List<Insight>,
        val weekdayStats: List<WeekdayStats>,
        val streakTrend: String,
        val consistencyWindow: ConsistencyWindow?
    )

    data class AllInsightsResult(
        val insights: List<Insight>,
        val habitSummaries: List<InsightsSummary>,
        val correlations: List<CorrelationResult>,
        val suggestions: List<HabitSuggestion>
    )
}
