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
import org.hamcrest.MatcherAssert.assertThat
import org.isoron.uhabits.core.BaseUnitTest
import org.isoron.uhabits.core.database.Database
import org.isoron.uhabits.core.database.Repository
import org.isoron.uhabits.core.models.sqlite.records.AchievementRecord
import org.isoron.uhabits.core.models.sqlite.records.AchievementUnlockRecord
import org.junit.Before
import org.junit.Test

class AchievementStoreTest : BaseUnitTest() {
    private lateinit var db: Database
    private lateinit var store: AchievementStore

    @Before
    override fun setUp() {
        super.setUp()
        db = buildMemoryDatabase()
        val achievementRepo = Repository(AchievementRecord::class.java, db)
        val unlockRepo = Repository(AchievementUnlockRecord::class.java, db)
        val repository = AchievementRepository(achievementRepo, unlockRepo)
        store = AchievementStore(repository)
    }

    @Test
    fun testGetDefinitions() {
        val definitions = store.getDefinitions()
        assertThat(definitions.size, equalTo(4))
        assertThat(definitions[0].key, equalTo("streak_7"))
    }

    @Test
    fun testGetDefinition() {
        val definition = store.getDefinition("streak_30")
        assertThat(definition, notNullValue())
        assertThat(definition!!.name, equalTo("30-Day Streak"))
    }

    @Test
    fun testUnlockByKey() {
        val definition = store.getDefinition("streak_7")!!
        store.unlockByKey("streak_7")

        val unlocks = store.getUnlocks()
        assertThat(unlocks.size, equalTo(1))
        assertThat(unlocks[0].achievementId, equalTo(definition.id))
    }

    @Test
    fun testUnlockByKeyWithHabitUuid() {
        val habitUuid = "test-habit-456"
        store.unlockByKey("streak_7", habitUuid)

        val unlocks = store.getUnlocks()
        assertThat(unlocks.size, equalTo(1))
        assertThat(unlocks[0].habitUuid, equalTo(habitUuid))
    }

    @Test
    fun testGetUnlocks() {
        store.unlockByKey("streak_7")
        store.unlockByKey("streak_30")

        val unlocks = store.getUnlocks()
        assertThat(unlocks.size, equalTo(2))
    }

    @Test
    fun testUnlockByKeyWithInvalidKey() {
        store.unlockByKey("nonexistent_key")
        val unlocks = store.getUnlocks()
        assertThat(unlocks.size, equalTo(0))
    }
}
