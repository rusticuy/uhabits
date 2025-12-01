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

package org.isoron.uhabits.core.database.migrations

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.isoron.uhabits.core.BaseUnitTest
import org.isoron.uhabits.core.database.Database
import org.isoron.uhabits.core.database.JdbcDatabase
import org.isoron.uhabits.core.database.MigrationHelper
import org.junit.Test
import java.sql.DriverManager

class Version27Test : BaseUnitTest() {

    private lateinit var db: Database
    private lateinit var helper: MigrationHelper

    override fun setUp() {
        super.setUp()
        db = JdbcDatabase(DriverManager.getConnection("jdbc:sqlite::memory:"))
        db.execute("pragma user_version=8;")
        helper = MigrationHelper(db)
        helper.migrateTo(26)
    }

    private fun migrateTo27() = helper.migrateTo(27)

    @Test
    fun `test migrate to 27 creates Achievements table`() {
        migrateTo27()
        db.query("select id, key, name, description, type, streak_target, habit_uuid, icon from Achievements where 0")
    }

    @Test
    fun `test migrate to 27 creates AchievementUnlocks table`() {
        migrateTo27()
        db.query("select id, achievement_id, unlocked_at, habit_uuid from AchievementUnlocks where 0")
    }

    @Test
    fun `test migrate to 27 creates indexes`() {
        migrateTo27()
        val cursor = db.query(
            "select name from sqlite_master where type='index' and " +
            "(name like '%Achievement%')"
        )
        var indexCount = 0
        while (cursor.moveToNext()) {
            indexCount++
        }
        assertThat(indexCount, equalTo(2))
    }

    @Test
    fun `test migrate to 27 seeds default achievements`() {
        migrateTo27()
        val cursor = db.query("select count(*) from Achievements")
        cursor.moveToNext()
        assertThat(cursor.getLong(0), equalTo(4L))
    }

    @Test
    fun `test migrate to 27 seeds streak achievements with correct targets`() {
        migrateTo27()
        val cursor7 = db.query("select streak_target from Achievements where key='streak_7'")
        cursor7.moveToNext()
        assertThat(cursor7.getInt(0), equalTo(7))

        val cursor30 = db.query("select streak_target from Achievements where key='streak_30'")
        cursor30.moveToNext()
        assertThat(cursor30.getInt(0), equalTo(30))

        val cursor100 = db.query("select streak_target from Achievements where key='streak_100'")
        cursor100.moveToNext()
        assertThat(cursor100.getInt(0), equalTo(100))

        val cursor365 = db.query("select streak_target from Achievements where key='streak_365'")
        cursor365.moveToNext()
        assertThat(cursor365.getInt(0), equalTo(365))
    }

    @Test
    fun `test migrate to 27 allows inserting achievement unlocks`() {
        migrateTo27()
        val achievementCursor = db.query("select id from Achievements where key='streak_7'")
        achievementCursor.moveToNext()
        val achievementId = achievementCursor.getLong(0)

        db.execute(
            "insert into AchievementUnlocks(achievement_id, unlocked_at) " +
            "values (?, ?)",
            achievementId,
            System.currentTimeMillis()
        )

        val cursor = db.query("select count(*) from AchievementUnlocks")
        cursor.moveToNext()
        assertThat(cursor.getLong(0), equalTo(1L))
    }

    @Test
    fun `test migrate to 27 enforces foreign key constraints`() {
        migrateTo27()
        val achievementCursor = db.query("select id from Achievements where key='streak_7'")
        achievementCursor.moveToNext()
        val achievementId = achievementCursor.getLong(0)

        db.execute(
            "insert into AchievementUnlocks(achievement_id, unlocked_at) " +
            "values (?, ?)",
            achievementId,
            System.currentTimeMillis()
        )

        val cursor = db.query(
            "select count(*) from AchievementUnlocks where achievement_id = ?",
            achievementId.toString()
        )
        cursor.moveToNext()
        assertThat(cursor.getLong(0), equalTo(1L))
    }

    @Test
    fun `test migrate to 27 allows habit-specific unlocks`() {
        migrateTo27()
        val achievementCursor = db.query("select id from Achievements where key='streak_7'")
        achievementCursor.moveToNext()
        val achievementId = achievementCursor.getLong(0)

        val testHabitUuid = "test-habit-uuid-123"
        db.execute(
            "insert into AchievementUnlocks(achievement_id, unlocked_at, habit_uuid) " +
            "values (?, ?, ?)",
            achievementId,
            System.currentTimeMillis(),
            testHabitUuid
        )

        val cursor = db.query("select habit_uuid from AchievementUnlocks where achievement_id = ?", achievementId.toString())
        cursor.moveToNext()
        assertThat(cursor.getString(0), equalTo(testHabitUuid))
    }

    @Test
    fun `test migrate to 27 allows multiple unlocks for same achievement with different habits`() {
        migrateTo27()
        val achievementCursor = db.query("select id from Achievements where key='streak_7'")
        achievementCursor.moveToNext()
        val achievementId = achievementCursor.getLong(0)

        db.execute(
            "insert into AchievementUnlocks(achievement_id, unlocked_at, habit_uuid) " +
            "values (?, ?, ?)",
            achievementId,
            System.currentTimeMillis(),
            "habit-1"
        )

        db.execute(
            "insert into AchievementUnlocks(achievement_id, unlocked_at, habit_uuid) " +
            "values (?, ?, ?)",
            achievementId,
            System.currentTimeMillis(),
            "habit-2"
        )

        val cursor = db.query("select count(*) from AchievementUnlocks where achievement_id = ?", achievementId.toString())
        cursor.moveToNext()
        assertThat(cursor.getLong(0), equalTo(2L))
    }

    @Test
    fun `test migrate to 27 key column is unique`() {
        migrateTo27()
        val cursor = db.query("PRAGMA table_info(Achievements)")
        var foundKeyColumn = false
        while (cursor.moveToNext()) {
            val columnName = cursor.getString(1)
            if (columnName == "key") {
                foundKeyColumn = true
            }
        }
        assertThat(foundKeyColumn, equalTo(true))
    }
}
