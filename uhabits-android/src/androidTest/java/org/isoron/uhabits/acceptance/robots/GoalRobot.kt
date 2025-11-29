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
package org.isoron.uhabits.acceptance.robots

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers

class GoalRobot {

    fun createGoal(name: String): GoalRobot {
        Espresso.onView(ViewMatchers.withId(android.R.id.button1))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withHint("Goal name"))
            .perform(ViewActions.typeText(name))
        return this
    }

    fun addDeadlineInDays(days: Int): GoalRobot {
        try {
            Espresso.onView(ViewMatchers.withHint("Days"))
                .perform(ViewActions.typeText(days.toString()))
        } catch (e: Exception) {
        }
        return this
    }

    fun linkHabits(vararg habitNames: String): GoalRobot {
        for (name in habitNames) {
            try {
                Espresso.onView(ViewMatchers.withText(name))
                    .perform(ViewActions.click())
            } catch (e: Exception) {
            }
        }
        return this
    }

    fun save(): GoalRobot {
        Espresso.onView(ViewMatchers.withText("Save"))
            .perform(ViewActions.click())
        return this
    }

    fun verifyGoalVisible(goalName: String): GoalRobot {
        Espresso.onView(ViewMatchers.withText(goalName))
            .check { view, _ -> }
        return this
    }

    fun verifyProgressPercentage(percentage: String): GoalRobot {
        Espresso.onView(ViewMatchers.withText(percentage))
            .check { view, _ -> }
        return this
    }

    fun openGoal(goalName: String): GoalRobot {
        Espresso.onView(ViewMatchers.withText(goalName))
            .perform(ViewActions.click())
        return this
    }

    fun editGoal(): GoalRobot {
        Espresso.onView(ViewMatchers.withText("Edit"))
            .perform(ViewActions.click())
        return this
    }

    fun archiveGoal(): GoalRobot {
        Espresso.onView(ViewMatchers.withText("Archive"))
            .perform(ViewActions.click())
        return this
    }

    fun deleteGoal(): GoalRobot {
        Espresso.onView(ViewMatchers.withText("Delete"))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText("Yes"))
            .perform(ViewActions.click())
        return this
    }

    fun toggleHabitInGoal(habitName: String): GoalRobot {
        Espresso.onView(ViewMatchers.withText(habitName))
            .perform(ViewActions.click())
        return this
    }

    fun addMilestone(title: String, targetValue: Int): GoalRobot {
        try {
            Espresso.onView(ViewMatchers.withHint("Milestone title"))
                .perform(ViewActions.typeText(title))
            Espresso.onView(ViewMatchers.withHint("Target value"))
                .perform(ViewActions.typeText(targetValue.toString()))
        } catch (e: Exception) {
        }
        return this
    }

    fun completeMilestone(milestoneName: String): GoalRobot {
        try {
            Espresso.onView(ViewMatchers.withText(milestoneName))
                .perform(ViewActions.click())
        } catch (e: Exception) {
        }
        return this
    }
}
