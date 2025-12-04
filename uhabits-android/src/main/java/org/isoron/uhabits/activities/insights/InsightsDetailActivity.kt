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
package org.isoron.uhabits.activities.insights

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import org.isoron.uhabits.HabitsApplication
import org.isoron.uhabits.R
import org.isoron.uhabits.core.insights.Insight
import org.isoron.uhabits.core.insights.InsightsInteractor
import org.isoron.uhabits.core.models.HabitList
import org.isoron.uhabits.core.models.Reminder
import org.isoron.uhabits.core.commands.CommandRunner
import org.isoron.uhabits.core.commands.EditHabitCommand
import org.isoron.uhabits.core.insights.InsightSuggestion
import org.isoron.uhabits.core.tasks.LoadInsightsTask
import org.isoron.uhabits.core.tasks.TaskRunner
import org.isoron.uhabits.intents.IntentFactory
import org.isoron.uhabits.utils.applyRootViewInsets
import org.isoron.uhabits.utils.enableEdgeToEdge

class InsightsDetailActivity : AppCompatActivity() {

    private lateinit var habitList: HabitList
    private lateinit var taskRunner: TaskRunner
    private lateinit var commandRunner: CommandRunner
    private lateinit var intentFactory: IntentFactory
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: TextView
    private val adapter = InsightsDetailAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insights_detail)

        val appComponent = (application as HabitsApplication).component
        habitList = appComponent.habitList
        taskRunner = appComponent.taskRunner
        commandRunner = appComponent.commandRunner
        intentFactory = appComponent.intentFactory
        appComponent.themeSwitcher.apply()
        enableEdgeToEdge()

        recyclerView = findViewById(R.id.insightsRecyclerView)
        emptyView = findViewById(R.id.emptyInsightsText)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        findViewById<View>(android.R.id.content).applyRootViewInsets()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.insights)

        loadInsights()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun loadInsights() {
        val interactor = InsightsInteractor(habitList)
        taskRunner.execute(
            LoadInsightsTask(interactor) { insights ->
                val allInsights = insights + listOfNotNull(interactor.getWeeklySummary())
                adapter.insights = allInsights
                adapter.notifyDataSetChanged()

                if (allInsights.isEmpty()) {
                    recyclerView.visibility = View.GONE
                    emptyView.visibility = View.VISIBLE
                } else {
                    recyclerView.visibility = View.VISIBLE
                    emptyView.visibility = View.GONE
                }
            }
        )
    }

    private inner class InsightsDetailAdapter :
        RecyclerView.Adapter<InsightsDetailAdapter.ViewHolder>() {

        var insights: List<Insight> = emptyList()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_insight_detail, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(insights[position])
        }

        override fun getItemCount() = insights.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            private val titleView: TextView = view.findViewById(R.id.insightTitle)
            private val bodyView: TextView = view.findViewById(R.id.insightBody)
            private val applySuggestionButton: MaterialButton =
                view.findViewById(R.id.applySuggestionButton)
            private val viewHabitButton: MaterialButton = view.findViewById(R.id.viewHabitButton)

            fun bind(insight: Insight) {
                titleView.text = insight.title
                bodyView.text = insight.body

                if (insight.suggestion != null) {
                    applySuggestionButton.visibility = View.VISIBLE
                    applySuggestionButton.setOnClickListener {
                        applySuggestion(insight)
                    }
                } else {
                    applySuggestionButton.visibility = View.GONE
                }

                if (insight.habit != null) {
                    viewHabitButton.visibility = View.VISIBLE
                    viewHabitButton.setOnClickListener {
                        val intent = intentFactory.startShowHabitActivity(
                            this@InsightsDetailActivity,
                            insight.habit
                        )
                        startActivity(intent)
                    }
                } else {
                    viewHabitButton.visibility = View.GONE
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
    }
}
