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

import android.content.ContentResolver
import android.provider.Settings
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import nl.dionsegijn.konfetti.xml.KonfettiView
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.isoron.uhabits.BaseAndroidTest
import org.isoron.uhabits.core.models.PaletteColor
import org.isoron.uhabits.core.preferences.Preferences
import org.isoron.uhabits.core.ui.ThemeSwitcher
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.spy
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

@RunWith(AndroidJUnit4::class)
class CelebrationConfettiControllerTest : BaseAndroidTest() {

    private lateinit var konfettiView: KonfettiView
    private lateinit var preferences: Preferences
    private lateinit var themeSwitcher: ThemeSwitcher
    private lateinit var contentResolver: ContentResolver
    private lateinit var controller: CelebrationConfettiController

    @Before
    override fun setUp() {
        super.setUp()
        konfettiView = spy(KonfettiView(targetContext))
        preferences = appComponent.preferences
        themeSwitcher = appComponent.themeSwitcher
        contentResolver = targetContext.contentResolver

        preferences.isConfettiAnimationDisabled = false

        controller = CelebrationConfettiController(
            konfettiView = konfettiView,
            preferences = preferences,
            themeSwitcher = themeSwitcher,
            contentResolver = contentResolver
        )
    }

    @Test
    fun testConfettiPlaysOnStart() {
        val state = CelebrationState(
            title = "Achievement!",
            message = "Test message",
            palette = PaletteColor(8),
            playConfetti = true
        )

        controller.onStart(state)
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        verify(konfettiView, times(1)).start(org.mockito.kotlin.any())
    }

    @Test
    fun testConfettiDoesNotPlayWhenFlagIsFalse() {
        val state = CelebrationState(
            title = "Achievement!",
            message = "Test message",
            palette = PaletteColor(8),
            playConfetti = false
        )

        controller.onStart(state)
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        verify(konfettiView, never()).start(org.mockito.kotlin.any())
    }

    @Test
    fun testConfettiDoesNotPlayWhenDisabledInPreferences() {
        preferences.isConfettiAnimationDisabled = true
        val state = CelebrationState(
            title = "Achievement!",
            message = "Test message",
            palette = PaletteColor(8),
            playConfetti = true
        )

        controller.onStart(state)
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        verify(konfettiView, never()).start(org.mockito.kotlin.any())
    }

    @Test
    fun testConfettiPlaysOnlyOnce() {
        val state = CelebrationState(
            title = "Achievement!",
            message = "Test message",
            palette = PaletteColor(8),
            playConfetti = true
        )

        controller.onStart(state)
        controller.onStop()
        controller.onStart(state)
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        verify(konfettiView, times(1)).start(org.mockito.kotlin.any())
    }

    @Test
    fun testConfettiStopsOnStop() {
        val state = CelebrationState(
            title = "Achievement!",
            message = "Test message",
            palette = PaletteColor(8),
            playConfetti = true
        )

        controller.onStart(state)
        controller.onStop()

        verify(konfettiView, times(1)).stop()
    }

    @Test
    fun testConfettiStopsOnDestroyView() {
        val state = CelebrationState(
            title = "Achievement!",
            message = "Test message",
            palette = PaletteColor(8),
            playConfetti = true
        )

        controller.onStart(state)
        controller.onDestroyView()

        verify(konfettiView, times(1)).stop()
    }

    @Test
    fun testConfettiResetsAfterDestroyView() {
        val state = CelebrationState(
            title = "Achievement!",
            message = "Test message",
            palette = PaletteColor(8),
            playConfetti = true
        )

        controller.onStart(state)
        controller.onDestroyView()

        val newController = CelebrationConfettiController(
            konfettiView = konfettiView,
            preferences = preferences,
            themeSwitcher = themeSwitcher,
            contentResolver = contentResolver
        )

        newController.onStart(state)
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        verify(konfettiView, times(2)).start(org.mockito.kotlin.any())
    }

    @Test
    fun testMultipleStopCallsDoNotCrash() {
        val state = CelebrationState(
            title = "Achievement!",
            message = "Test message",
            palette = PaletteColor(8),
            playConfetti = true
        )

        controller.onStart(state)
        controller.onStop()
        controller.onStop()
        controller.onDestroyView()
    }
}
