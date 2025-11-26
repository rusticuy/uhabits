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
package org.isoron.uhabits.core.analytics

import org.isoron.uhabits.core.models.Habit

class AnalyticsInteractor(
    private val heatmapGenerator: HeatmapGenerator = HeatmapGenerator(),
    private val trendCalculator: TrendCalculator = TrendCalculator(),
    private val chainTimelineBuilder: ChainTimelineBuilder = ChainTimelineBuilder()
) {

    fun buildDashboardState(
        habit: Habit,
        heatmapDaysBack: Int = 365,
        trendWindowSize: Int = 7,
        trendDaysBack: Int = 365,
        chainMaxDaysBack: Int = 3650
    ): AnalyticsDashboardState {
        val heatmapData = heatmapGenerator.generate(
            habit = habit,
            fromDaysAgo = heatmapDaysBack,
            toDaysAgo = 0
        )

        val trendData = trendCalculator.calculate(
            habit = habit,
            windowSize = trendWindowSize,
            fromDaysAgo = trendDaysBack,
            toDaysAgo = 0
        )

        val chainTimeline = chainTimelineBuilder.build(
            habit = habit,
            maxDaysBack = chainMaxDaysBack
        )

        val currentStreak = habit.streaks.getTimeline().firstOrNull()?.length ?: 0
        val longestStreak = habit.streaks.getTimeline().maxOfOrNull { it.length } ?: 0

        return AnalyticsDashboardState(
            heatmapData = heatmapData,
            trendData = trendData,
            chainTimeline = chainTimeline,
            currentStreakLength = currentStreak,
            longestStreakLength = longestStreak
        )
    }

    fun getHeatmapPage(
        habit: Habit,
        page: Int = 0,
        pageSize: Int = 365
    ): List<HeatmapCell> {
        val allData = heatmapGenerator.generate(
            habit = habit,
            fromDaysAgo = (page + 1) * pageSize,
            toDaysAgo = page * pageSize
        )
        return allData
    }
}
