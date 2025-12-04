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

import org.isoron.uhabits.core.models.HabitList
import org.isoron.uhabits.core.models.achievements.AchievementRepository
import org.isoron.uhabits.core.preferences.Preferences
import org.isoron.uhabits.core.utils.DateUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.max
import kotlin.math.min

class AchievementHistoryPresenter(
    private val achievementRepository: AchievementRepository,
    private val habitList: HabitList,
    private val preferences: Preferences,
    private val screen: Screen
) {
    private var currentState: AchievementHistoryState = AchievementHistoryState()

    init {
        loadFilterAndGroupingFromPreferences()
        refreshState()
    }

    class PresenterFactory(
        private val achievementRepository: AchievementRepository,
        private val habitList: HabitList,
        private val preferences: Preferences
    ) {
        fun create(screen: Screen): AchievementHistoryPresenter {
            return AchievementHistoryPresenter(achievementRepository, habitList, preferences, screen)
        }
    }

    fun getState(): AchievementHistoryState = currentState

    fun setFilter(filter: AchievementFilter) {
        currentState = currentState.copy(currentFilter = filter)
        preferences.achievementHistoryFilter = filter.name
        refreshState()
    }

    fun setGrouping(grouping: AchievementGrouping) {
        currentState = currentState.copy(currentGrouping = grouping)
        preferences.achievementHistoryGrouping = grouping.name
        refreshState()
    }

    fun setSearchQuery(query: String) {
        currentState = currentState.copy(searchQuery = query)
        refreshState()
    }

    fun onEntryClicked(entry: HistoryEntryState) {
        if (entry.habitUuid != null) {
            val habit = habitList.getByUUID(entry.habitUuid)
            if (habit != null) {
                screen.openHabit(habit)
            }
        } else {
            screen.openAchievementDetail(entry.id)
        }
    }

    fun onShareBadge(entry: HistoryEntryState) {
        screen.shareBadge(entry)
    }

    private fun loadFilterAndGroupingFromPreferences() {
        val filterName = preferences.achievementHistoryFilter
        currentState = currentState.copy(
            currentFilter = try {
                AchievementFilter.valueOf(filterName)
            } catch (e: IllegalArgumentException) {
                AchievementFilter.ALL
            }
        )

        val groupingName = preferences.achievementHistoryGrouping
        currentState = currentState.copy(
            currentGrouping = try {
                AchievementGrouping.valueOf(groupingName)
            } catch (e: IllegalArgumentException) {
                AchievementGrouping.BY_CATEGORY
            }
        )
    }

    private fun refreshState() {
        val allDefinitions = achievementRepository.getAllDefinitions()
        val allUnlocks = achievementRepository.getAllUnlocks()

        val unlockedIds = allUnlocks.map { it.achievementId }.toSet()

        var entries = allDefinitions.map { definition ->
            val isUnlocked = unlockedIds.contains(definition.id)
            val unlocksForThis = allUnlocks.filter { it.achievementId == definition.id }
            val latestUnlock = unlocksForThis.maxByOrNull { it.unlockedAt }

            HistoryEntryState(
                id = definition.id ?: 0,
                name = definition.name,
                description = definition.description,
                icon = definition.icon,
                unlockedAt = latestUnlock?.unlockedAt,
                habitUuid = definition.habitUuid,
                habitName = if (definition.habitUuid != null) {
                    habitList.getByUUID(definition.habitUuid)?.name
                } else {
                    null
                },
                isUnlocked = isUnlocked,
                progress = if (isUnlocked) 100 else 0
            )
        }

        entries = filterEntries(entries)
        entries = searchEntries(entries)

        val sections = when (currentState.currentGrouping) {
            AchievementGrouping.BY_CATEGORY -> groupByCategory(entries)
            AchievementGrouping.BY_TIMEFRAME -> groupByTimeframe(entries)
        }

        currentState = currentState.copy(sections = sections)
    }

    private fun filterEntries(entries: List<HistoryEntryState>): List<HistoryEntryState> {
        return when (currentState.currentFilter) {
            AchievementFilter.ALL -> entries
            AchievementFilter.UNLOCKED -> entries.filter { it.isUnlocked }
            AchievementFilter.LOCKED -> entries.filter { !it.isUnlocked }
            AchievementFilter.IN_PROGRESS -> entries.filter { !it.isUnlocked && it.progress > 0 }
        }
    }

    private fun searchEntries(entries: List<HistoryEntryState>): List<HistoryEntryState> {
        if (currentState.searchQuery.isEmpty()) {
            return entries
        }

        val query = currentState.searchQuery.lowercase()
        return entries.filter {
            it.name.lowercase().contains(query) ||
                it.description.lowercase().contains(query) ||
                (it.habitName?.lowercase()?.contains(query) ?: false)
        }
    }

    private fun groupByCategory(entries: List<HistoryEntryState>): List<HistorySectionState> {
        val grouped = entries.groupBy { entry ->
            when {
                entry.habitUuid != null -> "Habit-Specific"
                else -> "Global"
            }
        }

        return grouped.map { (category, categoryEntries) ->
            HistorySectionState(
                title = category,
                entries = categoryEntries.sortedWith(compareBy({ !it.isUnlocked }, { it.name }))
            )
        }.sortedBy { it.title }
    }

    private fun groupByTimeframe(entries: List<HistoryEntryState>): List<HistorySectionState> {
        val today = DateUtils.getToday()
        val calendar = Calendar.getInstance()

        val grouped = entries.groupBy { entry ->
            when {
                entry.unlockedAt == null -> "Locked"
                else -> {
                    calendar.timeInMillis = entry.unlockedAt
                    val entryDate = DateUtils.getToday(calendar.time)

                    when {
                        entryDate == today -> "Today"
                        entryDate == today.minus(1) -> "Yesterday"
                        entryDate.isAfter(today.minus(7)) -> "This Week"
                        entryDate.isAfter(today.minus(30)) -> "This Month"
                        else -> "Older"
                    }
                }
            }
        }

        val timeframeOrder = listOf("Today", "Yesterday", "This Week", "This Month", "Older", "Locked")

        return grouped.map { (timeframe, timeframeEntries) ->
            HistorySectionState(
                title = timeframe,
                entries = timeframeEntries.sortedByDescending { it.unlockedAt }
            )
        }.sortedBy { timeframeOrder.indexOf(it.title) }
    }

    interface Screen {
        fun openHabit(habit: Any)
        fun openAchievementDetail(achievementId: Long)
        fun shareBadge(entry: HistoryEntryState)
    }
}
