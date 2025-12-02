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
package org.isoron.uhabits.core.models.achievements

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.isoron.uhabits.core.BaseUnitTest
import org.isoron.uhabits.core.database.Database
import org.isoron.uhabits.core.database.Repository
import org.isoron.uhabits.core.models.sqlite.records.AchievementRecord
import org.isoron.uhabits.core.models.sqlite.records.AchievementUnlockRecord
import org.junit.Before
import org.junit.Test

class AchievementRepositoryTest : BaseUnitTest() {
    private lateinit var db: Database
    private lateinit var repository: AchievementRepository

    @Before
    override fun setUp() {
        super.setUp()
        db = buildMemoryDatabase()
        val achievementRepo = Repository(AchievementRecord::class.java, db)
        val unlockRepo = Repository(AchievementUnlockRecord::class.java, db)
        repository = AchievementRepository(achievementRepo, unlockRepo)
    }

    @Test
    fun testGetAllDefinitions() {
        val definitions = repository.getAllDefinitions()
        assertThat(definitions.size, equalTo(4))
        assertThat(definitions[0].key, equalTo("streak_7"))
        assertThat(definitions[1].key, equalTo("streak_30"))
        assertThat(definitions[2].key, equalTo("streak_100"))
        assertThat(definitions[3].key, equalTo("streak_365"))
    }

    @Test
    fun testGetDefinitionByKey() {
        val definition = repository.getDefinitionByKey("streak_7")
        assertThat(definition, notNullValue())
        assertThat(definition!!.key, equalTo("streak_7"))
        assertThat(definition.name, equalTo("7-Day Streak"))
        assertThat(definition.streakTarget, equalTo(7))
        assertThat(definition.type, equalTo(AchievementType.GLOBAL_STREAK))
    }

    @Test
    fun testGetDefinitionByKeyNotFound() {
        val definition = repository.getDefinitionByKey("nonexistent")
        assertThat(definition, nullValue())
    }

    @Test
    fun testUnlock() {
        val definition = repository.getDefinitionByKey("streak_7")!!
        assertThat(repository.isUnlocked(definition.id!!), equalTo(false))

        repository.unlock(definition.id!!)
        assertThat(repository.isUnlocked(definition.id!!), equalTo(true))
    }

    @Test
    fun testUnlockWithHabitUuid() {
        val definition = repository.getDefinitionByKey("streak_7")!!
        val habitUuid = "test-habit-123"

        assertThat(repository.isUnlocked(definition.id!!, habitUuid), equalTo(false))

        repository.unlock(definition.id!!, habitUuid)
        assertThat(repository.isUnlocked(definition.id!!, habitUuid), equalTo(true))
    }

    @Test
    fun testUnlockDoesNotDuplicate() {
        val definition = repository.getDefinitionByKey("streak_7")!!
        repository.unlock(definition.id!!)
        repository.unlock(definition.id!!)

        val unlocks = repository.getUnlocksForAchievement(definition.id!!)
        assertThat(unlocks.size, equalTo(1))
    }

    @Test
    fun testGetAllUnlocks() {
        val def1 = repository.getDefinitionByKey("streak_7")!!
        val def2 = repository.getDefinitionByKey("streak_30")!!

        repository.unlock(def1.id!!)
        repository.unlock(def2.id!!)

        val unlocks = repository.getAllUnlocks()
        assertThat(unlocks.size, equalTo(2))
    }

    @Test
    fun testGetUnlocksForAchievement() {
        val definition = repository.getDefinitionByKey("streak_7")!!
        repository.unlock(definition.id!!, "habit-1")
        repository.unlock(definition.id!!, "habit-2")

        val unlocks = repository.getUnlocksForAchievement(definition.id!!)
        assertThat(unlocks.size, equalTo(2))
    }

    @Test
    fun testIsUnlockedDifferentiatesByHabitUuid() {
        val definition = repository.getDefinitionByKey("streak_7")!!
        repository.unlock(definition.id!!, "habit-1")

        assertThat(repository.isUnlocked(definition.id!!, "habit-1"), equalTo(true))
        assertThat(repository.isUnlocked(definition.id!!, "habit-2"), equalTo(false))
    }

    @Test
    fun testSaveDefinition() {
        val newDefinition = AchievementDefinition(
            key = "custom_achievement",
            name = "Custom Achievement",
            description = "A custom achievement for testing",
            type = AchievementType.HABIT_COUNT,
            streakTarget = null,
            habitUuid = null,
            icon = "custom_icon"
        )

        repository.saveDefinition(newDefinition)

        val retrieved = repository.getDefinitionByKey("custom_achievement")
        assertThat(retrieved, notNullValue())
        assertThat(retrieved!!.name, equalTo("Custom Achievement"))
        assertThat(retrieved.type, equalTo(AchievementType.HABIT_COUNT))
    }

    @Test
    fun testUnlockWithTimestamp() {
        val definition = repository.getDefinitionByKey("streak_7")!!
        val timestamp = 123456789L

        repository.unlock(definition.id!!, null, timestamp)

        val unlocks = repository.getUnlocksForAchievement(definition.id!!)
        assertThat(unlocks.size, equalTo(1))
        assertThat(unlocks[0].unlockedAt, equalTo(timestamp))
    }
}
