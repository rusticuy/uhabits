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

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.isoron.uhabits.BaseUserInterfaceTest
import org.isoron.uhabits.acceptance.steps.CommonSteps.launchApp
import org.isoron.uhabits.acceptance.steps.CommonSteps.verifyDisplaysText
import org.isoron.uhabits.acceptance.steps.CommonSteps.verifyShowsScreen
import org.isoron.uhabits.acceptance.steps.CommonSteps.clickText
import org.isoron.uhabits.acceptance.steps.CommonSteps.pressBack
import org.isoron.uhabits.acceptance.steps.CommonSteps.verifyDoesNotDisplayText
import org.isoron.uhabits.acceptance.steps.GoalSteps.clickAddGoal
import org.isoron.uhabits.acceptance.steps.GoalSteps.selectHabitsForGoal
import org.isoron.uhabits.acceptance.steps.GoalSteps.setGoalDeadline
import org.isoron.uhabits.acceptance.steps.GoalSteps.setGoalName
import org.isoron.uhabits.acceptance.steps.GoalSteps.clickSaveGoal
import org.isoron.uhabits.acceptance.steps.GoalSteps.toggleGoalHabitCompletion
import org.isoron.uhabits.acceptance.steps.GoalSteps.verifyGoalProgressUpdated
import org.isoron.uhabits.acceptance.steps.GoalSteps.clickEditGoal
import org.isoron.uhabits.acceptance.steps.GoalSteps.editGoalMilestone
import org.isoron.uhabits.acceptance.steps.GoalSteps.clickArchiveGoal
import org.isoron.uhabits.acceptance.steps.GoalSteps.clickDeleteGoal
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class GoalsTest : BaseUserInterfaceTest() {

    @Test
    @Throws(Exception::class)
    fun shouldCreateGoalWithDeadline() {
        launchApp()
        verifyDisplaysText("Track time")
        clickAddGoal()
        setGoalName("Complete Reading Challenge")
        setGoalDeadline(30)
        clickSaveGoal()
        verifyDisplaysText("Complete Reading Challenge")
    }

    @Test
    @Throws(Exception::class)
    fun shouldLinkExistingHabitsToGoal() {
        launchApp()
        verifyDisplaysText("Track time")
        clickAddGoal()
        setGoalName("Achieve Fitness Goals")
        selectHabitsForGoal(listOf("Track time"))
        setGoalDeadline(45)
        clickSaveGoal()
        verifyDisplaysText("Achieve Fitness Goals")
    }

    @Test
    @Throws(Exception::class)
    fun shouldDisplayGoalProgressCorrectly() {
        launchApp()
        clickText("Track time")
        verifyGoalProgressUpdated("0%")
    }

    @Test
    @Throws(Exception::class)
    fun shouldUpdateGoalProgressWhenHabitToggled() {
        launchApp()
        clickAddGoal()
        setGoalName("Master Programming")
        selectHabitsForGoal(listOf("Track time"))
        clickSaveGoal()
        pressBack()
        toggleGoalHabitCompletion("Track time")
        verifyGoalProgressUpdated("50%")
    }

    @Test
    @Throws(Exception::class)
    fun shouldEditGoalMilestones() {
        launchApp()
        clickAddGoal()
        setGoalName("Learn a Language")
        editGoalMilestone(1, "Complete Basics", 25)
        editGoalMilestone(2, "Intermediate Level", 50)
        clickSaveGoal()
        verifyDisplaysText("Learn a Language")
    }

    @Test
    @Throws(Exception::class)
    fun shouldArchiveGoal() {
        launchApp()
        clickAddGoal()
        setGoalName("Write Novel")
        clickSaveGoal()
        pressBack()
        clickEditGoal("Write Novel")
        clickArchiveGoal()
        verifyDoesNotDisplayText("Write Novel")
    }

    @Test
    @Throws(Exception::class)
    fun shouldDeleteGoal() {
        launchApp()
        clickAddGoal()
        setGoalName("Travel Planning")
        clickSaveGoal()
        pressBack()
        clickEditGoal("Travel Planning")
        clickDeleteGoal()
        verifyDoesNotDisplayText("Travel Planning")
    }

    @Test
    @Throws(Exception::class)
    fun shouldHandleMultipleLinkedHabits() {
        launchApp()
        clickAddGoal()
        setGoalName("Wellness Program")
        selectHabitsForGoal(listOf("Track time", "Wake up early", "Meditate"))
        clickSaveGoal()
        verifyDisplaysText("Wellness Program")
    }

    @Test
    @Throws(Exception::class)
    fun shouldCalculateWeightedProgress() {
        launchApp()
        clickAddGoal()
        setGoalName("Balanced Living")
        selectHabitsForGoal(listOf("Track time", "Wake up early"))
        clickSaveGoal()
        pressBack()
        toggleGoalHabitCompletion("Track time")
        toggleGoalHabitCompletion("Wake up early")
        verifyGoalProgressUpdated("100%")
    }

    @Test
    @Throws(Exception::class)
    fun shouldHandleOverdueDeadline() {
        launchApp()
        clickAddGoal()
        setGoalName("Past Deadline Goal")
        setGoalDeadline(-5)
        clickSaveGoal()
        pressBack()
        verifyDisplaysText("Past Deadline Goal")
    }
}
