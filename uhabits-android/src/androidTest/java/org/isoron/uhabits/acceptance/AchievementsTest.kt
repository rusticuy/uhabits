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
package org.isoron.uhabits.acceptance

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.isoron.uhabits.BaseUserInterfaceTest
import org.isoron.uhabits.acceptance.steps.CommonSteps.launchApp
import org.isoron.uhabits.acceptance.steps.CommonSteps.verifyDisplaysText
import org.isoron.uhabits.acceptance.steps.CommonSteps.clickText
import org.isoron.uhabits.acceptance.steps.CommonSteps.pressBack
import org.isoron.uhabits.R
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class AchievementsTest : BaseUserInterfaceTest() {

    @Test
    @Throws(Exception::class)
    fun shouldOpenAchievementsHistoryFromMenu() {
        launchApp()
        verifyDisplaysText("Track time")
        openOverflowMenu()
        clickText("Achievements")
        verifyDisplaysText("Achievements")
    }

    @Test
    @Throws(Exception::class)
    fun shouldDisplayAchievementsHistoryScreen() {
        launchApp()
        openOverflowMenu()
        clickText("Achievements")
        verifyDisplaysText("Achievements")
        pressBack()
        verifyDisplaysText("Track time")
    }

    @Test
    @Throws(Exception::class)
    fun shouldNavigateBackFromAchievementsHistory() {
        launchApp()
        openOverflowMenu()
        clickText("Achievements")
        pressBack()
        verifyDisplaysText("Track time")
    }

    private fun openOverflowMenu() {
        Espresso.openActionBarOverflowOrOptionsMenu(device.targetContext)
        device.waitForIdle()
    }
}
