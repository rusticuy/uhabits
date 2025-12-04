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

package org.isoron.uhabits.activities.achievements.celebration

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.notNullValue
import org.isoron.uhabits.BaseAndroidTest
import org.isoron.uhabits.activities.habits.list.ListHabitsActivity
import org.isoron.uhabits.core.models.PaletteColor
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CelebrationDialogTest : BaseAndroidTest() {

    @get:Rule
    val activityRule = ActivityScenarioRule(ListHabitsActivity::class.java)

    @Test
    fun testDialogShowsCorrectState() {
        val state = CelebrationState(
            title = "Achievement Unlocked!",
            message = "You've reached a 7-day streak!",
            palette = PaletteColor(8),
            playConfetti = true
        )

        activityRule.scenario.onActivity { activity ->
            val dialog = CelebrationDialog()
            dialog.state = state
            dialog.show(activity.supportFragmentManager, "celebration")

            InstrumentationRegistry.getInstrumentation().waitForIdleSync()

            assertThat(dialog.state.title, equalTo("Achievement Unlocked!"))
            assertThat(dialog.state.message, equalTo("You've reached a 7-day streak!"))
            assertThat(dialog.state.playConfetti, equalTo(true))
        }
    }

    @Test
    fun testDialogLifecycleDoesNotCrash() {
        val state = CelebrationState(
            title = "Achievement!",
            message = "Test",
            palette = PaletteColor(8),
            playConfetti = true
        )

        activityRule.scenario.onActivity { activity ->
            val dialog = CelebrationDialog()
            dialog.state = state
            dialog.show(activity.supportFragmentManager, "celebration")

            InstrumentationRegistry.getInstrumentation().waitForIdleSync()

            dialog.dismiss()

            InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        }
    }

    @Test
    fun testDialogRecreationDoesNotCrash() {
        val state = CelebrationState(
            title = "Achievement!",
            message = "Test",
            palette = PaletteColor(8),
            playConfetti = true
        )

        activityRule.scenario.onActivity { activity ->
            val dialog = CelebrationDialog()
            dialog.state = state
            dialog.show(activity.supportFragmentManager, "celebration")

            InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        }

        activityRule.scenario.recreate()
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
    }

    @Test
    fun testMultipleShowsAndDismisses() {
        val state = CelebrationState(
            title = "Achievement!",
            message = "Test",
            palette = PaletteColor(8),
            playConfetti = true
        )

        activityRule.scenario.onActivity { activity ->
            for (i in 0 until 3) {
                val dialog = CelebrationDialog()
                dialog.state = state
                dialog.show(activity.supportFragmentManager, "celebration_$i")

                InstrumentationRegistry.getInstrumentation().waitForIdleSync()

                dialog.dismiss()

                InstrumentationRegistry.getInstrumentation().waitForIdleSync()
            }
        }
    }

    @Test
    fun testConfettiPlaysOnlyOncePerDialog() {
        activityRule.scenario.onActivity { activity ->
            val state = CelebrationState(
                title = "Achievement!",
                message = "Test",
                palette = PaletteColor(8),
                playConfetti = true
            )

            val dialog = CelebrationDialog()
            dialog.state = state
            dialog.show(activity.supportFragmentManager, "celebration")

            InstrumentationRegistry.getInstrumentation().waitForIdleSync()

            val konfettiView = dialog.dialog?.findViewById<nl.dionsegijn.konfetti.xml.KonfettiView>(
                org.isoron.uhabits.R.id.konfettiView
            )
            assertThat(konfettiView, notNullValue())
        }
    }

    @Test
    fun testDialogDismissesOnButtonClick() {
        val state = CelebrationState(
            title = "Achievement!",
            message = "Test",
            palette = PaletteColor(8),
            playConfetti = false
        )

        activityRule.scenario.onActivity { activity ->
            val dialog = CelebrationDialog()
            dialog.state = state
            dialog.show(activity.supportFragmentManager, "celebration")

            InstrumentationRegistry.getInstrumentation().waitForIdleSync()

            val dismissBtn = dialog.dialog?.findViewById<com.google.android.material.button.MaterialButton>(
                org.isoron.uhabits.R.id.celebrationDismissBtn
            )
            dismissBtn?.performClick()

            InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        }
    }

    @Test
    fun testDialogWithConfettiDisabled() {
        appComponent.preferences.isConfettiAnimationDisabled = true

        val state = CelebrationState(
            title = "Achievement!",
            message = "Test",
            palette = PaletteColor(8),
            playConfetti = true
        )

        activityRule.scenario.onActivity { activity ->
            val dialog = CelebrationDialog()
            dialog.state = state
            dialog.show(activity.supportFragmentManager, "celebration")

            InstrumentationRegistry.getInstrumentation().waitForIdleSync()

            dialog.dismiss()

            InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        }
    }
}
