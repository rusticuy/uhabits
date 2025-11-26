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

package org.isoron.uhabits.security

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.filters.LargeTest
import org.isoron.uhabits.BaseUserInterfaceTest
import org.isoron.uhabits.R
import org.isoron.uhabits.acceptance.steps.CommonSteps.clickText
import org.isoron.uhabits.acceptance.steps.CommonSteps.launchApp
import org.isoron.uhabits.acceptance.steps.CommonSteps.verifyDisplaysText
import org.isoron.uhabits.acceptance.steps.ListHabitsSteps.clickMenu
import org.isoron.uhabits.acceptance.steps.ListHabitsSteps.MenuItem.SETTINGS
import org.junit.Test

@LargeTest
class AppLockTest : BaseUserInterfaceTest() {

    @Test
    fun shouldShowLockScreenWhenPinIsEnabled() {
        launchApp()
        
        clickMenu(SETTINGS)
        verifyDisplaysText("Settings")
        
        Thread.sleep(500)
        
        clickText("App Lock")
        clickText("PIN")
        
        onView(withId(R.id.pinInput)).perform(typeText("1234"))
        onView(withId(R.id.unlockButton)).perform(click())
        
        Thread.sleep(1000)
        
        launchApp()
        
        onView(withId(R.id.pinInput)).check(matches(isDisplayed()))
        onView(withText("Unlock App")).check(matches(isDisplayed()))
    }

    @Test
    fun shouldUnlockWithCorrectPin() {
        launchApp()
        
        clickMenu(SETTINGS)
        verifyDisplaysText("Settings")
        
        Thread.sleep(500)
        
        clickText("App Lock")
        clickText("PIN")
        
        onView(withId(R.id.pinInput)).perform(typeText("5678"))
        onView(withId(R.id.unlockButton)).perform(click())
        
        Thread.sleep(1000)
        
        launchApp()
        
        onView(withId(R.id.pinInput)).perform(typeText("5678"))
        onView(withId(R.id.unlockButton)).perform(click())
        
        Thread.sleep(500)
        verifyDisplaysText("Habits")
    }

    @Test
    fun shouldRejectIncorrectPin() {
        launchApp()
        
        clickMenu(SETTINGS)
        verifyDisplaysText("Settings")
        
        Thread.sleep(500)
        
        clickText("App Lock")
        clickText("PIN")
        
        onView(withId(R.id.pinInput)).perform(typeText("9999"))
        onView(withId(R.id.unlockButton)).perform(click())
        
        Thread.sleep(1000)
        
        launchApp()
        
        onView(withId(R.id.pinInput)).perform(typeText("1111"))
        onView(withId(R.id.unlockButton)).perform(click())
        
        Thread.sleep(500)
        
        onView(withId(R.id.pinInput)).check(matches(isDisplayed()))
    }

    @Test
    fun shouldWorkWithKeypad() {
        launchApp()
        
        clickMenu(SETTINGS)
        verifyDisplaysText("Settings")
        
        Thread.sleep(500)
        
        clickText("App Lock")
        clickText("PIN")
        
        onView(withId(R.id.btn1)).perform(click())
        onView(withId(R.id.btn2)).perform(click())
        onView(withId(R.id.btn3)).perform(click())
        onView(withId(R.id.btn4)).perform(click())
        onView(withId(R.id.unlockButton)).perform(click())
        
        Thread.sleep(1000)
        
        launchApp()
        
        onView(withId(R.id.btn1)).perform(click())
        onView(withId(R.id.btn2)).perform(click())
        onView(withId(R.id.btn3)).perform(click())
        onView(withId(R.id.btn4)).perform(click())
        onView(withId(R.id.unlockButton)).perform(click())
        
        Thread.sleep(500)
        verifyDisplaysText("Habits")
    }

    @Test
    fun shouldWorkWithBackspace() {
        launchApp()
        
        clickMenu(SETTINGS)
        verifyDisplaysText("Settings")
        
        Thread.sleep(500)
        
        clickText("App Lock")
        clickText("PIN")
        
        onView(withId(R.id.btn5)).perform(click())
        onView(withId(R.id.btn6)).perform(click())
        onView(withId(R.id.btn7)).perform(click())
        onView(withId(R.id.btnBackspace)).perform(click())
        onView(withId(R.id.btn8)).perform(click())
        onView(withId(R.id.unlockButton)).perform(click())
        
        Thread.sleep(1000)
        
        launchApp()
        
        onView(withId(R.id.btn5)).perform(click())
        onView(withId(R.id.btn6)).perform(click())
        onView(withId(R.id.btn8)).perform(click())
        onView(withId(R.id.unlockButton)).perform(click())
        
        Thread.sleep(500)
        verifyDisplaysText("Habits")
    }
}
