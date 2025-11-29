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

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.isoron.uhabits.HabitsApplication
import org.isoron.uhabits.activities.goals.edit.EditGoalActivity
import org.isoron.uhabits.core.models.GoalList
import org.isoron.uhabits.inject.ActivityContextModule
import org.isoron.uhabits.inject.DaggerHabitsActivityComponent
import org.isoron.uhabits.inject.HabitsActivityComponent
import org.isoron.uhabits.utils.applyRootViewInsets
import org.isoron.uhabits.utils.enableEdgeToEdge

class ListGoalsActivity : AppCompatActivity() {

    private lateinit var goalList: GoalList
    private lateinit var adapter: GoalCardListAdapter
    private lateinit var component: HabitsActivityComponent
    private lateinit var rootView: ListGoalsRootView

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
        adapter = GoalCardListAdapter(goalList, this)

        rootView = ListGoalsRootView(this, adapter)
        rootView.applyRootViewInsets()
        setContentView(rootView)

        rootView.setFabClickListener {
            startActivity(Intent(this, EditGoalActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }
}
