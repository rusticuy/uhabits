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
import org.isoron.uhabits.core.models.NumericalHabitType
import org.isoron.uhabits.core.models.Timestamp
import org.isoron.uhabits.core.utils.DateUtils

class ChainTimelineBuilder {

    fun build(
        habit: Habit,
        maxDaysBack: Int = 3650
    ): List<ChainSegment> {
        val result = mutableListOf<ChainSegment>()
        val today = DateUtils.getTodayWithOffset()
        val from = today.minus(maxDaysBack)
        val to = today

        val entries = habit.computedEntries.getByInterval(from, to)
        val completedDates = mutableListOf<Timestamp>()

        for (entry in entries.reversed()) {
            val isCompleted = when {
                habit.isNumerical -> {
                    val value = entry.value / 1000.0
                    when (habit.targetType) {
                        NumericalHabitType.AT_LEAST -> value >= habit.targetValue
                        NumericalHabitType.AT_MOST -> value != Entry.UNKNOWN.toDouble() && value <= habit.targetValue
                    }
                }
                else -> entry.value > 0 && entry.value != Entry.SKIP
            }

            if (isCompleted) {
                completedDates.add(entry.timestamp)
            }
        }

        if (completedDates.isEmpty()) return result

        completedDates.sort()

        var segmentStart = completedDates[0]
        var segmentEnd = completedDates[0]

        for (i in 1 until completedDates.size) {
            val current = completedDates[i]
            if (current == segmentEnd.plus(1)) {
                segmentEnd = current
            } else {
                result.add(ChainSegment(segmentStart, segmentEnd, segmentStart.daysUntil(segmentEnd) + 1))
                segmentStart = current
                segmentEnd = current
            }
        }
        result.add(ChainSegment(segmentStart, segmentEnd, segmentStart.daysUntil(segmentEnd) + 1))

        return result.sortedByDescending { it.end }
    }
}
