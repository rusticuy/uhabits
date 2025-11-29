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

package org.isoron.uhabits.activities.goals.show

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.ChipGroup
import org.isoron.uhabits.R
import org.isoron.uhabits.core.models.Goal
import org.isoron.uhabits.core.models.GoalList

class ShowGoalView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
    val currentGoal: Goal? = null,
    val goalList: GoalList? = null
) : FrameLayout(context, attrs, defStyle) {

    lateinit var goalName: TextView
    lateinit var goalDescription: TextView
    lateinit var progressBar: ProgressBar
    lateinit var milestoneChips: ChipGroup
    lateinit var linkedHabitsContainer: LinearLayout
    lateinit var editButton: MaterialButton
    lateinit var archiveButton: MaterialButton
    lateinit var deleteButton: MaterialButton

    init {
        inflate(context, R.layout.show_goal, this)
        setupViews()
    }

    private fun setupViews() {
        goalName = findViewById(R.id.goalName)
        goalDescription = findViewById(R.id.goalDescription)
        progressBar = findViewById(R.id.progressBar)
        milestoneChips = findViewById(R.id.milestoneChips)
        linkedHabitsContainer = findViewById(R.id.linkedHabitsContainer)
        editButton = findViewById(R.id.editButton)
        archiveButton = findViewById(R.id.archiveButton)
        deleteButton = findViewById(R.id.deleteButton)

        currentGoal?.let {
            goalName.text = it.name
            goalDescription.text = it.description
            progressBar.progress = calculateProgress()
        }
    }

    private fun calculateProgress(): Int {
        return if (currentGoal?.milestones?.isEmpty() != false) {
            0
        } else {
            val completedMilestones = currentGoal!!.milestones.count { it.isCompleted }
            (completedMilestones * 100) / currentGoal!!.milestones.size
        }
    }

    fun setOnEditClickListener(listener: OnClickListener) {
        editButton.setOnClickListener(listener)
    }

    fun setOnArchiveClickListener(listener: OnClickListener) {
        archiveButton.setOnClickListener(listener)
    }

    fun setOnDeleteClickListener(listener: OnClickListener) {
        deleteButton.setOnClickListener(listener)
    }
}
