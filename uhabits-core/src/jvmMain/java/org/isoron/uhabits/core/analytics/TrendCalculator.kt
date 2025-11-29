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

import org.isoron.uhabits.core.models.Entry
import org.isoron.uhabits.core.models.Habit
import org.isoron.uhabits.core.utils.DateUtils
import kotlin.math.min

class TrendCalculator {

    fun calculate(
        habit: Habit,
        windowSize: Int = 7,
        fromDaysAgo: Int = 365,
        toDaysAgo: Int = 0
    ): List<TrendPoint> {
        val result = mutableListOf<TrendPoint>()
        val today = DateUtils.getTodayWithOffset()
        val fromDate = today.minus(fromDaysAgo)
        val toDate = today.minus(toDaysAgo)

        val values = mutableListOf<Double>()
        var current = fromDate
        while (!current.isNewerThan(toDate)) {
            val entry = habit.computedEntries.get(current)
            val value = normalizeValue(habit, entry.value)
            values.add(value)
            current = current.plus(1)
        }

        if (values.isEmpty()) return result

        var current2 = fromDate
        for (i in values.indices) {
            val movingAverage = calculateMovingAverage(values, i, windowSize)
            val regressionValue = calculateRegressionSlope(values, i, windowSize)
            result.add(TrendPoint(current2, movingAverage, regressionValue))
            current2 = current2.plus(1)
        }

        return result
    }

    private fun calculateMovingAverage(values: List<Double>, index: Int, windowSize: Int): Double {
        val start = maxOf(0, index - windowSize + 1)
        val window = values.subList(start, index + 1)
        return if (window.isEmpty()) 0.0 else window.average()
    }

    private fun calculateRegressionSlope(values: List<Double>, index: Int, windowSize: Int): Double {
        val start = maxOf(0, index - windowSize + 1)
        val window = values.subList(start, index + 1)

        if (window.size < 2) return 0.0

        var sumX = 0.0
        var sumY = 0.0
        var sumXY = 0.0
        var sumX2 = 0.0

        for (i in window.indices) {
            val x = i.toDouble()
            val y = window[i]
            sumX += x
            sumY += y
            sumXY += x * y
            sumX2 += x * x
        }

        val n = window.size.toDouble()
        val denominator = n * sumX2 - sumX * sumX
        return if (denominator != 0.0) {
            (n * sumXY - sumX * sumY) / denominator
        } else {
            0.0
        }
    }

    private fun normalizeValue(habit: Habit, entryValue: Int): Double {
        return when {
            entryValue == Entry.UNKNOWN -> 0.0
            habit.isNumerical -> {
                val normalized = entryValue / 1000.0
                val max = habit.targetValue
                if (max > 0) min(1.0, normalized / max) else if (normalized > 0) 1.0 else 0.0
            }
            else -> {
                when (entryValue) {
                    Entry.YES_MANUAL, Entry.YES_AUTO -> 1.0
                    Entry.SKIP -> 0.5
                    Entry.NO -> 0.0
                    else -> 0.0
                }
            }
        }
    }
}
