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
import org.isoron.uhabits.core.models.NumericalHabitType
import org.isoron.uhabits.core.utils.DateUtils
import org.junit.Before
import org.junit.Test
import kotlin.math.abs
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CorrelationAnalyzerTest : BaseUnitTest() {

    private lateinit var analyzer: CorrelationAnalyzer

    @Before
    override fun setUp() {
        super.setUp()
        analyzer = CorrelationAnalyzer()
    }

    @Test
    fun testCalculateCorrelation_perfectPositiveCorrelation() {
        val habit1 = fixtures.createEmptyHabit()
        habit1.name = "Exercise"
        val habit2 = fixtures.createEmptyHabit()
        habit2.name = "Sleep Well"
        val today = DateUtils.getTodayWithOffset()

        for (i in 0 until 50) {
            val value = if (i % 2 == 0) Entry.YES_MANUAL else Entry.NO
            habit1.originalEntries.add(Entry(today.minus(i), value))
            habit2.originalEntries.add(Entry(today.minus(i), value))
        }
        habit1.recompute()
        habit2.recompute()

        val correlation = analyzer.calculateCorrelation(habit1, habit2)

        assertNotNull(correlation)
        assertTrue(correlation.coefficient > 0.95)
        assertThat(correlation.sampleSize, greaterThanOrEqualTo(40))
        assertThat(correlation.strength, equalTo(CorrelationResult.CorrelationStrength.STRONG))
    }

    @Test
    fun testCalculateCorrelation_perfectNegativeCorrelation() {
        val habit1 = fixtures.createEmptyHabit()
        habit1.name = "Exercise"
        val habit2 = fixtures.createEmptyHabit()
        habit2.name = "Skip Workout"
        val today = DateUtils.getTodayWithOffset()

        for (i in 0 until 50) {
            val value1 = if (i % 2 == 0) Entry.YES_MANUAL else Entry.NO
            val value2 = if (i % 2 == 0) Entry.NO else Entry.YES_MANUAL
            habit1.originalEntries.add(Entry(today.minus(i), value1))
            habit2.originalEntries.add(Entry(today.minus(i), value2))
        }
        habit1.recompute()
        habit2.recompute()

        val correlation = analyzer.calculateCorrelation(habit1, habit2)

        assertNotNull(correlation)
        assertTrue(correlation.coefficient < -0.95)
        assertThat(correlation.strength, equalTo(CorrelationResult.CorrelationStrength.STRONG))
    }

    @Test
    fun testCalculateCorrelation_noCorrelation() {
        val habit1 = fixtures.createEmptyHabit()
        habit1.name = "Exercise"
        val habit2 = fixtures.createEmptyHabit()
        habit2.name = "Read"
        val today = DateUtils.getTodayWithOffset()

        for (i in 0 until 50) {
            val value1 = if (i % 2 == 0) Entry.YES_MANUAL else Entry.NO
            val value2 = if (i % 3 == 0) Entry.YES_MANUAL else Entry.NO
            habit1.originalEntries.add(Entry(today.minus(i), value1))
            habit2.originalEntries.add(Entry(today.minus(i), value2))
        }
        habit1.recompute()
        habit2.recompute()

        val correlation = analyzer.calculateCorrelation(habit1, habit2)

        assertNotNull(correlation)
        assertTrue(abs(correlation.coefficient) < 0.5)
    }

    @Test
    fun testCalculateCorrelation_insufficientData() {
        val habit1 = fixtures.createEmptyHabit()
        val habit2 = fixtures.createEmptyHabit()
        val today = DateUtils.getTodayWithOffset()

        for (i in 0 until 5) {
            habit1.originalEntries.add(Entry(today.minus(i), Entry.YES_MANUAL))
            habit2.originalEntries.add(Entry(today.minus(i), Entry.YES_MANUAL))
        }
        habit1.recompute()
        habit2.recompute()

        val correlation = analyzer.calculateCorrelation(habit1, habit2)

        assertNull(correlation)
    }

    @Test
    fun testCalculateCorrelation_withNumericalHabits() {
        val habit1 = fixtures.createEmptyNumericalHabit(NumericalHabitType.AT_LEAST)
        habit1.targetValue = 5.0
        habit1.name = "Run km"
        val habit2 = fixtures.createEmptyNumericalHabit(NumericalHabitType.AT_LEAST)
        habit2.targetValue = 8.0
        habit2.name = "Sleep hours"
        val today = DateUtils.getTodayWithOffset()

        for (i in 0 until 50) {
            val value1 = (5 + i % 5) * 1000
            val value2 = (8 + i % 5) * 1000
            habit1.originalEntries.add(Entry(today.minus(i), value1))
            habit2.originalEntries.add(Entry(today.minus(i), value2))
        }
        habit1.recompute()
        habit2.recompute()

        val correlation = analyzer.calculateCorrelation(habit1, habit2)

        assertNotNull(correlation)
        assertTrue(correlation.coefficient > 0.8)
    }

    @Test
    fun testCalculateCorrelation_mixedHabitTypes() {
        val habit1 = fixtures.createEmptyHabit()
        habit1.name = "Boolean Habit"
        val habit2 = fixtures.createEmptyNumericalHabit(NumericalHabitType.AT_LEAST)
        habit2.targetValue = 5.0
        habit2.name = "Numerical Habit"
        val today = DateUtils.getTodayWithOffset()

        for (i in 0 until 50) {
            val value1 = if (i % 2 == 0) Entry.YES_MANUAL else Entry.NO
            val value2 = if (i % 2 == 0) 6000 else 3000
            habit1.originalEntries.add(Entry(today.minus(i), value1))
            habit2.originalEntries.add(Entry(today.minus(i), value2))
        }
        habit1.recompute()
        habit2.recompute()

        val correlation = analyzer.calculateCorrelation(habit1, habit2)

        assertNotNull(correlation)
        assertTrue(correlation.coefficient > 0.8)
    }

    @Test
    fun testCalculateCorrelation_allSameValues() {
        val habit1 = fixtures.createEmptyHabit()
        val habit2 = fixtures.createEmptyHabit()
        val today = DateUtils.getTodayWithOffset()

        for (i in 0 until 50) {
            habit1.originalEntries.add(Entry(today.minus(i), Entry.YES_MANUAL))
            habit2.originalEntries.add(Entry(today.minus(i), Entry.YES_MANUAL))
        }
        habit1.recompute()
        habit2.recompute()

        val correlation = analyzer.calculateCorrelation(habit1, habit2)

        assertNull(correlation)
    }

    @Test
    fun testFindCorrelations_multipleHabits() {
        val habit1 = fixtures.createEmptyHabit()
        habit1.name = "Exercise"
        habit1.id = 1L
        val habit2 = fixtures.createEmptyHabit()
        habit2.name = "Sleep"
        habit2.id = 2L
        val habit3 = fixtures.createEmptyHabit()
        habit3.name = "Read"
        habit3.id = 3L

        habitList.add(habit1)
        habitList.add(habit2)
        habitList.add(habit3)

        val today = DateUtils.getTodayWithOffset()

        for (i in 0 until 50) {
            val value = if (i % 2 == 0) Entry.YES_MANUAL else Entry.NO
            habit1.originalEntries.add(Entry(today.minus(i), value))
            habit2.originalEntries.add(Entry(today.minus(i), value))
            habit3.originalEntries.add(Entry(today.minus(i), if (i % 3 == 0) Entry.YES_MANUAL else Entry.NO))
        }

        habit1.recompute()
        habit2.recompute()
        habit3.recompute()

        val correlations = analyzer.findCorrelations(habitList, minSampleSize = 30, minCorrelation = 0.5)

        assertTrue(correlations.isNotEmpty())
        val strongCorrelation = correlations.find { 
            (it.habit1Id == 1L && it.habit2Id == 2L) || (it.habit1Id == 2L && it.habit2Id == 1L)
        }
        assertNotNull(strongCorrelation)
        assertTrue(strongCorrelation.coefficient > 0.9)
    }

    @Test
    fun testFindCorrelations_excludesArchivedHabits() {
        val habit1 = fixtures.createEmptyHabit()
        habit1.name = "Exercise"
        habit1.id = 1L
        val habit2 = fixtures.createEmptyHabit()
        habit2.name = "Sleep"
        habit2.id = 2L
        habit2.isArchived = true

        habitList.add(habit1)
        habitList.add(habit2)

        val today = DateUtils.getTodayWithOffset()

        for (i in 0 until 50) {
            val value = Entry.YES_MANUAL
            habit1.originalEntries.add(Entry(today.minus(i), value))
            habit2.originalEntries.add(Entry(today.minus(i), value))
        }

        habit1.recompute()
        habit2.recompute()

        val correlations = analyzer.findCorrelations(habitList)

        assertTrue(correlations.isEmpty())
    }

    @Test
    fun testGenerateCorrelationInsights() {
        val correlations = listOf(
            CorrelationResult(
                habit1Id = 1L,
                habit2Id = 2L,
                habit1Name = "Exercise",
                habit2Name = "Sleep",
                coefficient = 0.8,
                sampleSize = 50,
                strength = CorrelationResult.CorrelationStrength.STRONG
            ),
            CorrelationResult(
                habit1Id = 3L,
                habit2Id = 4L,
                habit1Name = "Coffee",
                habit2Name = "Sleep",
                coefficient = -0.6,
                sampleSize = 45,
                strength = CorrelationResult.CorrelationStrength.MODERATE
            )
        )

        val insights = analyzer.generateCorrelationInsights(correlations)

        assertThat(insights.size, equalTo(2))

        val positiveInsight = insights[0]
        assertThat(positiveInsight.type, equalTo(InsightType.HABIT_CORRELATION))
        assertThat(positiveInsight.message, containsString("Exercise"))
        assertThat(positiveInsight.message, containsString("Sleep"))
        assertThat(positiveInsight.message, containsString("positively"))
        assertThat(positiveInsight.message, containsString("strongly"))

        val negativeInsight = insights[1]
        assertThat(negativeInsight.message, containsString("Coffee"))
        assertThat(negativeInsight.message, containsString("negatively"))
        assertThat(negativeInsight.message, containsString("moderately"))
    }

    @Test
    fun testCorrelationStrengthDetermination() {
        assertEquals(CorrelationResult.CorrelationStrength.NONE, CorrelationResult.determineStrength(0.1))
        assertEquals(CorrelationResult.CorrelationStrength.NONE, CorrelationResult.determineStrength(-0.2))
        assertEquals(CorrelationResult.CorrelationStrength.WEAK, CorrelationResult.determineStrength(0.4))
        assertEquals(CorrelationResult.CorrelationStrength.WEAK, CorrelationResult.determineStrength(-0.45))
        assertEquals(CorrelationResult.CorrelationStrength.MODERATE, CorrelationResult.determineStrength(0.6))
        assertEquals(CorrelationResult.CorrelationStrength.MODERATE, CorrelationResult.determineStrength(-0.65))
        assertEquals(CorrelationResult.CorrelationStrength.STRONG, CorrelationResult.determineStrength(0.85))
        assertEquals(CorrelationResult.CorrelationStrength.STRONG, CorrelationResult.determineStrength(-0.9))
    }
}
