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
package org.isoron.uhabits.core.ui.screens.goals

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual.equalTo
import org.isoron.uhabits.core.BaseUnitTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.clearInvocations
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class ListGoalsBehaviorTest : BaseUnitTest() {
    private val screen: ListGoalsBehavior.Screen = mock()
    private lateinit var behavior: ListGoalsBehavior

    @Before
    override fun setUp() {
        super.setUp()
        val goal1 = fixtures.createGoal("Learn Guitar")
        val goal2 = fixtures.createGoal("Read a Book", daysUntilDeadline = 15)
        val archivedGoal = fixtures.createArchivedGoal()
        goalList.add(goal1)
        goalList.add(goal2)
        goalList.add(archivedGoal)
        clearInvocations(goalList)

        behavior = ListGoalsBehavior(
            goalList,
            screen,
            commandRunner,
            mock()
        )
    }

    @Test
    fun testOnClickGoal() {
        val goal = goalList.getAll()[0]
        behavior.onClickGoal(goal)
        verify(screen).showGoalScreen(goal)
    }

    @Test
    fun testOnClickCreateGoal() {
        behavior.onClickCreateGoal()
        verify(screen).showEditGoalScreen(null)
    }

    @Test
    fun testOnDeleteGoal() {
        val goal = goalList.getAll()[0]
        val initialCount = goalList.getAll().size
        behavior.onDeleteGoal(goal)
        assertThat(goalList.getAll().size, equalTo(initialCount - 1))
    }

    @Test
    fun testSetSortOrderNewest() {
        behavior.setSortOrder(ListGoalsBehavior.SortOrder.NEWEST)
        verify(screen).updateGoalList(org.hamcrest.Matchers.any())
    }

    @Test
    fun testSetSortOrderOldest() {
        behavior.setSortOrder(ListGoalsBehavior.SortOrder.OLDEST)
        verify(screen).updateGoalList(org.hamcrest.Matchers.any())
    }

    @Test
    fun testSetSortOrderByName() {
        behavior.setSortOrder(ListGoalsBehavior.SortOrder.NAME)
        verify(screen).updateGoalList(org.hamcrest.Matchers.any())
    }

    @Test
    fun testSetShowArchived() {
        behavior.setShowArchived(true)
        verify(screen).updateGoalList(org.hamcrest.Matchers.any())
    }

    @Test
    fun testOnStartup() {
        behavior.onStartup()
        verify(screen).updateGoalList(org.hamcrest.Matchers.any())
    }

    @Test
    fun testFilteredGoalsExcludesArchived() {
        behavior.setShowArchived(false)
        val capturedGoals = mutableListOf<org.isoron.uhabits.core.models.Goal>()
        val captor = org.mockito.kotlin.argumentCaptor<List<org.isoron.uhabits.core.models.Goal>>()
        verify(screen).updateGoalList(captor.capture())
        val goals = captor.lastValue
        for (goal in goals) {
            assertThat(goal.archived, equalTo(false))
        }
    }

    @Test
    fun testArchiveGoalShowsConfirmation() {
        val goal = goalList.getAll()[0]
        behavior.onArchiveGoal(goal)
        verify(screen).showArchiveConfirmation(goal)
    }
}
