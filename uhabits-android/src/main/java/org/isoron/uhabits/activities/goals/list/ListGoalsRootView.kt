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
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbarwidget.AppBarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.isoron.uhabits.R

class ListGoalsRootView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
    val adapter: GoalCardListAdapter? = null
) : FrameLayout(context, attrs, defStyle) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var appBar: AppBarLayout
    private lateinit var fab: FloatingActionButton

    init {
        inflate(context, R.layout.list_goals, this)
        setupViews()
    }

    private fun setupViews() {
        appBar = findViewById(R.id.appBar)
        recyclerView = findViewById(R.id.recyclerView)
        fab = findViewById(R.id.fab)

        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter?.let {
            recyclerView.adapter = it
        }
    }

    fun setFabClickListener(listener: OnClickListener) {
        fab.setOnClickListener(listener)
    }
}
