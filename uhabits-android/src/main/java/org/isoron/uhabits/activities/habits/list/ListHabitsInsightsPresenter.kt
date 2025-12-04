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
package org.isoron.uhabits.activities.habits.list

import org.isoron.uhabits.activities.habits.list.views.InsightsCardCarousel
import org.isoron.uhabits.core.insights.Insight
import org.isoron.uhabits.core.insights.InsightSuggestion
import org.isoron.uhabits.core.insights.InsightsInteractor
import org.isoron.uhabits.core.models.HabitList
import org.isoron.uhabits.core.models.Reminder
import org.isoron.uhabits.core.tasks.LoadInsightsTask
import org.isoron.uhabits.core.tasks.TaskRunner
import org.isoron.uhabits.core.commands.CommandRunner
import org.isoron.uhabits.core.commands.EditHabitCommand
import org.isoron.uhabits.inject.ActivityScope
import javax.inject.Inject

@ActivityScope
class ListHabitsInsightsPresenter @Inject constructor(
    private val taskRunner: TaskRunner,
    private val habitList: HabitList,
    private val commandRunner: CommandRunner,
    private val carousel: InsightsCardCarousel,
    private val screen: Screen
) {

    private var currentInsights: List<Insight> = emptyList()
    private var loadTask: LoadInsightsTask? = null

    interface Screen {
        fun showInsightsDetail()
        fun showHabitForInsight(habitId: Long)
    }

    fun onAttach() {
        loadInsights()
    }

    fun onDetach() {
        loadTask?.cancel()
    }

    fun loadInsights() {
        val interactor = InsightsInteractor(habitList)
        loadTask = LoadInsightsTask(interactor) { insights ->
            if (!areInsightsDifferent(currentInsights, insights)) {
                return@LoadInsightsTask
            }
            currentInsights = insights
            carousel.setInsights(insights)
        }
        taskRunner.execute(loadTask!!)
    }

    private fun areInsightsDifferent(old: List<Insight>, new: List<Insight>): Boolean {
        if (old.size != new.size) return true
        return old.zip(new).any { (o, n) -> o.id != n.id }
    }

    fun setupCarousel() {
        carousel.onViewDetailsClickListener = {
            screen.showInsightsDetail()
        }

        carousel.onApplySuggestionListener = { insight ->
            applySuggestion(insight)
        }

        carousel.onInsightClickListener = { insight ->
            if (insight.habit != null) {
                screen.showHabitForInsight(insight.habit.id!!)
            } else {
                screen.showInsightsDetail()
            }
        }
    }

    private fun applySuggestion(insight: Insight) {
        when (val suggestion = insight.suggestion) {
            is InsightSuggestion.AdjustReminder -> {
                val habit = habitList.getById(suggestion.habitId) ?: return
                val updatedHabit = habit.copy()
                updatedHabit.reminder = Reminder(
                    suggestion.suggestedHour,
                    suggestion.suggestedMinute,
                    intArrayOf()
                )
                commandRunner.run(EditHabitCommand(habitList, habit.id!!, updatedHabit))
                loadInsights()
            }
            is InsightSuggestion.CreateHabit -> {
            }
            is InsightSuggestion.AdjustFrequency -> {
            }
            null -> {}
        }
    }
}
