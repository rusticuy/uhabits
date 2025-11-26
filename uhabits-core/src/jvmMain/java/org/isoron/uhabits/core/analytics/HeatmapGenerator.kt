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

class HeatmapGenerator {

    fun generate(
        habit: Habit,
        fromDaysAgo: Int = 365,
        toDaysAgo: Int = 0
    ): List<HeatmapCell> {
        val result = mutableListOf<HeatmapCell>()
        val today = DateUtils.getTodayWithOffset()
        val fromDate = today.minus(fromDaysAgo)
        val toDate = today.minus(toDaysAgo)

        var current = fromDate
        while (!current.isNewerThan(toDate)) {
            val entry = habit.computedEntries.get(current)
            val value = when {
                entry.value == Entry.UNKNOWN -> 0.0
                habit.isNumerical -> {
                    val normalized = entry.value / 1000.0
                    val max = habit.targetValue
                    if (max > 0) min(1.0, normalized / max) else if (normalized > 0) 1.0 else 0.0
                }
                else -> {
                    when (entry.value) {
                        Entry.YES_MANUAL, Entry.YES_AUTO -> 1.0
                        Entry.SKIP -> 0.5
                        Entry.NO -> 0.0
                        else -> 0.0
                    }
                }
            }
            result.add(HeatmapCell(current, value))
            current = current.plus(1)
        }

        return result
    }
}
