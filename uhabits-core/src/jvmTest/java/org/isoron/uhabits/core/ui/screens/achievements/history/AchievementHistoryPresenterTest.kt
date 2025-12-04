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

package org.isoron.uhabits.core.ui.screens.achievements.history

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.empty
import org.hamcrest.core.IsEqual.equalTo
import org.hamcrest.core.IsNot.not
import org.isoron.uhabits.core.BaseUnitTest
import org.isoron.uhabits.core.models.achievements.AchievementDefinition
import org.isoron.uhabits.core.models.achievements.AchievementRepository
import org.isoron.uhabits.core.models.achievements.AchievementType
import org.isoron.uhabits.core.models.achievements.AchievementUnlock
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class AchievementHistoryPresenterTest : BaseUnitTest() {
    private lateinit var achievementRepository: AchievementRepository
    private lateinit var screen: AchievementHistoryPresenter.Screen
    private lateinit var presenter: AchievementHistoryPresenter
    private val testPrefs = org.mockito.kotlin.mock<org.isoron.uhabits.core.preferences.Preferences>()

    @Before
    override fun setUp() {
        super.setUp()
        screen = mock()
        testPrefs.achievementHistoryFilter = "ALL"
        testPrefs.achievementHistoryGrouping = "BY_CATEGORY"
    }

    @Test
    fun testInitialState() {
        achievementRepository = mock {
            on { getAllDefinitions() } doReturn emptyList()
            on { getAllUnlocks() } doReturn emptyList()
        }

        presenter = AchievementHistoryPresenter(achievementRepository, habitList, testPrefs, screen)
        val state = presenter.getState()

        assertThat(state.currentFilter, equalTo(AchievementFilter.ALL))
        assertThat(state.currentGrouping, equalTo(AchievementGrouping.BY_CATEGORY))
        assertThat(state.sections, empty())
    }

    @Test
    fun testFilterAll() {
        val achievement1 = AchievementDefinition(
            id = 1L,
            key = "STREAK_7",
            name = "7-Day Streak",
            description = "Achieve a 7-day streak",
            type = AchievementType.GLOBAL_STREAK,
            streakTarget = 7
        )
        val achievement2 = AchievementDefinition(
            id = 2L,
            key = "STREAK_30",
            name = "30-Day Streak",
            description = "Achieve a 30-day streak",
            type = AchievementType.GLOBAL_STREAK,
            streakTarget = 30
        )
        val unlock1 = AchievementUnlock(achievementId = 1L, unlockedAt = unixTime(2025, 0, 1))

        achievementRepository = mock {
            on { getAllDefinitions() } doReturn listOf(achievement1, achievement2)
            on { getAllUnlocks() } doReturn listOf(unlock1)
        }

        presenter = AchievementHistoryPresenter(achievementRepository, habitList, testPrefs, screen)
        presenter.setFilter(AchievementFilter.ALL)
        val state = presenter.getState()

        assertThat(state.sections[0].entries.size, equalTo(2))
        assertThat(state.currentFilter, equalTo(AchievementFilter.ALL))
    }

    @Test
    fun testFilterUnlockedOnly() {
        val achievement1 = AchievementDefinition(
            id = 1L,
            key = "STREAK_7",
            name = "7-Day Streak",
            description = "Achieve a 7-day streak",
            type = AchievementType.GLOBAL_STREAK,
            streakTarget = 7
        )
        val achievement2 = AchievementDefinition(
            id = 2L,
            key = "STREAK_30",
            name = "30-Day Streak",
            description = "Achieve a 30-day streak",
            type = AchievementType.GLOBAL_STREAK,
            streakTarget = 30
        )
        val unlock1 = AchievementUnlock(achievementId = 1L, unlockedAt = unixTime(2025, 0, 1))

        achievementRepository = mock {
            on { getAllDefinitions() } doReturn listOf(achievement1, achievement2)
            on { getAllUnlocks() } doReturn listOf(unlock1)
        }

        presenter = AchievementHistoryPresenter(achievementRepository, habitList, testPrefs, screen)
        presenter.setFilter(AchievementFilter.UNLOCKED)
        val state = presenter.getState()

        assertThat(state.sections[0].entries.size, equalTo(1))
        assertThat(state.sections[0].entries[0].isUnlocked, equalTo(true))
        assertThat(state.sections[0].entries[0].name, equalTo("7-Day Streak"))
    }

    @Test
    fun testFilterLockedOnly() {
        val achievement1 = AchievementDefinition(
            id = 1L,
            key = "STREAK_7",
            name = "7-Day Streak",
            description = "Achieve a 7-day streak",
            type = AchievementType.GLOBAL_STREAK,
            streakTarget = 7
        )
        val achievement2 = AchievementDefinition(
            id = 2L,
            key = "STREAK_30",
            name = "30-Day Streak",
            description = "Achieve a 30-day streak",
            type = AchievementType.GLOBAL_STREAK,
            streakTarget = 30
        )
        val unlock1 = AchievementUnlock(achievementId = 1L, unlockedAt = unixTime(2025, 0, 1))

        achievementRepository = mock {
            on { getAllDefinitions() } doReturn listOf(achievement1, achievement2)
            on { getAllUnlocks() } doReturn listOf(unlock1)
        }

        presenter = AchievementHistoryPresenter(achievementRepository, habitList, testPrefs, screen)
        presenter.setFilter(AchievementFilter.LOCKED)
        val state = presenter.getState()

        assertThat(state.sections[0].entries.size, equalTo(1))
        assertThat(state.sections[0].entries[0].isUnlocked, equalTo(false))
        assertThat(state.sections[0].entries[0].name, equalTo("30-Day Streak"))
    }

    @Test
    fun testGroupByCategory() {
        val habit = fixtures.createEmptyHabit("Exercise")
        habitList.add(habit)

        val globalAchievement = AchievementDefinition(
            id = 1L,
            key = "STREAK_7",
            name = "7-Day Streak",
            description = "Achieve a 7-day streak",
            type = AchievementType.GLOBAL_STREAK,
            streakTarget = 7,
            habitUuid = null
        )
        val habitAchievement = AchievementDefinition(
            id = 2L,
            key = "EXERCISE_STREAK_10",
            name = "Exercise 10-Day Streak",
            description = "Exercise 10 days in a row",
            type = AchievementType.HABIT_SPECIFIC_STREAK,
            streakTarget = 10,
            habitUuid = habit.uuid
        )

        achievementRepository = mock {
            on { getAllDefinitions() } doReturn listOf(globalAchievement, habitAchievement)
            on { getAllUnlocks() } doReturn emptyList()
        }

        presenter = AchievementHistoryPresenter(achievementRepository, habitList, testPrefs, screen)
        presenter.setGrouping(AchievementGrouping.BY_CATEGORY)
        val state = presenter.getState()

        assertThat(state.sections.size, equalTo(2))
        val categories = state.sections.map { it.title }
        assertThat(categories, containsInAnyOrder("Global", "Habit-Specific"))
    }

    @Test
    fun testGroupByTimeframe() {
        val today = unixTime(2025, 0, 25)
        val yesterday = unixTime(2025, 0, 24)
        val weekAgo = unixTime(2025, 0, 18)

        val achievement1 = AchievementDefinition(
            id = 1L,
            key = "STREAK_7",
            name = "7-Day Streak",
            description = "Achieve a 7-day streak",
            type = AchievementType.GLOBAL_STREAK,
            streakTarget = 7
        )
        val achievement2 = AchievementDefinition(
            id = 2L,
            key = "STREAK_30",
            name = "30-Day Streak",
            description = "Achieve a 30-day streak",
            type = AchievementType.GLOBAL_STREAK,
            streakTarget = 30
        )
        val achievement3 = AchievementDefinition(
            id = 3L,
            key = "STREAK_100",
            name = "100-Day Streak",
            description = "Achieve a 100-day streak",
            type = AchievementType.GLOBAL_STREAK,
            streakTarget = 100
        )

        val unlock1 = AchievementUnlock(achievementId = 1L, unlockedAt = today)
        val unlock2 = AchievementUnlock(achievementId = 2L, unlockedAt = yesterday)
        val unlock3 = AchievementUnlock(achievementId = 3L, unlockedAt = weekAgo)

        achievementRepository = mock {
            on { getAllDefinitions() } doReturn listOf(achievement1, achievement2, achievement3)
            on { getAllUnlocks() } doReturn listOf(unlock1, unlock2, unlock3)
        }

        presenter = AchievementHistoryPresenter(achievementRepository, habitList, testPrefs, screen)
        presenter.setGrouping(AchievementGrouping.BY_TIMEFRAME)
        val state = presenter.getState()

        val timeframes = state.sections.map { it.title }
        assertThat(timeframes, containsInAnyOrder("Today", "Yesterday", "This Week"))
    }

    @Test
    fun testSearchQuery() {
        val achievement1 = AchievementDefinition(
            id = 1L,
            key = "STREAK_7",
            name = "7-Day Streak",
            description = "Achieve a 7-day streak",
            type = AchievementType.GLOBAL_STREAK,
            streakTarget = 7
        )
        val achievement2 = AchievementDefinition(
            id = 2L,
            key = "COMPLETIONS_100",
            name = "100 Completions",
            description = "Complete 100 habit checks",
            type = AchievementType.TOTAL_COMPLETIONS
        )

        achievementRepository = mock {
            on { getAllDefinitions() } doReturn listOf(achievement1, achievement2)
            on { getAllUnlocks() } doReturn emptyList()
        }

        presenter = AchievementHistoryPresenter(achievementRepository, habitList, testPrefs, screen)
        presenter.setSearchQuery("streak")
        val state = presenter.getState()

        assertThat(state.sections[0].entries.size, equalTo(1))
        assertThat(state.sections[0].entries[0].name, equalTo("7-Day Streak"))
    }

    @Test
    fun testSearchQueryCaseInsensitive() {
        val achievement = AchievementDefinition(
            id = 1L,
            key = "STREAK_7",
            name = "7-Day Streak",
            description = "Achieve a 7-day streak",
            type = AchievementType.GLOBAL_STREAK,
            streakTarget = 7
        )

        achievementRepository = mock {
            on { getAllDefinitions() } doReturn listOf(achievement)
            on { getAllUnlocks() } doReturn emptyList()
        }

        presenter = AchievementHistoryPresenter(achievementRepository, habitList, testPrefs, screen)
        presenter.setSearchQuery("STREAK")
        val state = presenter.getState()

        assertThat(state.sections[0].entries.size, equalTo(1))
        assertThat(state.sections[0].entries[0].name, equalTo("7-Day Streak"))
    }

    @Test
    fun testPersistFilterPreference() {
        achievementRepository = mock {
            on { getAllDefinitions() } doReturn emptyList()
            on { getAllUnlocks() } doReturn emptyList()
        }

        presenter = AchievementHistoryPresenter(achievementRepository, habitList, testPrefs, screen)
        presenter.setFilter(AchievementFilter.UNLOCKED)

        verify(testPrefs).achievementHistoryFilter = AchievementFilter.UNLOCKED.name
    }

    @Test
    fun testPersistGroupingPreference() {
        achievementRepository = mock {
            on { getAllDefinitions() } doReturn emptyList()
            on { getAllUnlocks() } doReturn emptyList()
        }

        presenter = AchievementHistoryPresenter(achievementRepository, habitList, testPrefs, screen)
        presenter.setGrouping(AchievementGrouping.BY_TIMEFRAME)

        verify(testPrefs).achievementHistoryGrouping = AchievementGrouping.BY_TIMEFRAME.name
    }

    @Test
    fun testOnEntryClickedWithHabitAchievement() {
        val habit = fixtures.createEmptyHabit("Exercise")
        habitList.add(habit)

        val achievement = AchievementDefinition(
            id = 1L,
            key = "EXERCISE_STREAK_10",
            name = "Exercise 10-Day Streak",
            description = "Exercise 10 days in a row",
            type = AchievementType.HABIT_SPECIFIC_STREAK,
            streakTarget = 10,
            habitUuid = habit.uuid
        )

        achievementRepository = mock {
            on { getAllDefinitions() } doReturn listOf(achievement)
            on { getAllUnlocks() } doReturn emptyList()
        }

        presenter = AchievementHistoryPresenter(achievementRepository, habitList, testPrefs, screen)
        val state = presenter.getState()
        val entry = state.sections[0].entries[0]

        presenter.onEntryClicked(entry)

        verify(screen).openHabit(habit)
    }

    @Test
    fun testOnEntryClickedWithGlobalAchievement() {
        val achievement = AchievementDefinition(
            id = 1L,
            key = "STREAK_7",
            name = "7-Day Streak",
            description = "Achieve a 7-day streak",
            type = AchievementType.GLOBAL_STREAK,
            streakTarget = 7,
            habitUuid = null
        )

        achievementRepository = mock {
            on { getAllDefinitions() } doReturn listOf(achievement)
            on { getAllUnlocks() } doReturn emptyList()
        }

        presenter = AchievementHistoryPresenter(achievementRepository, habitList, testPrefs, screen)
        val state = presenter.getState()
        val entry = state.sections[0].entries[0]

        presenter.onEntryClicked(entry)

        verify(screen).openAchievementDetail(1L)
    }

    @Test
    fun testOnShareBadge() {
        val achievement = AchievementDefinition(
            id = 1L,
            key = "STREAK_7",
            name = "7-Day Streak",
            description = "Achieve a 7-day streak",
            type = AchievementType.GLOBAL_STREAK,
            streakTarget = 7
        )

        achievementRepository = mock {
            on { getAllDefinitions() } doReturn listOf(achievement)
            on { getAllUnlocks() } doReturn emptyList()
        }

        presenter = AchievementHistoryPresenter(achievementRepository, habitList, testPrefs, screen)
        val state = presenter.getState()
        val entry = state.sections[0].entries[0]

        presenter.onShareBadge(entry)

        verify(screen).shareBadge(entry)
    }

    @Test
    fun testStateIsImmutable() {
        val achievement = AchievementDefinition(
            id = 1L,
            key = "STREAK_7",
            name = "7-Day Streak",
            description = "Achieve a 7-day streak",
            type = AchievementType.GLOBAL_STREAK,
            streakTarget = 7
        )

        achievementRepository = mock {
            on { getAllDefinitions() } doReturn listOf(achievement)
            on { getAllUnlocks() } doReturn emptyList()
        }

        presenter = AchievementHistoryPresenter(achievementRepository, habitList, testPrefs, screen)
        val state1 = presenter.getState()

        presenter.setFilter(AchievementFilter.LOCKED)
        val state2 = presenter.getState()

        assertThat(state1, not(equalTo(state2)))
        assertThat(state1.currentFilter, equalTo(AchievementFilter.ALL))
        assertThat(state2.currentFilter, equalTo(AchievementFilter.LOCKED))
    }

    @Test
    fun testMultipleFiltersAndGroupings() {
        val habit = fixtures.createEmptyHabit("Exercise")
        habitList.add(habit)

        val globalAchievement1 = AchievementDefinition(
            id = 1L,
            key = "STREAK_7",
            name = "7-Day Streak",
            description = "Achieve a 7-day streak",
            type = AchievementType.GLOBAL_STREAK,
            streakTarget = 7,
            habitUuid = null
        )
        val globalAchievement2 = AchievementDefinition(
            id = 2L,
            key = "STREAK_30",
            name = "30-Day Streak",
            description = "Achieve a 30-day streak",
            type = AchievementType.GLOBAL_STREAK,
            streakTarget = 30,
            habitUuid = null
        )
        val habitAchievement = AchievementDefinition(
            id = 3L,
            key = "EXERCISE_STREAK_10",
            name = "Exercise 10-Day Streak",
            description = "Exercise 10 days in a row",
            type = AchievementType.HABIT_SPECIFIC_STREAK,
            streakTarget = 10,
            habitUuid = habit.uuid
        )

        val unlock1 = AchievementUnlock(achievementId = 1L, unlockedAt = unixTime(2025, 0, 25))
        val unlock2 = AchievementUnlock(achievementId = 2L, unlockedAt = unixTime(2025, 0, 24))

        achievementRepository = mock {
            on { getAllDefinitions() } doReturn listOf(globalAchievement1, globalAchievement2, habitAchievement)
            on { getAllUnlocks() } doReturn listOf(unlock1, unlock2)
        }

        presenter = AchievementHistoryPresenter(achievementRepository, habitList, testPrefs, screen)

        var state = presenter.getState()
        assertThat(state.sections.size, equalTo(2))

        presenter.setFilter(AchievementFilter.UNLOCKED)
        presenter.setGrouping(AchievementGrouping.BY_TIMEFRAME)
        state = presenter.getState()

        assertThat(state.currentFilter, equalTo(AchievementFilter.UNLOCKED))
        assertThat(state.currentGrouping, equalTo(AchievementGrouping.BY_TIMEFRAME))
        assertThat(state.sections.size, not(empty()))
    }
}
