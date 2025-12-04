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

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.isoron.uhabits.core.BaseUnitTest
import org.isoron.uhabits.core.models.Entry
import org.isoron.uhabits.core.models.Reminder
import org.isoron.uhabits.core.models.WeekdayList
import org.isoron.uhabits.core.utils.DateUtils
import org.junit.Before
import org.junit.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class SuggestionGeneratorTest : BaseUnitTest() {

    private lateinit var generator: SuggestionGenerator

    @Before
    override fun setUp() {
        super.setUp()
        generator = SuggestionGenerator()
    }

    @Test
    fun testGenerateSuggestions_emptySList() {
        val suggestions = generator.generateSuggestions(habitList)

        assertTrue(suggestions.isNotEmpty())
        
        val coverageSuggestions = suggestions.filter { it.type == SuggestionType.COVERAGE_GAP }
        assertTrue(coverageSuggestions.isNotEmpty())
    }

    @Test
    fun testGenerateSuggestions_withExerciseHabits() {
        val habit1 = fixtures.createEmptyHabit()
        habit1.name = "Morning run"
        habit1.id = 1L
        val habit2 = fixtures.createEmptyHabit()
        habit2.name = "Gym workout"
        habit2.id = 2L

        habitList.add(habit1)
        habitList.add(habit2)

        val today = DateUtils.getTodayWithOffset()
        for (i in 0 until 30) {
            habit1.originalEntries.add(Entry(today.minus(i), Entry.YES_MANUAL))
            habit2.originalEntries.add(Entry(today.minus(i), Entry.YES_MANUAL))
        }
        habit1.recompute()
        habit2.recompute()

        val suggestions = generator.generateSuggestions(habitList)

        val categoryBasedSuggestions = suggestions.filter { it.type == SuggestionType.CATEGORY_BASED }
        assertTrue(categoryBasedSuggestions.isNotEmpty())

        val exerciseSuggestion = categoryBasedSuggestions.find { it.category == "Exercise" }
        assertNotNull(exerciseSuggestion)
        assertThat(exerciseSuggestion.confidence, greaterThan(0.5))
    }

    @Test
    fun testGenerateSuggestions_coverageGaps() {
        val habit1 = fixtures.createEmptyHabit()
        habit1.name = "Read book"
        habitList.add(habit1)

        val suggestions = generator.generateSuggestions(habitList)

        val coverageSuggestions = suggestions.filter { it.type == SuggestionType.COVERAGE_GAP }
        assertTrue(coverageSuggestions.isNotEmpty())

        val categories = coverageSuggestions.map { it.category }.toSet()
        assertTrue(categories.contains("Exercise") || categories.contains("Health"))
    }

    @Test
    fun testGenerateSuggestions_timingBased() {
        val habitWithReminder = fixtures.createEmptyHabit()
        habitWithReminder.name = "Exercise"
        habitWithReminder.id = 1L
        habitWithReminder.reminder = Reminder(hour = 9, minute = 0, days = WeekdayList.EVERY_DAY)

        val habitWithoutReminder = fixtures.createEmptyHabit()
        habitWithoutReminder.name = "Read"
        habitWithoutReminder.id = 2L

        habitList.add(habitWithReminder)
        habitList.add(habitWithoutReminder)

        val today = DateUtils.getTodayWithOffset()
        for (i in 0 until 30) {
            habitWithReminder.originalEntries.add(Entry(today.minus(i), Entry.YES_MANUAL))
            habitWithoutReminder.originalEntries.add(Entry(today.minus(i), Entry.NO))
        }
        habitWithReminder.recompute()
        habitWithoutReminder.recompute()

        val suggestions = generator.generateSuggestions(habitList)

        val timingSuggestions = suggestions.filter { it.type == SuggestionType.TIME_SLOT }
        assertTrue(timingSuggestions.isNotEmpty())

        val reminderSuggestion = timingSuggestions.find { it.title.contains("Read") }
        assertNotNull(reminderSuggestion)
        assertThat(reminderSuggestion.reason, containsString("success rate"))
    }

    @Test
    fun testGenerateSuggestions_noTimingSuggestionsWithoutReminders() {
        val habit1 = fixtures.createEmptyHabit()
        habit1.name = "Exercise"
        val habit2 = fixtures.createEmptyHabit()
        habit2.name = "Read"

        habitList.add(habit1)
        habitList.add(habit2)

        val suggestions = generator.generateSuggestions(habitList)

        val timingSuggestions = suggestions.filter { it.type == SuggestionType.TIME_SLOT }
        assertTrue(timingSuggestions.isEmpty())
    }

    @Test
    fun testGenerateSuggestions_categoryDetection() {
        val exerciseHabit = fixtures.createEmptyHabit()
        exerciseHabit.name = "Morning run"
        habitList.add(exerciseHabit)

        val learningHabit = fixtures.createEmptyHabit()
        learningHabit.name = "Read technical book"
        habitList.add(learningHabit)

        val healthHabit = fixtures.createEmptyHabit()
        healthHabit.name = "Drink water"
        habitList.add(healthHabit)

        val mindfulnessHabit = fixtures.createEmptyHabit()
        mindfulnessHabit.name = "Meditate"
        habitList.add(mindfulnessHabit)

        val productivityHabit = fixtures.createEmptyHabit()
        productivityHabit.name = "Plan work tasks"
        habitList.add(productivityHabit)

        val suggestions = generator.generateSuggestions(habitList)

        val coverageSuggestions = suggestions.filter { it.type == SuggestionType.COVERAGE_GAP }
        assertTrue(coverageSuggestions.isEmpty() || coverageSuggestions.size <= 2)
    }

    @Test
    fun testGenerateSuggestions_confidenceScores() {
        val habit1 = fixtures.createEmptyHabit()
        habit1.name = "Exercise"
        val habit2 = fixtures.createEmptyHabit()
        habit2.name = "Gym"

        habitList.add(habit1)
        habitList.add(habit2)

        val suggestions = generator.generateSuggestions(habitList)

        suggestions.forEach { suggestion ->
            assertThat(suggestion.confidence, greaterThanOrEqualTo(0.0))
            assertThat(suggestion.confidence, lessThanOrEqualTo(1.0))
        }

        val categoryBasedSuggestion = suggestions.find { it.type == SuggestionType.CATEGORY_BASED }
        if (categoryBasedSuggestion != null) {
            assertThat(categoryBasedSuggestion.confidence, greaterThanOrEqualTo(0.7))
        }
    }

    @Test
    fun testGenerateSuggestions_avoidsDuplicates() {
        val habit1 = fixtures.createEmptyHabit()
        habit1.name = "Morning stretch"
        val habit2 = fixtures.createEmptyHabit()
        habit2.name = "Evening walk"

        habitList.add(habit1)
        habitList.add(habit2)

        val suggestions = generator.generateSuggestions(habitList)

        val categoryBasedSuggestions = suggestions.filter { it.type == SuggestionType.CATEGORY_BASED }
        
        categoryBasedSuggestions.forEach { suggestion ->
            assertThat(suggestion.title.lowercase(), not(containsString("stretch")))
            assertThat(suggestion.title.lowercase(), not(containsString("walk")))
        }
    }

    @Test
    fun testGenerateSuggestions_withArchivedHabits() {
        val activeHabit = fixtures.createEmptyHabit()
        activeHabit.name = "Exercise"
        activeHabit.isArchived = false

        val archivedHabit = fixtures.createEmptyHabit()
        archivedHabit.name = "Old habit"
        archivedHabit.isArchived = true

        habitList.add(activeHabit)
        habitList.add(archivedHabit)

        val suggestions = generator.generateSuggestions(habitList)

        assertTrue(suggestions.isNotEmpty())
    }

    @Test
    fun testGenerateSuggestions_limitsCoverageSuggestions() {
        val suggestions = generator.generateSuggestions(habitList)

        val coverageSuggestions = suggestions.filter { it.type == SuggestionType.COVERAGE_GAP }
        assertThat(coverageSuggestions.size, lessThanOrEqualTo(2))
    }

    @Test
    fun testGenerateSuggestions_limitsTimingSuggestions() {
        val habitWithReminder = fixtures.createEmptyHabit()
        habitWithReminder.name = "Exercise"
        habitWithReminder.reminder = Reminder(hour = 9, minute = 0, days = WeekdayList.EVERY_DAY)

        habitList.add(habitWithReminder)

        for (i in 1..5) {
            val habit = fixtures.createEmptyHabit()
            habit.name = "Habit $i"
            habitList.add(habit)
        }

        val today = DateUtils.getTodayWithOffset()
        for (i in 0 until 30) {
            habitWithReminder.originalEntries.add(Entry(today.minus(i), Entry.YES_MANUAL))
        }
        habitWithReminder.recompute()

        val suggestions = generator.generateSuggestions(habitList)

        val timingSuggestions = suggestions.filter { it.type == SuggestionType.TIME_SLOT }
        assertThat(timingSuggestions.size, lessThanOrEqualTo(2))
    }
}
