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

data class CorrelationResult(
    val habit1Id: Long,
    val habit2Id: Long,
    val habit1Name: String,
    val habit2Name: String,
    val coefficient: Double,
    val sampleSize: Int,
    val strength: CorrelationStrength
) {
    enum class CorrelationStrength {
        NONE,
        WEAK,
        MODERATE,
        STRONG
    }

    companion object {
        fun determineStrength(coefficient: Double): CorrelationStrength {
            val absCoeff = kotlin.math.abs(coefficient)
            return when {
                absCoeff < 0.3 -> CorrelationStrength.NONE
                absCoeff < 0.5 -> CorrelationStrength.WEAK
                absCoeff < 0.7 -> CorrelationStrength.MODERATE
                else -> CorrelationStrength.STRONG
            }
        }
    }
}
