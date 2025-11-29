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

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.isoron.uhabits.HabitsApplication
import org.isoron.uhabits.activities.goals.edit.EditGoalActivity
import org.isoron.uhabits.core.models.Goal
import org.isoron.uhabits.core.models.GoalList
import org.isoron.uhabits.inject.ActivityContextModule
import org.isoron.uhabits.inject.DaggerHabitsActivityComponent
import org.isoron.uhabits.inject.HabitsActivityComponent
import org.isoron.uhabits.utils.applyRootViewInsets
import org.isoron.uhabits.utils.enableEdgeToEdge

class ShowGoalActivity : AppCompatActivity() {

    private lateinit var goalList: GoalList
    private lateinit var component: HabitsActivityComponent
    private var currentGoal: Goal? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appComponent = (applicationContext as HabitsApplication).component
        component = DaggerHabitsActivityComponent
            .builder()
            .activityContextModule(ActivityContextModule(this))
            .habitsApplicationComponent(appComponent)
            .build()

        component.themeSwitcher.apply()
        enableEdgeToEdge()

        goalList = appComponent.goalList

        val goalId = intent.getLongExtra("goalId", -1)
        if (goalId != -1L) {
            currentGoal = goalList.getById(goalId)
        }

        val rootView = ShowGoalView(this, currentGoal, goalList)
        rootView.applyRootViewInsets()
        setContentView(rootView)

        rootView.setOnEditClickListener {
            val editIntent = Intent(this, EditGoalActivity::class.java).apply {
                putExtra("goalId", currentGoal?.id)
            }
            startActivity(editIntent)
        }

        rootView.setOnArchiveClickListener {
            currentGoal?.isArchived = true
            goalList.update(currentGoal!!)
            finish()
        }

        rootView.setOnDeleteClickListener {
            currentGoal?.let { goalList.remove(it) }
            finish()
        }
    }
}
