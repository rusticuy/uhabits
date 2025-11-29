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

package org.isoron.uhabits.activities.goals.edit

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import org.isoron.uhabits.HabitsApplication
import org.isoron.uhabits.R
import org.isoron.uhabits.core.models.Goal
import org.isoron.uhabits.core.models.GoalList
import org.isoron.uhabits.core.models.Timestamp
import org.isoron.uhabits.inject.ActivityContextModule
import org.isoron.uhabits.inject.DaggerHabitsActivityComponent
import org.isoron.uhabits.inject.HabitsActivityComponent
import org.isoron.uhabits.utils.applyRootViewInsets
import org.isoron.uhabits.utils.enableEdgeToEdge
import java.util.Calendar

class EditGoalActivity : AppCompatActivity() {

    private lateinit var goalList: GoalList
    private lateinit var component: HabitsActivityComponent
    private var goalId: Long? = null
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
        goalId = intent.getLongExtra("goalId", -1).takeIf { it != -1L }

        if (goalId != null) {
            currentGoal = goalList.getById(goalId!!)
        }

        val rootView = EditGoalView(this, currentGoal)
        rootView.applyRootViewInsets()
        setContentView(rootView)

        rootView.setOnSaveClickListener {
            saveGoal()
        }
    }

    private fun saveGoal() {
        if (currentGoal == null) {
            currentGoal = Goal(name = "New Goal")
        }
        goalList.add(currentGoal!!)
        finish()
    }
}
