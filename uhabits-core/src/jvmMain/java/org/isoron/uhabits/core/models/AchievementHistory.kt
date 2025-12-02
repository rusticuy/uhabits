/*
 * Copyright (C) 2016-2025 Álinson uhabits Santos Xavier <git@axavier.org>
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

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.isoron.uhabits.core.utils.DateUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.SortedSet
import java.util.TreeSet

/**
 * Represents the current state of the achievement history screen.
 */
data class AchievementHistoryState(
    val achievements: List<AchievementHistoryItem> = emptyList(),
    val selectedFilter: AchievementFilter = AchievementFilter.ALL,
    val isLoading: Boolean = false,
    val isEmpty: Boolean = false
)

/**
 * Represents an item in the achievement history list.
 */
sealed class AchievementHistoryItem {
    abstract val id: String
    
    data class SectionHeader(
        val title: String,
        val count: Int
    ) : AchievementHistoryItem() {
        override val id: String = "header_$title"
    }
    
    data class AchievementEntry(
        val achievement: Achievement,
        val habit: Habit
    ) : AchievementHistoryItem() {
        override val id: String = "achievement_${achievement.uuid}"
    }
}

/**
 * Filters for achievement history.
 */
enum class AchievementFilter(val displayName: String) {
    ALL("All"),
    STREAKS("Streaks"),
    PERFECT("Perfect"),
    MILESTONES("Milestones"),
    UNSEEN("Unseen")
}

/**
 * Interface for managing achievement lists.
 */
interface AchievementList {
    fun getAll(): List<Achievement>
    fun getByHabit(habitId: Long): List<Achievement>
    fun getByType(type: AchievementType): List<Achievement>
    fun getUnseen(): List<Achievement>
    fun add(achievement: Achievement): Achievement
    fun update(achievement: Achievement)
    fun remove(achievement: Achievement)
    fun markAsSeen(achievement: Achievement)
    fun markAsCelebrated(achievement: Achievement)
    fun clear()
}

/**
 * In-memory implementation of AchievementList for testing and as a fallback.
 */
class MemoryAchievementList : AchievementList {
    private val achievements = SortedSet(compareBy<Achievement> { it.timestamp }.thenBy { it.uuid })
    
    override fun getAll(): List<Achievement> = achievements.toList()
    
    override fun getByHabit(habitId: Long): List<Achievement> = 
        achievements.filter { it.habitId == habitId }
    
    override fun getByType(type: AchievementType): List<Achievement> = 
        achievements.filter { it.type == type }
    
    override fun getUnseen(): List<Achievement> = 
        achievements.filter { !it.isSeen }
    
    override fun add(achievement: Achievement): Achievement {
        achievements.add(achievement)
        return achievement
    }
    
    override fun update(achievement: Achievement) {
        achievements.removeIf { it.uuid == achievement.uuid }
        achievements.add(achievement)
    }
    
    override fun remove(achievement: Achievement) {
        achievements.removeIf { it.uuid == achievement.uuid }
    }
    
    override fun markAsSeen(achievement: Achievement) {
        update(achievement.markAsSeen())
    }
    
    override fun markAsCelebrated(achievement: Achievement) {
        update(achievement.markAsCelebrated())
    }
    
    override fun clear() {
        achievements.clear()
    }
}

/**
 * Factory for creating AchievementHistoryPresenter instances to avoid circular dependencies.
 */
class Factory @Inject constructor(
    private val achievementList: AchievementList,
    private val habitList: HabitList
) {
    fun create(screen: AchievementHistoryPresenter.Screen): AchievementHistoryPresenter {
        return AchievementHistoryPresenter(achievementList, habitList, screen)
    }
}

/**
 * Presenter for achievement history screen.
 */
class AchievementHistoryPresenter(
    private val achievementList: AchievementList,
    private val habitList: HabitList,
    private val screen: Screen
) {
    private val _state = MutableStateFlow(AchievementHistoryState())
    val state: StateFlow<AchievementHistoryState> = _state.asStateFlow()
    
    private var currentFilter: AchievementFilter = AchievementFilter.ALL
    
    init {
        loadAchievements()
    }
    
    fun onFilterChanged(filter: AchievementFilter) {
        currentFilter = filter
        loadAchievements()
    }
    
    fun onAchievementClicked(item: AchievementHistoryItem.AchievementEntry) {
        screen.showHabitDetails(item.habit)
    }
    
    fun onRefresh() {
        loadAchievements()
    }
    
    fun markAllAsSeen() {
        val unseenAchievements = achievementList.getUnseen()
        unseenAchievements.forEach { achievementList.markAsSeen(it) }
        loadAchievements()
    }
    
    private fun loadAchievements() {
        _state.value = _state.value.copy(isLoading = true)
        
        try {
            val allAchievements = when (currentFilter) {
                AchievementFilter.ALL -> achievementList.getAll()
                AchievementFilter.STREAKS -> achievementList.getByType(AchievementType.STREAK) + 
                    achievementList.getByType(AchievementType.LONGEST_STREAK)
                AchievementFilter.PERFECT -> achievementList.getByType(AchievementType.PERFECT_WEEK) +
                    achievementList.getByType(AchievementType.PERFECT_MONTH)
                AchievementFilter.MILESTONES -> achievementList.getByType(AchievementType.TOTAL_CHECKS) +
                    achievementList.getByType(AchievementType.MILESTONE)
                AchievementFilter.UNSEEN -> achievementList.getUnseen()
            }.sortedByDescending { it.timestamp }
            
            val historyItems = mutableListOf<AchievementHistoryItem>()
            val groupedByDate = allAchievements.groupBy { achievement ->
                val formatter = org.isoron.uhabits.core.utils.DateFormats.getDialogDateFormat()
                formatter.format(achievement.timestamp.toLocalDate())
            }
            
            groupedByDate.forEach { (date, achievements) ->
                historyItems.add(
                    AchievementHistoryItem.SectionHeader(date, achievements.size)
                )
                achievements.forEach { achievement ->
                    val habit = habitList.getById(achievement.habitId)
                    if (habit != null) {
                        historyItems.add(
                            AchievementHistoryItem.AchievementEntry(achievement, habit)
                        )
                    }
                }
            }
            
            _state.value = AchievementHistoryState(
                achievements = historyItems,
                selectedFilter = currentFilter,
                isLoading = false,
                isEmpty = historyItems.isEmpty()
            )
            
        } catch (e: Exception) {
            _state.value = _state.value.copy(isLoading = false)
            screen.showError("Failed to load achievements")
        }
    }
    
    interface Screen {
        fun showHabitDetails(habit: Habit)
        fun showError(message: String)
    }
}