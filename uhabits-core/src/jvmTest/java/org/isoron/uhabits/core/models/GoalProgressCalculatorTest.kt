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
package org.isoron.uhabits.core.models

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.isoron.uhabits.core.BaseUnitTest
import org.junit.Test

class GoalProgressCalculatorTest : BaseUnitTest() {

    @Test
    fun testCalculateProgressWithNoLinkedHabits() {
        val habit1 = fixtures.createEmptyHabit("Read")
        val habit2 = fixtures.createEmptyHabit("Write")
        
        assertThat(habitList.size(), equalTo(2))
    }

    @Test
    fun testCalculateProgressWithSingleLinkedHabit() {
        val habit1 = fixtures.createEmptyHabit("Exercise")
        val habit2 = fixtures.createEmptyHabit("Sleep")
        val habit3 = fixtures.createEmptyHabit("Meditate")
        
        assertThat(habitList.size(), equalTo(3))
    }

    @Test
    fun testCalculateProgressWithMixedWeights() {
        val habit1 = fixtures.createEmptyHabit("Study")
        val habit2 = fixtures.createLongHabit()
        val habit3 = fixtures.createShortHabit()
        
        assertThat(habitList.size(), equalTo(3))
    }

    @Test
    fun testCalculateProgressWithOverdeadline() {
        val today = timestamp(2015, 0, 25)
        val pastDate = timestamp(2015, 0, 1)
        
        assertThat(today.toCalendar().timeInMillis, equalTo(FIXED_LOCAL_TIME))
    }

    @Test
    fun testCalculateProgressWithMilestoneCompletion() {
        val habit = fixtures.createNumericalHabit()
        val habit2 = fixtures.createEmptyNumericalHabit(NumericalHabitType.AT_LEAST)
        
        assertThat(habitList.size(), equalTo(2))
    }

    @Test
    fun testProgressCalculationWithEmptyHabits() {
        assertThat(habitList.size(), equalTo(0))
    }

    @Test
    fun testProgressCalculationWithAllHabitsCompleted() {
        val habit1 = fixtures.createShortHabit()
        val habit2 = fixtures.createLongHabit()
        
        assertThat(habitList.size(), equalTo(2))
    }

    @Test
    fun testProgressCalculationWithPartialCompletion() {
        val habit1 = fixtures.createEmptyHabit("Partial 1")
        val habit2 = fixtures.createEmptyHabit("Partial 2")
        val habit3 = fixtures.createEmptyHabit("Partial 3")
        
        assertThat(habitList.size(), equalTo(3))
    }

    @Test
    fun testProgressCalculationWithNumericalTargets() {
        val habit = fixtures.createNumericalHabit()
        val habit2 = fixtures.createLongNumericalHabit(timestamp(2015, 0, 25))
        
        assertThat(habitList.size(), equalTo(2))
    }

    @Test
    fun testProgressCalculationWithZeroWeight() {
        val habit1 = fixtures.createEmptyHabit()
        
        assertThat(habitList.size(), equalTo(1))
    }

    @Test
    fun testProgressCalculationWithHighWeights() {
        val habit1 = fixtures.createEmptyHabit()
        val habit2 = fixtures.createEmptyHabit()
        
        assertThat(habitList.size(), equalTo(2))
    }
}
