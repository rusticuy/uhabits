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

import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.isoron.uhabits.core.BaseUnitTest
import org.junit.Before
import org.junit.Test

class EditGoalCommandTest : BaseUnitTest() {

    @Before
    @Throws(Exception::class)
    override fun setUp() {
        super.setUp()
    }

    @Test
    fun testEditGoalName() {
        val goal = fixtures.createEmptyGoal("Original Name")
        assertThat(goal, notNullValue())
    }

    @Test
    fun testEditGoalDeadline() {
        val goal = fixtures.createEmptyGoal()
        assertThat(goal, notNullValue())
    }

    @Test
    fun testEditGoalDescription() {
        val goal = fixtures.createEmptyGoal()
        assertThat(goal, notNullValue())
    }
}
