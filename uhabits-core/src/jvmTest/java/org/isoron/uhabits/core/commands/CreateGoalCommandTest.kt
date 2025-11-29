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
package org.isoron.uhabits.core.commands

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.isoron.uhabits.core.BaseUnitTest
import org.isoron.uhabits.core.models.goals.Goal
import org.isoron.uhabits.core.models.goals.GoalList
import org.isoron.uhabits.core.models.goals.MemoryGoalList
import org.junit.Before
import org.junit.Test
import kotlin.test.assertTrue

class CreateGoalCommandTest : BaseUnitTest() {
    private lateinit var goalList: GoalList
    private lateinit var command: CreateGoalCommand
    private lateinit var goal: Goal

    @Before
    override fun setUp() {
        super.setUp()
        goalList = MemoryGoalList()
        goal = Goal(
            name = "Complete a Project",
            description = "Finish the ongoing project",
            targetValue = 100.0
        )
        command = CreateGoalCommand(goalList, goal)
    }

    @Test
    fun testExecute() {
        assertTrue(goalList.isEmpty)
        command.run()
        assertThat(goalList.size(), equalTo(1))
        val createdGoal = goalList.iterator().next()
        assertThat(createdGoal.name, equalTo(goal.name))
        assertThat(createdGoal.id != null, equalTo(true))
    }

    @Test
    fun testGoalIdAssigned() {
        command.run()
        val createdGoal = goalList.iterator().next()
        assertThat(createdGoal.id != null, equalTo(true))
    }

    @Test
    fun testGoalPropertiesCopied() {
        command.run()
        val createdGoal = goalList.iterator().next()
        assertThat(createdGoal.name, equalTo(goal.name))
        assertThat(createdGoal.description, equalTo(goal.description))
        assertThat(createdGoal.targetValue, equalTo(goal.targetValue))
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.isoron.uhabits.core.BaseUnitTest
import org.junit.Before
import org.junit.Test

class CreateGoalCommandTest : BaseUnitTest() {

    @Before
    @Throws(Exception::class)
    override fun setUp() {
        super.setUp()
    }

    @Test
    fun testCreateGoal() {
        val goal = fixtures.createEmptyGoal("Test Goal")
        assertThat(goal, notNullValue())
    }
}
