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
package org.isoron.uhabits.core.achievements

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.contains
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasItem
import org.isoron.uhabits.core.BaseUnitTest
import org.isoron.uhabits.core.commands.CreateRepetitionCommand
import org.isoron.uhabits.core.models.Entry
import org.isoron.uhabits.core.models.Habit
import org.isoron.uhabits.core.models.Timestamp
import org.isoron.uhabits.core.utils.DateUtils.Companion.getTodayWithOffset
import org.junit.Test

class AchievementDetectorTest : BaseUnitTest() {

    @Test
    fun `unlocks default streak milestones exactly once`() {
        val repository = InMemoryAchievementRepository()
        val detector = createDetector(repository, defaultDefinitions())
        val habit = fixtures.createEmptyHabit()
        habitList.add(habit)

        val totalDays = 370
        val start = getTodayWithOffset().minus(totalDays - 1)
        logStreak(habit, start, totalDays)

        val unlocked = detector.getUnlockedAchievements()
        assertThat(
            unlocked.map { it.definition.id },
            contains("streak_7", "streak_30", "streak_100", "streak_365")
        )
        assertThat(
            unlocked.map { it.streakLength },
            contains(7, 30, 100, 365)
        )
    }

    @Test
    fun `awards habit specific achievements only to matching habits`() {
        val habitA = fixtures.createEmptyHabit("Meditate")
        val habitB = fixtures.createEmptyHabit("Run")
        habitList.add(habitA)
        habitList.add(habitB)

        val targetedByUuid = AchievementDefinition(
            id = "meditate_five",
            title = "Meditate focus",
            description = "Maintain the meditation habit five days straight.",
            requiredStreak = 5,
            habitUuid = habitA.uuid
        )
        val targetedById = AchievementDefinition(
            id = "run_three",
            title = "Run warmup",
            description = "Run for three days in a row.",
            requiredStreak = 3,
            habitId = habitB.id
        )

        val definitions = defaultDefinitions() + targetedByUuid + targetedById
        val repository = InMemoryAchievementRepository()
        val detector = createDetector(repository, definitions)

        val today = getTodayWithOffset()
        logStreak(habitA, today.minus(10), 5)
        logStreak(habitB, today.minus(20), 3)

        val unlocked = detector.getUnlockedAchievements()
        assertThat(unlocked.size, equalTo(2))
        assertThat(unlocked.map { it.definition.id }, hasItem(targetedByUuid.id))
        assertThat(unlocked.map { it.definition.id }, hasItem(targetedById.id))
        assertThat(
            unlocked.count { it.definition.id == targetedByUuid.id && it.habitUuid == habitA.uuid },
            equalTo(1)
        )
        assertThat(
            unlocked.count { it.definition.id == targetedById.id && it.habitId == habitB.id },
            equalTo(1)
        )
    }

    private fun createDetector(
        repository: AchievementRepository,
        definitions: List<AchievementDefinition>
    ): AchievementDetector {
        val provider = object : AchievementDefinitionProvider {
            override fun getDefinitions(): List<AchievementDefinition> = definitions
        }
        return AchievementDetector(commandRunner, repository, provider).also { it.startListening() }
    }

    private fun logStreak(habit: Habit, start: Timestamp, length: Int) {
        for (offset in 0 until length) {
            val timestamp = start.plus(offset)
            val command = CreateRepetitionCommand(
                habitList = habitList,
                habit = habit,
                timestamp = timestamp,
                value = Entry.YES_MANUAL,
                notes = ""
            )
            commandRunner.run(command)
        }
    }

    private fun defaultDefinitions(): List<AchievementDefinition> {
        return listOf(
            AchievementDefinition(
                id = "streak_7",
                title = "One Week Streak",
                description = "Complete any habit for seven consecutive days.",
                requiredStreak = 7
            ),
            AchievementDefinition(
                id = "streak_30",
                title = "30-Day Momentum",
                description = "Stay consistent with any habit for a full month.",
                requiredStreak = 30
            ),
            AchievementDefinition(
                id = "streak_100",
                title = "100 Days Strong",
                description = "Reach a one-hundred-day streak on any habit.",
                requiredStreak = 100
            ),
            AchievementDefinition(
                id = "streak_365",
                title = "Year of Habits",
                description = "Keep a habit alive for one full year without breaking the streak.",
                requiredStreak = 365
            )
        )
    }
}
