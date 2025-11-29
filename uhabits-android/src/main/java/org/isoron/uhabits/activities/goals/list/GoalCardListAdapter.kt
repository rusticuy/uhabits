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

package org.isoron.uhabits.activities.goals.list

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import org.isoron.uhabits.R
import org.isoron.uhabits.activities.goals.show.ShowGoalActivity
import org.isoron.uhabits.core.models.Goal
import org.isoron.uhabits.core.models.GoalList
import org.isoron.uhabits.core.models.Timestamp
import java.util.Calendar

class GoalCardListAdapter(
    private val goalList: GoalList,
    private val context: Context
) : RecyclerView.Adapter<GoalCardViewHolder>() {

    private val goals = mutableListOf<Goal>()

    init {
        refreshGoals()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalCardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.goal_card, parent, false) as MaterialCardView
        return GoalCardViewHolder(view, context)
    }

    override fun onBindViewHolder(holder: GoalCardViewHolder, position: Int) {
        val goal = goals[position]
        holder.bind(goal, goalList)
    }

    override fun getItemCount(): Int = goals.size

    fun refreshGoals() {
        goals.clear()
        goals.addAll(goalList.getAll())
    }

    override fun notifyDataSetChanged() {
        refreshGoals()
        super.notifyDataSetChanged()
    }
}

class GoalCardViewHolder(
    itemView: android.view.View,
    private val context: Context
) : RecyclerView.ViewHolder(itemView) {

    private val goalName: TextView = itemView.findViewById(R.id.goalName)
    private val habitCount: TextView = itemView.findViewById(R.id.habitCount)
    private val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
    private val dueDateChip: Chip = itemView.findViewById(R.id.dueDateChip)

    fun bind(goal: Goal, goalList: GoalList) {
        goalName.text = goal.name
        habitCount.text = context.getString(
            R.string.goal_habits_linked,
            goal.linkedHabits.size
        )
        progressBar.progress = calculateProgress(goal)
        dueDateChip.text = goal.deadline.toDateString()

        itemView.setOnClickListener {
            val intent = Intent(context, ShowGoalActivity::class.java).apply {
                putExtra("goalId", goal.id)
            }
            context.startActivity(intent)
        }
    }

    private fun calculateProgress(goal: Goal): Int {
        return if (goal.milestones.isEmpty()) {
            0
        } else {
            val completedMilestones = goal.milestones.count { it.isCompleted }
            (completedMilestones * 100) / goal.milestones.size
        }
    }
}

private fun Timestamp.toDateString(): String {
    val cal = this.toCalendar()
    return "${cal.get(Calendar.MONTH) + 1}/${cal.get(Calendar.DAY_OF_MONTH)}/${cal.get(Calendar.YEAR)}"
}
