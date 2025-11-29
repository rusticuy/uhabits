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

package org.isoron.uhabits.core.models

class GoalProgressCalculator(private val habitList: HabitList) {
    fun calculateProgress(goal: Goal): Double {
        if (goal.linkedHabits.isEmpty()) return 0.0

        var totalProgress = 0.0
        var totalWeight = 0.0

        for (habitId in goal.linkedHabits) {
            val habit = habitList.getById(habitId)
            if (habit != null) {
                val weight = 1.0
                val today = Timestamp.today()
                val score = habit.scores.get(today).value
                totalProgress += (score / 100.0) * weight
                totalWeight += weight
            }
        }

        return if (totalWeight > 0) (totalProgress / totalWeight) * 100.0 else 0.0
    }

    fun calculateProgressToday(goal: Goal): Int {
        val progress = calculateProgress(goal)
        return progress.toInt()
    }

    fun calculateMilestoneProgress(goal: Goal, milestone: GoalMilestone): Double {
        if (goal.linkedHabits.isEmpty()) return 0.0

        var totalProgress = 0.0
        var totalWeight = 0.0

        for (habitId in goal.linkedHabits) {
            val habit = habitList.getById(habitId)
            if (habit != null) {
                val weight = 1.0
                val today = Timestamp.today()
                val score = habit.scores.get(today).value
                totalProgress += (score / 100.0) * weight
                totalWeight += weight
            }
        }

        val progress = if (totalWeight > 0) (totalProgress / totalWeight) * 100.0 else 0.0
        return minOf(progress, milestone.targetValue)
    }
}
