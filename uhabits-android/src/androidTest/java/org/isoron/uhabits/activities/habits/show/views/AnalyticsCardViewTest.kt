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
package org.isoron.uhabits.activities.habits.show.views

import android.view.LayoutInflater
import android.view.View
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import org.isoron.uhabits.BaseViewTest
import org.isoron.uhabits.R
import org.isoron.uhabits.core.ui.screens.habits.show.views.AnalyticsCardPresenter.Companion.buildState
import org.isoron.uhabits.core.ui.views.LightTheme
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class AnalyticsCardViewTest : BaseViewTest() {
    val PATH = "habits/show/AnalyticsCard/"
    private lateinit var view: AnalyticsCardView

    @Before
    override fun setUp() {
        super.setUp()
        val habit = fixtures.createLongHabit()
        view = LayoutInflater
            .from(targetContext)
            .inflate(R.layout.show_habit, null)
            .findViewById<View>(R.id.analyticsCard) as AnalyticsCardView
        view.setState(
            buildState(
                habit = habit,
                firstWeekday = preferences.firstWeekday,
                theme = LightTheme()
            )
        )
        measureView(view, 800f, 900f)
    }

    @Test
    fun testRender() {
        assertRenders(view, PATH + "render.png")
    }

    @Test
    fun testHeatmapRenders() {
        val heatmap = view.findViewById<View>(R.id.heatmapChart)
        assert(heatmap != null)
    }

    @Test
    fun testTrendChartRenders() {
        val trendChart = view.findViewById<View>(R.id.trendChart)
        assert(trendChart != null)
    }

    @Test
    fun testChainTimelineRenders() {
        val chainTimeline = view.findViewById<View>(R.id.chainTimelineRecyclerView)
        assert(chainTimeline != null)
    }
}
