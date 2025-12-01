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
package org.isoron.uhabits.core.models.sqlite

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.isoron.uhabits.core.BaseUnitTest
import org.isoron.uhabits.core.models.goals.Goal
import org.isoron.uhabits.core.models.goals.GoalHabitLink
import org.isoron.uhabits.core.models.goals.MemoryGoalList
import org.junit.Before
import org.junit.Test

class GoalHabitLinkRepositoryTest : BaseUnitTest() {
    private lateinit var testGoalList: MemoryGoalList

    @Before
    override fun setUp() {
        super.setUp()
        testGoalList = MemoryGoalList()
    }

    @Test
    fun testSaveLinkBetweenGoalAndHabit() {
        val goal = Goal(name = "Test Goal", targetValue = 100.0)
        testGoalList.add(goal)
        
        val habit1 = fixtures.createEmptyHabit("Habit 1")
        val habit2 = fixtures.createEmptyHabit("Habit 2")
        
        val link1 = GoalHabitLink(goalId = goal.id, habitId = habit1.id, weight = 1.0)
        val link2 = GoalHabitLink(goalId = goal.id, habitId = habit2.id, weight = 1.0)
        
        testGoalList.addHabitLink(link1)
        testGoalList.addHabitLink(link2)
        
        assertThat(testGoalList.getLinkedHabits(goal.id!!).size, equalTo(2))
    }

    @Test
    fun testRetrieveLinksForGoal() {
        val goal = Goal(name = "Test Goal", targetValue = 100.0)
        testGoalList.add(goal)
        
        val habit1 = fixtures.createEmptyHabit("Read")
        val habit2 = fixtures.createEmptyHabit("Write")
        val habit3 = fixtures.createEmptyHabit("Think")
        
        val link1 = GoalHabitLink(goalId = goal.id, habitId = habit1.id, weight = 1.0)
        val link2 = GoalHabitLink(goalId = goal.id, habitId = habit2.id, weight = 1.0)
        val link3 = GoalHabitLink(goalId = goal.id, habitId = habit3.id, weight = 1.0)
        
        testGoalList.addHabitLink(link1)
        testGoalList.addHabitLink(link2)
        testGoalList.addHabitLink(link3)
        
        assertThat(testGoalList.getLinkedHabits(goal.id!!).size, equalTo(3))
    }

    @Test
    fun testUpdateLinkWeight() {
        val goal = Goal(name = "Test Goal", targetValue = 100.0)
        testGoalList.add(goal)
        
        val habit = fixtures.createEmptyHabit("Weighted Habit")
        
        val link = GoalHabitLink(goalId = goal.id, habitId = habit.id, weight = 1.0)
        testGoalList.addHabitLink(link)
        
        link.weight = 2.5
        testGoalList.updateHabitLink(link)
        
        val updated = testGoalList.getLinkedHabits(goal.id!!)[0]
        assertThat(updated.weight, equalTo(2.5))
    }

    @Test
    fun testDeleteLink() {
        val goal = Goal(name = "Test Goal", targetValue = 100.0)
        testGoalList.add(goal)
        
        val habit1 = fixtures.createEmptyHabit()
        val habit2 = fixtures.createEmptyHabit()
        
        val link1 = GoalHabitLink(goalId = goal.id, habitId = habit1.id, weight = 1.0)
        val link2 = GoalHabitLink(goalId = goal.id, habitId = habit2.id, weight = 1.0)
        
        testGoalList.addHabitLink(link1)
        testGoalList.addHabitLink(link2)
        
        assertThat(testGoalList.getLinkedHabits(goal.id!!).size, equalTo(2))
        
        testGoalList.removeHabitLink(link1)
        
        assertThat(testGoalList.getLinkedHabits(goal.id!!).size, equalTo(1))
    }

    @Test
    fun testQueryGoalsByLinkedHabit() {
        val goal1 = Goal(name = "Goal 1", targetValue = 100.0)
        val goal2 = Goal(name = "Goal 2", targetValue = 100.0)
        testGoalList.add(goal1)
        testGoalList.add(goal2)
        
        val habit1 = fixtures.createEmptyHabit()
        val habit2 = fixtures.createShortHabit()
        val habit3 = fixtures.createLongHabit()
        
        val link1 = GoalHabitLink(goalId = goal1.id, habitId = habit1.id, weight = 1.0)
        val link2 = GoalHabitLink(goalId = goal2.id, habitId = habit2.id, weight = 1.0)
        val link3 = GoalHabitLink(goalId = goal1.id, habitId = habit3.id, weight = 1.0)
        
        testGoalList.addHabitLink(link1)
        testGoalList.addHabitLink(link2)
        testGoalList.addHabitLink(link3)
        
        assertThat(testGoalList.getLinkedHabits(goal1.id!!).size, equalTo(2))
        assertThat(testGoalList.getLinkedHabits(goal2.id!!).size, equalTo(1))
    }

    @Test
    fun testHandleMultipleLinksPerGoal() {
        val goal = Goal(name = "Multi Goal", targetValue = 100.0)
        testGoalList.add(goal)
        
        val habit1 = fixtures.createEmptyHabit("Multi 1")
        val habit2 = fixtures.createEmptyHabit("Multi 2")
        val habit3 = fixtures.createEmptyHabit("Multi 3")
        
        val link1 = GoalHabitLink(goalId = goal.id, habitId = habit1.id, weight = 1.0)
        val link2 = GoalHabitLink(goalId = goal.id, habitId = habit2.id, weight = 1.5)
        val link3 = GoalHabitLink(goalId = goal.id, habitId = habit3.id, weight = 2.0)
        
        testGoalList.addHabitLink(link1)
        testGoalList.addHabitLink(link2)
        testGoalList.addHabitLink(link3)
        
        assertThat(testGoalList.getLinkedHabits(goal.id!!).size, equalTo(3))
    }

    @Test
    fun testCascadeDeleteWhenHabitDeleted() {
        val goal = Goal(name = "Cascade Goal", targetValue = 100.0)
        testGoalList.add(goal)
        
        val habit1 = fixtures.createEmptyHabit("Cascading")
        
        val link = GoalHabitLink(goalId = goal.id, habitId = habit1.id, weight = 1.0)
        testGoalList.addHabitLink(link)
        
        assertThat(testGoalList.getLinkedHabits(goal.id!!).size, equalTo(1))
        
        testGoalList.remove(goal)
        
        val linksAfter = testGoalList.getLinkedHabits(goal.id!!)
        assertThat(linksAfter.size, equalTo(0))
    }
}
