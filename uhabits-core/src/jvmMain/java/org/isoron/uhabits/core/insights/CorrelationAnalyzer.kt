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
import org.isoron.uhabits.core.models.HabitList
import org.isoron.uhabits.core.models.NumericalHabitType
import org.isoron.uhabits.core.models.Timestamp
import org.isoron.uhabits.core.utils.DateUtils
import kotlin.math.sqrt

class CorrelationAnalyzer {

    fun findCorrelations(
        habitList: HabitList,
        minSampleSize: Int = 30,
        minCorrelation: Double = 0.3
    ): List<CorrelationResult> {
        val habits = habitList.toList().filter { !it.isArchived }
        val results = mutableListOf<CorrelationResult>()

        for (i in habits.indices) {
            for (j in (i + 1) until habits.size) {
                val habit1 = habits[i]
                val habit2 = habits[j]

                val correlation = calculateCorrelation(habit1, habit2)
                if (correlation != null && 
                    correlation.sampleSize >= minSampleSize && 
                    kotlin.math.abs(correlation.coefficient) >= minCorrelation) {
                    results.add(correlation)
                }
            }
        }

        return results.sortedByDescending { kotlin.math.abs(it.coefficient) }
    }

    fun calculateCorrelation(habit1: Habit, habit2: Habit, daysBack: Int = 90): CorrelationResult? {
        val today = DateUtils.getTodayWithOffset()
        val pairs = mutableListOf<Pair<Double, Double>>()

        for (i in 0 until daysBack) {
            val timestamp = today.minus(i)
            val value1 = getNumericValue(habit1, timestamp)
            val value2 = getNumericValue(habit2, timestamp)

            if (value1 != null && value2 != null) {
                pairs.add(Pair(value1, value2))
            }
        }

        if (pairs.size < 10) return null

        val coefficient = computePearsonCorrelation(pairs)
        if (coefficient.isNaN() || coefficient.isInfinite()) return null

        return CorrelationResult(
            habit1Id = habit1.id ?: -1,
            habit2Id = habit2.id ?: -1,
            habit1Name = habit1.name,
            habit2Name = habit2.name,
            coefficient = coefficient,
            sampleSize = pairs.size,
            strength = CorrelationResult.determineStrength(coefficient)
        )
    }

    private fun getNumericValue(habit: Habit, timestamp: Timestamp): Double? {
        val entry = habit.computedEntries.get(timestamp)

        if (entry.value == Entry.UNKNOWN) return null

        return if (habit.isNumerical) {
            entry.value / 1000.0
        } else {
            when (entry.value) {
                Entry.YES_MANUAL, Entry.YES_AUTO -> 1.0
                Entry.NO, Entry.SKIP -> 0.0
                else -> null
            }
        }
    }

    private fun computePearsonCorrelation(pairs: List<Pair<Double, Double>>): Double {
        if (pairs.isEmpty()) return 0.0

        val n = pairs.size.toDouble()
        val sumX = pairs.sumOf { it.first }
        val sumY = pairs.sumOf { it.second }
        val sumXY = pairs.sumOf { it.first * it.second }
        val sumX2 = pairs.sumOf { it.first * it.first }
        val sumY2 = pairs.sumOf { it.second * it.second }

        val numerator = n * sumXY - sumX * sumY
        val denomX = sqrt(n * sumX2 - sumX * sumX)
        val denomY = sqrt(n * sumY2 - sumY * sumY)
        val denominator = denomX * denomY

        if (denominator == 0.0) return 0.0

        return numerator / denominator
    }

    fun generateCorrelationInsights(correlations: List<CorrelationResult>): List<Insight> {
        return correlations.map { correlation ->
            val direction = if (correlation.coefficient > 0) "positively" else "negatively"
            val strengthDesc = when (correlation.strength) {
                CorrelationResult.CorrelationStrength.STRONG -> "strongly"
                CorrelationResult.CorrelationStrength.MODERATE -> "moderately"
                CorrelationResult.CorrelationStrength.WEAK -> "weakly"
                else -> ""
            }

            Insight(
                type = InsightType.HABIT_CORRELATION,
                title = "Habit Connection",
                message = "${correlation.habit1Name} and ${correlation.habit2Name} are $strengthDesc $direction correlated",
                habitId = correlation.habit1Id,
                metadata = mapOf(
                    "habit1Id" to correlation.habit1Id,
                    "habit2Id" to correlation.habit2Id,
                    "coefficient" to correlation.coefficient,
                    "strength" to correlation.strength.name
                )
            )
        }
    }
}
