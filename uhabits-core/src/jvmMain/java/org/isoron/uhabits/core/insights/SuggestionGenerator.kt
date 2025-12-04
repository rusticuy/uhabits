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
package org.isoron.uhabits.core.insights

import org.isoron.uhabits.core.models.Habit
import org.isoron.uhabits.core.models.HabitList

class SuggestionGenerator {

    fun generateSuggestions(habitList: HabitList): List<HabitSuggestion> {
        val suggestions = mutableListOf<HabitSuggestion>()
        val activeHabits = habitList.toList().filter { !it.isArchived }

        suggestions.addAll(generateCoverageSuggestions(activeHabits))
        suggestions.addAll(generateTimingeSuggestions(activeHabits))
        suggestions.addAll(generateCategoryBasedSuggestions(activeHabits))

        return suggestions
    }

    private fun generateCoverageSuggestions(habits: List<Habit>): List<HabitSuggestion> {
        val suggestions = mutableListOf<HabitSuggestion>()

        val categories = extractCategories(habits)
        val missingCategories = listOf("Exercise", "Health", "Learning", "Mindfulness", "Productivity")
            .filter { it !in categories }

        missingCategories.take(2).forEach { category ->
            suggestions.add(
                HabitSuggestion(
                    type = SuggestionType.COVERAGE_GAP,
                    title = "Try $category",
                    description = getSuggestionForCategory(category),
                    category = category,
                    reason = "You don't have any $category habits yet",
                    confidence = 0.6
                )
            )
        }

        return suggestions
    }

    private fun generateTimingeSuggestions(habits: List<Habit>): List<HabitSuggestion> {
        val suggestions = mutableListOf<HabitSuggestion>()

        val habitsWithReminders = habits.filter { it.hasReminder() }
        val habitsWithoutReminders = habits.filter { !it.hasReminder() && !it.isArchived }

        if (habitsWithReminders.isNotEmpty() && habitsWithoutReminders.isNotEmpty()) {
            val avgSuccessWithReminders = calculateAverageSuccess(habitsWithReminders)
            
            if (avgSuccessWithReminders > 0.5) {
                habitsWithoutReminders.take(2).forEach { habit ->
                    suggestions.add(
                        HabitSuggestion(
                            type = SuggestionType.TIME_SLOT,
                            title = "Set a reminder for ${habit.name}",
                            description = "Add a daily reminder to stay consistent",
                            category = extractCategory(habit),
                            reason = "Habits with reminders show ${(avgSuccessWithReminders * 100).toInt()}% success rate",
                            confidence = 0.7
                        )
                    )
                }
            }
        }

        return suggestions
    }

    private fun generateCategoryBasedSuggestions(habits: List<Habit>): List<HabitSuggestion> {
        val suggestions = mutableListOf<HabitSuggestion>()

        val categoryCount = habits
            .map { extractCategory(it) }
            .groupingBy { it }
            .eachCount()

        val topCategory = categoryCount.maxByOrNull { it.value }?.key
        if (topCategory != null && categoryCount[topCategory]!! >= 2) {
            val relatedHabits = getRelatedHabits(topCategory)
            val existingNames = habits.map { it.name.lowercase() }

            relatedHabits
                .filter { !existingNames.contains(it.lowercase()) }
                .take(1)
                .forEach { suggestion ->
                    suggestions.add(
                        HabitSuggestion(
                            type = SuggestionType.CATEGORY_BASED,
                            title = suggestion,
                            description = "Based on your interest in $topCategory",
                            category = topCategory,
                            reason = "You have ${categoryCount[topCategory]} $topCategory habits",
                            confidence = 0.8
                        )
                    )
                }
        }

        return suggestions
    }

    private fun extractCategories(habits: List<Habit>): Set<String> {
        return habits.map { extractCategory(it) }.toSet()
    }

    private fun extractCategory(habit: Habit): String {
        val name = habit.name.lowercase()
        return when {
            name.contains("exercise") || name.contains("run") || name.contains("gym") || 
            name.contains("workout") || name.contains("walk") -> "Exercise"
            name.contains("read") || name.contains("learn") || name.contains("study") -> "Learning"
            name.contains("meditate") || name.contains("mindful") || name.contains("yoga") -> "Mindfulness"
            name.contains("water") || name.contains("sleep") || name.contains("vitamin") || 
            name.contains("health") -> "Health"
            name.contains("work") || name.contains("task") || name.contains("email") || 
            name.contains("plan") -> "Productivity"
            else -> "General"
        }
    }

    private fun calculateAverageSuccess(habits: List<Habit>): Double {
        if (habits.isEmpty()) return 0.0

        val successRates = habits.map { habit ->
            val today = org.isoron.uhabits.core.utils.DateUtils.getTodayWithOffset()
            val entries = (0 until 30).mapNotNull { i ->
                val timestamp = today.minus(i)
                val entry = habit.computedEntries.get(timestamp)
                if (entry.value == org.isoron.uhabits.core.models.Entry.UNKNOWN) null
                else {
                    if (habit.isNumerical) {
                        val value = entry.value / 1000.0
                        when (habit.targetType) {
                            org.isoron.uhabits.core.models.NumericalHabitType.AT_LEAST -> 
                                value >= habit.targetValue
                            org.isoron.uhabits.core.models.NumericalHabitType.AT_MOST -> 
                                value <= habit.targetValue
                        }
                    } else {
                        entry.value == org.isoron.uhabits.core.models.Entry.YES_MANUAL || 
                        entry.value == org.isoron.uhabits.core.models.Entry.YES_AUTO
                    }
                }
            }

            if (entries.isEmpty()) 0.0 else entries.count { it }.toDouble() / entries.size
        }

        return successRates.average()
    }

    private fun getSuggestionForCategory(category: String): String {
        return when (category) {
            "Exercise" -> "Regular physical activity improves health and mood"
            "Health" -> "Track daily health habits for better wellbeing"
            "Learning" -> "Continuous learning keeps your mind sharp"
            "Mindfulness" -> "Mindfulness practices reduce stress and improve focus"
            "Productivity" -> "Build productive habits to achieve your goals"
            else -> "Start a new habit in this area"
        }
    }

    private fun getRelatedHabits(category: String): List<String> {
        return when (category) {
            "Exercise" -> listOf("Morning stretch", "Evening walk", "Strength training")
            "Health" -> listOf("Drink 8 glasses of water", "Take vitamins", "Sleep 8 hours")
            "Learning" -> listOf("Read for 30 minutes", "Practice a language", "Learn a new skill")
            "Mindfulness" -> listOf("Morning meditation", "Gratitude journal", "Deep breathing")
            "Productivity" -> listOf("Plan tomorrow", "Review goals", "Clear inbox")
            else -> emptyList()
        }
    }
}
