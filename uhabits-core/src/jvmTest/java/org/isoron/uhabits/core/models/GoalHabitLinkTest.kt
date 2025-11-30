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
import org.isoron.uhabits.core.models.goals.Goal
import org.isoron.uhabits.core.models.goals.GoalHabitLink
import org.isoron.uhabits.core.models.goals.MemoryGoalList
import org.junit.Before
import org.junit.Test

class GoalHabitLinkTest : BaseUnitTest() {
    private lateinit var testGoalList: MemoryGoalList

    @Before
    override fun setUp() {
        super.setUp()
        testGoalList = MemoryGoalList()
    }

    @Test
    fun testLinkHabitToGoal() {
        val goal = Goal(name = "Learn Reading", targetValue = 100.0)
        testGoalList.add(goal)
        
        val habit1 = fixtures.createEmptyHabit("Read")
        val habit2 = fixtures.createEmptyHabit("Write")
        val habit3 = fixtures.createEmptyHabit("Think")
        
        val link1 = GoalHabitLink(goalId = goal.id, habitId = habit1.id, weight = 1.0)
        testGoalList.addHabitLink(link1)
        
        val links = testGoalList.getLinkedHabits(goal.id!!)
        assertThat(links.size, equalTo(1))
        assertThat(links[0].habitId, equalTo(habit1.id))
    }

    @Test
    fun testMultipleHabitsLinkedToSingleGoal() {
        val goal = Goal(name = "Improve Health", targetValue = 100.0)
        testGoalList.add(goal)
        
        val habit1 = fixtures.createShortHabit()
        val habit2 = fixtures.createLongHabit()
        val habit3 = fixtures.createNumericalHabit()
        
        val link1 = GoalHabitLink(goalId = goal.id, habitId = habit1.id, weight = 1.0)
        val link2 = GoalHabitLink(goalId = goal.id, habitId = habit2.id, weight = 1.0)
        val link3 = GoalHabitLink(goalId = goal.id, habitId = habit3.id, weight = 1.0)
        
        testGoalList.addHabitLink(link1)
        testGoalList.addHabitLink(link2)
        testGoalList.addHabitLink(link3)
        
        val links = testGoalList.getLinkedHabits(goal.id!!)
        assertThat(links.size, equalTo(3))
    }

    @Test
    fun testWeightedLinkBetweenHabitAndGoal() {
        val goal = Goal(name = "Weighted Goal", targetValue = 100.0)
        testGoalList.add(goal)
        
        val habit1 = fixtures.createEmptyHabit()
        val habit2 = fixtures.createEmptyHabit("Different Habit")
        
        val link1 = GoalHabitLink(goalId = goal.id, habitId = habit1.id, weight = 1.5)
        val link2 = GoalHabitLink(goalId = goal.id, habitId = habit2.id, weight = 2.0)
        
        testGoalList.addHabitLink(link1)
        testGoalList.addHabitLink(link2)
        
        val links = testGoalList.getLinkedHabits(goal.id!!)
        assertThat(links[0].weight, equalTo(1.5))
        assertThat(links[1].weight, equalTo(2.0))
    }

    @Test
    fun testRemoveLinkBetweenHabitAndGoal() {
        val goal = Goal(name = "Remove Test", targetValue = 100.0)
        testGoalList.add(goal)
        
        val habit1 = fixtures.createEmptyHabit()
        val habit2 = fixtures.createEmptyHabit("Remove Me")
        
        val link1 = GoalHabitLink(goalId = goal.id, habitId = habit1.id, weight = 1.0)
        val link2 = GoalHabitLink(goalId = goal.id, habitId = habit2.id, weight = 1.0)
        
        testGoalList.addHabitLink(link1)
        testGoalList.addHabitLink(link2)
        
        assertThat(testGoalList.getLinkedHabits(goal.id!!).size, equalTo(2))
        
        testGoalList.removeHabitLink(link2)
        
        assertThat(testGoalList.getLinkedHabits(goal.id!!).size, equalTo(1))
    }

    @Test
    fun testQueryGoalsByHabit() {
        val goal1 = Goal(name = "Goal 1", targetValue = 100.0)
        val goal2 = Goal(name = "Goal 2", targetValue = 100.0)
        testGoalList.add(goal1)
        testGoalList.add(goal2)
        
        val habit = fixtures.createEmptyHabit("Track Progress")
        
        val link1 = GoalHabitLink(goalId = goal1.id, habitId = habit.id, weight = 1.0)
        val link2 = GoalHabitLink(goalId = goal2.id, habitId = habit.id, weight = 1.0)
        
        testGoalList.addHabitLink(link1)
        testGoalList.addHabitLink(link2)
        
        val goal1Links = testGoalList.getLinkedHabits(goal1.id!!)
        val goal2Links = testGoalList.getLinkedHabits(goal2.id!!)
        
        assertThat(goal1Links.size, equalTo(1))
        assertThat(goal2Links.size, equalTo(1))
    }
}
