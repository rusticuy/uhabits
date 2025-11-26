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

package org.isoron.uhabits.core.ui.screens.habits.show.views

import org.isoron.platform.time.DayOfWeek
import org.isoron.platform.time.LocalDate
import org.isoron.uhabits.core.models.Entry
import org.isoron.uhabits.core.models.Entry.Companion.SKIP
import org.isoron.uhabits.core.models.Entry.Companion.YES_AUTO
import org.isoron.uhabits.core.models.Entry.Companion.YES_MANUAL
import org.isoron.uhabits.core.models.Habit
import org.isoron.uhabits.core.models.NumericalHabitType.AT_LEAST
import org.isoron.uhabits.core.models.NumericalHabitType.AT_MOST
import org.isoron.uhabits.core.models.PaletteColor
import org.isoron.uhabits.core.models.Streak
import org.isoron.uhabits.core.ui.views.HistoryChart
import org.isoron.uhabits.core.ui.views.HistoryChart.Square.DIMMED
import org.isoron.uhabits.core.ui.views.HistoryChart.Square.GREY
import org.isoron.uhabits.core.ui.views.HistoryChart.Square.HATCHED
import org.isoron.uhabits.core.ui.views.HistoryChart.Square.OFF
import org.isoron.uhabits.core.ui.views.HistoryChart.Square.ON
import org.isoron.uhabits.core.ui.views.Theme
import org.isoron.uhabits.core.utils.DateUtils
import kotlin.math.roundToInt

data class TrendDataPoint(
    val date: LocalDate,
    val value: Double
)

data class ChainTimelineItem(
    val startDate: LocalDate,
    val endDate: LocalDate,
    val length: Int
)

data class AnalyticsCardState(
    val color: PaletteColor,
    val firstWeekday: DayOfWeek,
    val heatmapSeries: List<HistoryChart.Square>,
    val heatmapDefaultSquare: HistoryChart.Square,
    val heatmapNotesIndicators: List<Boolean>,
    val trendData: List<TrendDataPoint>,
    val chainTimeline: List<ChainTimelineItem>,
    val theme: Theme,
    val today: LocalDate
)

class AnalyticsCardPresenter {
    companion object {
        fun buildState(
            habit: Habit,
            firstWeekday: DayOfWeek,
            theme: Theme
        ): AnalyticsCardState {
            val today = DateUtils.getTodayWithOffset()
            val oldest = habit.computedEntries.getKnown().lastOrNull()?.timestamp ?: today
            val entries = habit.computedEntries.getByInterval(oldest, today)

            val heatmapSeries = if (habit.isNumerical) {
                entries.map {
                    when {
                        it.value == Entry.UNKNOWN -> OFF
                        it.value == SKIP -> HATCHED
                        (habit.targetType == AT_MOST) && (it.value / 1000.0 <= habit.targetValue) -> ON
                        (habit.targetType == AT_LEAST) && (it.value / 1000.0 >= habit.targetValue) -> ON
                        else -> GREY
                    }
                }
            } else {
                entries.map {
                    when (it.value) {
                        YES_MANUAL -> ON
                        YES_AUTO -> DIMMED
                        SKIP -> HATCHED
                        else -> OFF
                    }
                }
            }

            val heatmapNotesIndicators = entries.map {
                when (it.notes) {
                    "" -> false
                    else -> true
                }
            }

            val trendData = buildTrendData(entries, habit)
            val chainTimeline = buildChainTimeline(habit, today)

            return AnalyticsCardState(
                color = habit.color,
                firstWeekday = firstWeekday,
                today = today.toLocalDate(),
                theme = theme,
                heatmapSeries = heatmapSeries,
                heatmapDefaultSquare = OFF,
                heatmapNotesIndicators = heatmapNotesIndicators,
                trendData = trendData,
                chainTimeline = chainTimeline
            )
        }

        private fun buildTrendData(
            entries: List<Entry>,
            habit: Habit
        ): List<TrendDataPoint> {
            if (entries.isEmpty()) return emptyList()

            val windowSize = 7
            val trendPoints = mutableListOf<TrendDataPoint>()

            for (i in (windowSize - 1) until entries.size) {
                val window = entries.subList(maxOf(0, i - windowSize + 1), i + 1)
                val average = if (habit.isNumerical) {
                    window.filter { it.value > 0 }.map { it.value / 1000.0 }.average()
                } else {
                    window.count { it.value == YES_MANUAL || it.value == YES_AUTO }.toDouble() / windowSize
                }

                trendPoints.add(
                    TrendDataPoint(
                        date = entries[i].timestamp.toLocalDate(),
                        value = average
                    )
                )
            }

            return trendPoints
        }

        private fun buildChainTimeline(
            habit: Habit,
            today: org.isoron.uhabits.core.models.Timestamp
        ): List<ChainTimelineItem> {
            val streaks = habit.streaks.getBest(20)
            return streaks
                .sortedWith { s1, s2 -> s2.start.compareTo(s1.start) }
                .map { streak ->
                    ChainTimelineItem(
                        startDate = streak.start.toLocalDate(),
                        endDate = streak.end.toLocalDate(),
                        length = streak.length
                    )
                }
        }
    }
}
