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
package org.isoron.uhabits.core.models.goals

import org.isoron.uhabits.core.models.HabitList
import org.isoron.uhabits.core.utils.DateUtils

class GoalProgressCalculator(
    private val goalList: GoalList,
    private val habitList: HabitList
) {

    fun calculate(goalId: Long): GoalProgress? {
        val goal = goalList.getById(goalId) ?: return null

        val linkedHabits = goalList.getLinkedHabits(goalId)
        val habits = linkedHabits.mapNotNull { link ->
            habitList.getById(link.habitId)?.let { it to link }
        }

        val habitSnapshots = habits.map { it.first }

        var totalWeightedScore = 0.0
        var totalWeight = 0.0

        for ((habit, link) in habits) {
            val score = getHabitScore(habit)
            totalWeightedScore += score * link.weight
            totalWeight += link.weight
        }

        val percentageComplete = if (totalWeight > 0) {
            (totalWeightedScore / totalWeight).coerceIn(0.0, 1.0) * 100.0
        } else {
            0.0
        }

        val milestones = goalList.getMilestones(goalId)

        val now = System.currentTimeMillis()
        val deadlineOverdue = goal.deadline?.let { it < now } ?: false

        return GoalProgress(
            goalId = goalId,
            percentageComplete = percentageComplete,
            linkedHabits = habitSnapshots,
            milestoneStatus = milestones,
            deadlineOverdue = deadlineOverdue,
            deadlineMillis = goal.deadline
        )
    }

    private fun getHabitScore(habit: org.isoron.uhabits.core.models.Habit): Double {
        val today = DateUtils.getTodayWithOffset()
        val score = habit.scores[today]
        return score.value
    }
}
