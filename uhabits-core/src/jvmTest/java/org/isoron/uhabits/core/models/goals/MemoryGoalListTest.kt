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
package org.isoron.uhabits.core.models.goals

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.isoron.uhabits.core.BaseUnitTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertTrue

class MemoryGoalListTest : BaseUnitTest() {
    private lateinit var goalList: GoalList

    @Before
    override fun setUp() {
        super.setUp()
        goalList = MemoryGoalList()
    }

    @Test
    fun testAddGoal() {
        val goal = Goal(name = "Test Goal", targetValue = 100.0)
        assertTrue(goalList.isEmpty)
        goalList.add(goal)
        assertThat(goalList.size(), equalTo(1))
        assertThat(goal.id != null, equalTo(true))
    }

    @Test
    fun testGetById() {
        val goal = Goal(name = "Test Goal", targetValue = 100.0)
        goalList.add(goal)
        val retrieved = goalList.getById(goal.id!!)
        assertThat(retrieved?.name, equalTo("Test Goal"))
    }

    @Test
    fun testGetByIdNonExistent() {
        val retrieved = goalList.getById(999L)
        assertThat(retrieved, nullValue())
    }

    @Test
    fun testRemoveGoal() {
        val goal = Goal(name = "Test Goal", targetValue = 100.0)
        goalList.add(goal)
        assertThat(goalList.size(), equalTo(1))
        goalList.remove(goal)
        assertThat(goalList.isEmpty, equalTo(true))
    }

    @Test
    fun testRemoveGoalAlsoRemovesLinks() {
        val goal = Goal(name = "Test Goal", targetValue = 100.0)
        goalList.add(goal)
        val goalId = goal.id ?: throw IllegalStateException()

        val habit = fixtures.createEmptyHabit()
        habitList.add(habit)
        val habitId = habit.id ?: throw IllegalStateException()

        val link = GoalHabitLink(goalId = goalId, habitId = habitId)
        goalList.addHabitLink(link)

        assertThat(goalList.getLinkedHabits(goalId).size, equalTo(1))

        goalList.remove(goal)

        assertThat(goalList.getLinkedHabits(goalId).size, equalTo(0))
    }

    @Test
    fun testRemoveGoalAlsoRemovesMilestones() {
        val goal = Goal(name = "Test Goal", targetValue = 100.0)
        goalList.add(goal)
        val goalId = goal.id ?: throw IllegalStateException()

        val milestone = GoalMilestone(goalId = goalId, targetPercentage = 50.0)
        goalList.addMilestone(milestone)

        assertThat(goalList.getMilestones(goalId).size, equalTo(1))

        goalList.remove(goal)

        assertThat(goalList.getMilestones(goalId).size, equalTo(0))
    }

    @Test
    fun testUpdateGoal() {
        val goal = Goal(name = "Original", targetValue = 50.0)
        goalList.add(goal)

        goal.name = "Modified"
        goalList.update(goal)

        val retrieved = goalList.getById(goal.id!!)
        assertThat(retrieved?.name, equalTo("Modified"))
    }

    @Test
    fun testGetByUUID() {
        val goal = Goal(name = "Test Goal", targetValue = 100.0)
        goalList.add(goal)

        val retrieved = goalList.getByUUID(goal.uuid)
        assertThat(retrieved?.name, equalTo("Test Goal"))
    }

    @Test
    fun testIndexOf() {
        val goal1 = Goal(name = "Goal 1", targetValue = 50.0)
        val goal2 = Goal(name = "Goal 2", targetValue = 75.0)
        goalList.add(goal1)
        goalList.add(goal2)

        assertThat(goalList.indexOf(goal1), equalTo(0))
        assertThat(goalList.indexOf(goal2), equalTo(1))
    }

    @Test
    fun testAddHabitLink() {
        val goal = Goal(name = "Test Goal", targetValue = 100.0)
        goalList.add(goal)
        val goalId = goal.id ?: throw IllegalStateException()

        val habit = fixtures.createEmptyHabit()
        habitList.add(habit)
        val habitId = habit.id ?: throw IllegalStateException()

        val link = GoalHabitLink(goalId = goalId, habitId = habitId, weight = 1.5)
        goalList.addHabitLink(link)

        val links = goalList.getLinkedHabits(goalId)
        assertThat(links.size, equalTo(1))
        assertThat(links[0].weight, equalTo(1.5))
    }

    @Test
    fun testRemoveHabitLink() {
        val goal = Goal(name = "Test Goal", targetValue = 100.0)
        goalList.add(goal)
        val goalId = goal.id ?: throw IllegalStateException()

        val habit = fixtures.createEmptyHabit()
        habitList.add(habit)
        val habitId = habit.id ?: throw IllegalStateException()

        val link = GoalHabitLink(goalId = goalId, habitId = habitId)
        goalList.addHabitLink(link)
        assertThat(goalList.getLinkedHabits(goalId).size, equalTo(1))

        goalList.removeHabitLink(link)
        assertThat(goalList.getLinkedHabits(goalId).size, equalTo(0))
    }

    @Test
    fun testAddMilestone() {
        val goal = Goal(name = "Test Goal", targetValue = 100.0)
        goalList.add(goal)
        val goalId = goal.id ?: throw IllegalStateException()

        val milestone = GoalMilestone(goalId = goalId, targetPercentage = 50.0)
        goalList.addMilestone(milestone)

        val milestones = goalList.getMilestones(goalId)
        assertThat(milestones.size, equalTo(1))
        assertThat(milestones[0].targetPercentage, equalTo(50.0))
    }

    @Test
    fun testRemoveMilestone() {
        val goal = Goal(name = "Test Goal", targetValue = 100.0)
        goalList.add(goal)
        val goalId = goal.id ?: throw IllegalStateException()

        val milestone = GoalMilestone(goalId = goalId, targetPercentage = 50.0)
        goalList.addMilestone(milestone)
        assertThat(goalList.getMilestones(goalId).size, equalTo(1))

        goalList.removeMilestone(milestone)
        assertThat(goalList.getMilestones(goalId).size, equalTo(0))
    }

    @Test
    fun testUpdateMilestone() {
        val goal = Goal(name = "Test Goal", targetValue = 100.0)
        goalList.add(goal)
        val goalId = goal.id ?: throw IllegalStateException()

        val milestone = GoalMilestone(goalId = goalId, targetPercentage = 50.0, isComplete = false)
        goalList.addMilestone(milestone)

        milestone.isComplete = true
        goalList.updateMilestone(milestone)

        val updated = goalList.getMilestones(goalId)[0]
        assertThat(updated.isComplete, equalTo(true))
    }
}
