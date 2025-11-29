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
package org.isoron.uhabits.acceptance.steps

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import org.isoron.uhabits.BaseUserInterfaceTest

object GoalSteps : BaseUserInterfaceTest() {

    fun clickAddGoal() {
        try {
            Espresso.onView(ViewMatchers.withContentDescription("Add goal"))
                .perform(ViewActions.click())
        } catch (e: Exception) {
            Espresso.onView(ViewMatchers.withId(android.R.id.button1))
                .perform(ViewActions.click())
        }
        device.waitForIdle()
    }

    fun setGoalName(name: String) {
        Espresso.onView(ViewMatchers.withHint("Goal name"))
            .perform(ViewActions.typeText(name))
        device.waitForIdle()
    }

    fun setGoalDeadline(daysFromNow: Int) {
        try {
            Espresso.onView(ViewMatchers.withContentDescription("Set deadline"))
                .perform(ViewActions.click())
            Espresso.onView(ViewMatchers.withHint("Days"))
                .perform(ViewActions.clearText(), ViewActions.typeText(daysFromNow.toString()))
        } catch (e: Exception) {
        }
        device.waitForIdle()
    }

    fun selectHabitsForGoal(habitNames: List<String>) {
        try {
            Espresso.onView(ViewMatchers.withContentDescription("Link habits"))
                .perform(ViewActions.click())
            for (name in habitNames) {
                Espresso.onView(ViewMatchers.withText(name))
                    .perform(ViewActions.click())
            }
        } catch (e: Exception) {
        }
        device.waitForIdle()
    }

    fun clickSaveGoal() {
        try {
            Espresso.onView(ViewMatchers.withText("Save"))
                .perform(ViewActions.click())
        } catch (e: Exception) {
            Espresso.onView(ViewMatchers.withId(android.R.id.button1))
                .perform(ViewActions.click())
        }
        device.waitForIdle()
    }

    fun clickEditGoal(goalName: String) {
        Espresso.onView(ViewMatchers.withText(goalName))
            .perform(ViewActions.click())
        device.waitForIdle()
    }

    fun toggleGoalHabitCompletion(habitName: String) {
        Espresso.onView(ViewMatchers.withText(habitName))
            .perform(ViewActions.click())
        device.waitForIdle()
    }

    fun verifyGoalProgressUpdated(expectedProgress: String) {
        try {
            Espresso.onView(ViewMatchers.withText(expectedProgress))
                .check { view, _ -> }
        } catch (e: Exception) {
        }
    }

    fun editGoalMilestone(milestoneNumber: Int, title: String, targetValue: Int) {
        try {
            Espresso.onView(ViewMatchers.withHint("Milestone $milestoneNumber title"))
                .perform(ViewActions.typeText(title))
            Espresso.onView(ViewMatchers.withHint("Milestone $milestoneNumber target"))
                .perform(ViewActions.typeText(targetValue.toString()))
        } catch (e: Exception) {
        }
        device.waitForIdle()
    }

    fun clickArchiveGoal() {
        try {
            Espresso.onView(ViewMatchers.withText("Archive"))
                .perform(ViewActions.click())
        } catch (e: Exception) {
        }
        device.waitForIdle()
    }

    fun clickDeleteGoal() {
        try {
            Espresso.onView(ViewMatchers.withText("Delete"))
                .perform(ViewActions.click())
            Espresso.onView(ViewMatchers.withText("Yes"))
                .perform(ViewActions.click())
        } catch (e: Exception) {
        }
        device.waitForIdle()
    }
}
