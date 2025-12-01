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

package org.isoron.uhabits.activities.achievements

import android.app.Activity
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.notNullValue
import org.isoron.uhabits.BaseUserInterfaceTest
import org.isoron.uhabits.BaseUserInterfaceTest.Companion.device
import org.isoron.uhabits.BaseUserInterfaceTest.Companion.startActivity
import org.isoron.uhabits.HabitsApplication
import org.isoron.uhabits.R
import org.isoron.uhabits.activities.habits.list.ListHabitsActivity
import org.isoron.uhabits.core.achievements.AchievementUnlocked
import org.isoron.uhabits.core.models.PaletteColor
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class AchievementCelebrationTest : BaseUserInterfaceTest() {

    @Test
    fun showsCelebrationDialogAndConfetti() {
        startActivity(ListHabitsActivity::class.java)
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.waitForIdleSync()

        val application = instrumentation.targetContext.applicationContext as HabitsApplication
        val detector = application.component.achievementDetector
        val event = AchievementUnlocked(
            id = "test-streak-50",
            title = "Streak legend",
            description = "Complete a 50 day streak",
            streakLength = 50,
            habitName = "Meditate",
            paletteColor = PaletteColor(5),
            iconKey = "streak"
        )

        instrumentation.runOnMainSync {
            detector.notifyUnlocked(event)
        }

        val titleShown = device.wait(Until.hasObject(By.text(event.title)), 5000)
        assertTrue("celebration title not visible", titleShown)
        val descriptionShown = device.wait(Until.hasObject(By.text(event.description)), 2000)
        assertTrue("celebration description not visible", descriptionShown)

        val streakText = instrumentation.targetContext.resources.getQuantityString(
            R.plurals.achievement_streak_days,
            event.streakLength,
            event.streakLength
        )
        val streakShown = device.wait(Until.hasObject(By.text(streakText)), 2000)
        assertTrue("streak label missing", streakShown)

        val habitText = instrumentation.targetContext.getString(R.string.achievement_habit_label, event.habitName)
        val habitShown = device.wait(Until.hasObject(By.text(habitText)), 2000)
        assertTrue("habit label missing", habitShown)

        val shareButton = device.findObject(By.res("org.isoron.uhabits:id/celebrationShareButton"))
        val dismissButton = device.findObject(By.res("org.isoron.uhabits:id/celebrationDismissButton"))
        assertThat("share button missing", shareButton, notNullValue())
        assertThat("dismiss button missing", dismissButton, notNullValue())

        val activity = getCurrentActivity<ListHabitsActivity>()
        assertThat("ListHabitsActivity not resumed", activity, notNullValue())
        val lastConfetti = activity!!.rootView.lastConfettiTimestamp
        assertTrue("confetti was not triggered", lastConfetti > 0L)

        dismissButton!!.click()
        val dialogDismissed = device.wait(Until.gone(By.text(event.title)), 2000)
        assertTrue("celebration dialog remained on screen", dialogDismissed)
    }

    private inline fun <reified T : Activity> getCurrentActivity(): T? {
        val monitor = ActivityLifecycleMonitorRegistry.getInstance()
        var current: Activity? = null
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            val activities = monitor.getActivitiesInStage(Stage.RESUMED)
            current = activities.firstOrNull { it is T }
        }
        return current as? T
    }
}
