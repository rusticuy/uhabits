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

package org.isoron.uhabits.material

import android.app.Activity
import android.content.ContentUris
import android.content.Intent
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import org.isoron.uhabits.BaseUserInterfaceTest
import org.isoron.uhabits.BaseViewTest
import org.isoron.uhabits.R
import org.isoron.uhabits.activities.habits.list.ListHabitsActivity
import org.isoron.uhabits.activities.habits.show.ShowHabitActivity
import org.isoron.uhabits.core.ui.ThemeSwitcher
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 * Material3 UI screenshot tests for light, dark, and pure-black themes.
 *
 * To regenerate golden images:
 * 1. Run: adb shell am instrument -w -e generateAssets true org.isoron.uhabits.test/androidx.test.runner.AndroidJUnitRunner
 * 2. Pull screenshots from: adb pull /data/data/org.isoron.uhabits/test-screenshots/views/habits/material/
 * 3. Replace assets in src/androidTest/assets/views/habits/material/
 * 4. Run tests again to verify they pass
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class MaterialThemeScreenshotTest : BaseUserInterfaceTest() {

    private val viewTestHelper = BaseViewTest()
    private var capturedActivity: Activity? = null

    @Test
    @Throws(IOException::class)
    fun testListHabitsLight() {
        captureListHabits(
            ThemeSwitcher.THEME_LIGHT,
            false,
            "habits/material/list_habits_light.png"
        )
    }

    @Test
    @Throws(IOException::class)
    fun testListHabitsDark() {
        captureListHabits(
            ThemeSwitcher.THEME_DARK,
            false,
            "habits/material/list_habits_dark.png"
        )
    }

    @Test
    @Throws(IOException::class)
    fun testListHabitsPureBlack() {
        captureListHabits(
            ThemeSwitcher.THEME_DARK,
            true,
            "habits/material/list_habits_pure_black.png"
        )
    }

    @Test
    @Throws(IOException::class)
    fun testShowHabitLight() {
        captureShowHabit(
            ThemeSwitcher.THEME_LIGHT,
            false,
            "habits/material/show_habit_light.png"
        )
    }

    @Test
    @Throws(IOException::class)
    fun testShowHabitDark() {
        captureShowHabit(
            ThemeSwitcher.THEME_DARK,
            false,
            "habits/material/show_habit_dark.png"
        )
    }

    @Test
    @Throws(IOException::class)
    fun testShowHabitPureBlack() {
        captureShowHabit(
            ThemeSwitcher.THEME_DARK,
            true,
            "habits/material/show_habit_pure_black.png"
        )
    }

    @Throws(IOException::class)
    private fun captureListHabits(theme: Int, pureBlack: Boolean, filename: String) {
        prefs.theme = theme
        prefs.isPureBlackEnabled = pureBlack

        val instrumentation = InstrumentationRegistry.getInstrumentation()
        capturedActivity = null

        startActivity(ListHabitsActivity::class.java)

        val uiDevice = UiDevice.getInstance(instrumentation)
        uiDevice.wait(Until.hasObject(By.pkg("org.isoron.uhabits")), 5000)
        instrumentation.waitForIdleSync()
        Thread.sleep(1500)

        instrumentation.runOnMainSync {
            capturedActivity = findCurrentActivity(ListHabitsActivity::class.java)
        }

        val activity = capturedActivity as? ListHabitsActivity
            ?: throw RuntimeException("Could not find ListHabitsActivity")

        captureViewScreenshot(activity.rootView, filename)
    }

    @Throws(IOException::class)
    private fun captureShowHabit(theme: Int, pureBlack: Boolean, filename: String) {
        prefs.theme = theme
        prefs.isPureBlackEnabled = pureBlack

        val habits = habitList.habits
        if (habits.isEmpty()) {
            throw RuntimeException("No habits available for screenshot")
        }

        val habit = habits[0]
        val uri = ContentUris.withAppendedId(android.provider.BaseColumns._ID, habit.id)
        val intent = Intent().apply {
            component = android.content.ComponentName(
                "org.isoron.uhabits",
                ShowHabitActivity::class.java.canonicalName!!
            )
            data = uri
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        targetContext.startActivity(intent)

        val instrumentation = InstrumentationRegistry.getInstrumentation()
        capturedActivity = null

        val uiDevice = UiDevice.getInstance(instrumentation)
        uiDevice.wait(Until.hasObject(By.pkg("org.isoron.uhabits")), 5000)
        instrumentation.waitForIdleSync()
        Thread.sleep(1500)

        instrumentation.runOnMainSync {
            capturedActivity = findCurrentActivity(ShowHabitActivity::class.java)
        }

        val activity = capturedActivity as? ShowHabitActivity
            ?: throw RuntimeException("Could not find ShowHabitActivity")

        captureViewScreenshot(activity.window.decorView, filename)
    }

    @Throws(IOException::class)
    private fun captureViewScreenshot(view: android.view.View, filename: String) {
        view.measure(0, 0)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        val bitmap = viewTestHelper.renderView(view)
        viewTestHelper.assertRenders(bitmap, filename)
    }

    private fun findCurrentActivity(targetClass: Class<*>): Activity? {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val rootDecor = instrumentation.targetContext.window?.decorView

        rootDecor?.let {
            try {
                val field = rootDecor::class.java.getDeclaredField("mContext")
                field.isAccessible = true
                val context = field.get(rootDecor)
                if (context is Activity && targetClass.isInstance(context)) {
                    return context
                }
            } catch (e: Exception) {
                // Ignore
            }
        }

        return null
    }
}
