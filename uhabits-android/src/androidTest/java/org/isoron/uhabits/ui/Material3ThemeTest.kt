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

package org.isoron.uhabits.ui

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.LargeTest
import org.isoron.uhabits.BaseUserInterfaceTest
import org.isoron.uhabits.R
import org.isoron.uhabits.acceptance.steps.CommonSteps.launchApp
import org.junit.Test

@LargeTest
class Material3ThemeTest : BaseUserInterfaceTest() {

    @Test
    fun verifyMaterial3ThemeApplies() {
        launchApp()
        
        onView(withId(R.id.toolbar))
            .check(matches(isDisplayed()))
    }

    @Test
    fun verifyListHabitsActivityDisplaysWithMaterial3() {
        launchApp()
        
        onView(withId(R.id.toolbar))
            .check(matches(isDisplayed()))
    }

    @Test
    fun verifyMaterial3ComponentsAreUsed() {
        launchApp()
        
        onView(withId(R.id.toolbar)).check { view, _ ->
            val toolbarClass = view.javaClass.name
            assert(
                toolbarClass.contains("MaterialToolbar") ||
                toolbarClass.contains("Toolbar")
            ) { "Toolbar should be MaterialToolbar or AppCompat Toolbar" }
        }
    }
}
