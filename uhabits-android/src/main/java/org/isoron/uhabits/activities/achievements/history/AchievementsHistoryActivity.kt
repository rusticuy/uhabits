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

package org.isoron.uhabits.activities.achievements.history

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import org.isoron.uhabits.R
import org.isoron.uhabits.HabitsApplication
import org.isoron.uhabits.core.models.AchievementList
import org.isoron.uhabits.core.models.HabitList
import org.isoron.uhabits.inject.ActivityContextModule
import org.isoron.uhabits.inject.DaggerHabitsActivityComponent
import org.isoron.uhabits.inject.HabitsActivityComponent
import org.isoron.uhabits.utils.applyRootViewInsets
import org.isoron.uhabits.utils.enableEdgeToEdge

class AchievementsHistoryActivity : AppCompatActivity() {

    private lateinit var achievementList: AchievementList
    private lateinit var habitList: HabitList
    private lateinit var adapter: AchievementCardListAdapter
    private lateinit var component: HabitsActivityComponent
    private lateinit var rootView: AchievementsHistoryRootView

    private var selectedStreakTier: Int = 0
    private var selectedHabitId: Long? = null

    companion object {
        const val EXTRA_HABIT_ID = "habit_id"
        const val EXTRA_STREAK_TIER = "streak_tier"
        private const val STATE_STREAK_TIER = "state_streak_tier"
        private const val STATE_HABIT_ID = "state_habit_id"
    }

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

        achievementList = appComponent.achievementList
        habitList = appComponent.habitList
        adapter = AchievementCardListAdapter(achievementList, habitList)

        rootView = AchievementsHistoryRootView(this, adapter = adapter)
        rootView.applyRootViewInsets()
        setContentView(rootView)

        val toolbar = rootView.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.achievements)

        if (savedInstanceState != null) {
            selectedStreakTier = savedInstanceState.getInt(STATE_STREAK_TIER, 0)
            selectedHabitId = savedInstanceState.getLong(STATE_HABIT_ID, -1).takeIf { it != -1L }
        } else {
            selectedStreakTier = intent.getIntExtra(EXTRA_STREAK_TIER, 0)
            val habitId = intent.getLongExtra(EXTRA_HABIT_ID, -1)
            selectedHabitId = if (habitId != -1L) habitId else null
        }

        adapter.setFilterHabit(selectedHabitId)
        adapter.filterByStreakTier(selectedStreakTier)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(STATE_STREAK_TIER, selectedStreakTier)
        outState.putLong(STATE_HABIT_ID, selectedHabitId ?: -1)
    }

    override fun onResume() {
        super.onResume()
        adapter.refresh()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
