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

package org.isoron.uhabits.activities.habits.show

import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.rule.ActivityTestRule
import org.isoron.uhabits.BaseAndroidTest
import org.isoron.uhabits.R
import org.isoron.uhabits.activities.habits.show.views.AnalyticsCardView
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class ShowHabitAnalyticsTest : BaseAndroidTest() {

    @get:Rule
    val activityRule = ActivityTestRule(ShowHabitActivity::class.java, false, false)

    @Before
    override fun setUp() {
        super.setUp()
    }

    @Test
    fun testAnalyticsCardVisible() {
        val habit = fixtures.createLongHabit()
        val intent = Intent().apply {
            data = android.content.ContentUris.withAppendedId(
                android.net.Uri.parse("content://org.isoron.uhabits/habits"),
                habit.id!!
            )
        }
        activityRule.launchActivity(intent)

        onView(withId(R.id.analyticsCard))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testHeatmapChartVisible() {
        val habit = fixtures.createLongHabit()
        val intent = Intent().apply {
            data = android.content.ContentUris.withAppendedId(
                android.net.Uri.parse("content://org.isoron.uhabits/habits"),
                habit.id!!
            )
        }
        activityRule.launchActivity(intent)

        onView(withId(R.id.heatmapChart))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testTrendChartVisible() {
        val habit = fixtures.createLongHabit()
        val intent = Intent().apply {
            data = android.content.ContentUris.withAppendedId(
                android.net.Uri.parse("content://org.isoron.uhabits/habits"),
                habit.id!!
            )
        }
        activityRule.launchActivity(intent)

        onView(withId(R.id.trendChart))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testChainTimelineVisible() {
        val habit = fixtures.createLongHabit()
        val intent = Intent().apply {
            data = android.content.ContentUris.withAppendedId(
                android.net.Uri.parse("content://org.isoron.uhabits/habits"),
                habit.id!!
            )
        }
        activityRule.launchActivity(intent)

        onView(withId(R.id.chainTimelineRecyclerView))
            .check(matches(isDisplayed()))
    }
}
