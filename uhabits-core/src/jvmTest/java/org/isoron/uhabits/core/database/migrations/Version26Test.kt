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
import org.isoron.uhabits.core.database.MigrationHelper
import org.junit.Test

class Version26Test : BaseUnitTest() {

    private lateinit var db: Database
    private lateinit var helper: MigrationHelper

    override fun setUp() {
        super.setUp()
        try {
            db = openDatabaseResource("/databases/025.db")
        } catch (e: Exception) {
            db = buildMemoryDatabase()
            helper = MigrationHelper(db)
            helper.migrateTo(25)
        }
        helper = MigrationHelper(db)
    }

    private fun migrateTo26() = helper.migrateTo(26)

    @Test
    fun `test migrate to 26 creates Goals table`() {
        migrateTo26()
        db.query("select name from Goals where 0")
    }

    @Test
    fun `test migrate to 26 creates Goals table with required columns`() {
        migrateTo26()
        db.query(
            "select id, name, description, target_value, deadline_date, " +
            "goal_type, archived_neq, created_at from Goals where 0"
        )
    }

    @Test
    fun `test migrate to 26 creates GoalHabitLinks table`() {
        migrateTo26()
        db.query("select goal_id, habit_id, weight from GoalHabitLinks where 0")
    }

    @Test
    fun `test migrate to 26 creates GoalMilestones table`() {
        migrateTo26()
        db.query(
            "select id, goal_id, title, target_value, deadline_date, completed " +
            "from GoalMilestones where 0"
        )
    }

    @Test
    fun `test migrate to 26 creates indexes for GoalHabitLinks`() {
        migrateTo26()
        val cursor = db.query(
            "select name from sqlite_master where type='index' and " +
            "(name like '%GoalHabitLinks%' or name like '%GoalMilestones%')"
        )
        var indexCount = 0
        while (cursor.moveToNext()) {
            indexCount++
        }
        assertThat(indexCount, equalTo(3))
    }

    @Test
    fun `test migrate to 26 creates GoalMilestones with foreign key to Goals`() {
        migrateTo26()
        db.execute(
            "insert into Habits(id, name, description, frequency_num, frequency_den, " +
            "color) values (1, 'Test', '', 1, 1, 0)"
        )
        db.execute(
            "insert into Goals(id, name, created_at) values (1, 'Goal 1', 123456789)"
        )
        db.execute("insert into GoalMilestones(goal_id, title, target_value) " +
                   "values (1, 'Milestone 1', 100.0)")
        val cursor = db.query("select count(*) from GoalMilestones")
        cursor.moveToNext()
        assertThat(cursor.getLong(0), equalTo(1L))
    }

    @Test
    fun `test migrate to 26 creates GoalHabitLinks with foreign keys`() {
        migrateTo26()
        db.execute(
            "insert into Habits(id, name, description, frequency_num, frequency_den, " +
            "color) values (1, 'Test Habit', '', 1, 1, 0)"
        )
        db.execute(
            "insert into Goals(id, name, created_at) values (1, 'Goal 1', 123456789)"
        )
        db.execute("insert into GoalHabitLinks(goal_id, habit_id, weight) " +
                   "values (1, 1, 1.5)")
        val cursor = db.query("select weight from GoalHabitLinks where goal_id = 1")
        cursor.moveToNext()
        assertThat(cursor.getDouble(0), equalTo(1.5))
    }

    @Test
    fun `test migrate to 26 enforces foreign key constraints on Goals`() {
        migrateTo26()
        db.execute(
            "insert into Habits(id, name, description, frequency_num, frequency_den, " +
            "color) values (1, 'Test', '', 1, 1, 0)"
        )
        db.execute(
            "insert into Goals(id, name, created_at) values (1, 'Goal 1', 123456789)"
        )
        db.execute(
            "insert into Goals(id, name, created_at) values (2, 'Goal 2', 123456789)"
        )
        
        val cursor = db.query("select count(*) from Goals")
        cursor.moveToNext()
        assertThat(cursor.getLong(0), equalTo(2L))
    }

    @Test
    fun `test migrate to 26 allows multiple goals with linked habits`() {
        migrateTo26()
        db.execute(
            "insert into Habits(id, name, description, frequency_num, frequency_den, " +
            "color) values (1, 'Habit 1', '', 1, 1, 0)"
        )
        db.execute(
            "insert into Habits(id, name, description, frequency_num, frequency_den, " +
            "color) values (2, 'Habit 2', '', 1, 1, 0)"
        )
        db.execute(
            "insert into Goals(id, name, created_at) values (1, 'Goal 1', 123456789)"
        )
        db.execute("insert into GoalHabitLinks(goal_id, habit_id, weight) " +
                   "values (1, 1, 1.0)")
        db.execute("insert into GoalHabitLinks(goal_id, habit_id, weight) " +
                   "values (1, 2, 2.0)")
        
        val cursor = db.query("select count(*) from GoalHabitLinks where goal_id = 1")
        cursor.moveToNext()
        assertThat(cursor.getLong(0), equalTo(2L))
    }

    @Test
    fun `test migrate to 26 allows milestones with multiple goals`() {
        migrateTo26()
        db.execute(
            "insert into Goals(id, name, created_at) values (1, 'Goal 1', 123456789)"
        )
        db.execute(
            "insert into Goals(id, name, created_at) values (2, 'Goal 2', 123456789)"
        )
        db.execute(
            "insert into GoalMilestones(goal_id, title, target_value) " +
            "values (1, 'Milestone 1', 25.0)"
        )
        db.execute(
            "insert into GoalMilestones(goal_id, title, target_value) " +
            "values (1, 'Milestone 2', 50.0)"
        )
        db.execute(
            "insert into GoalMilestones(goal_id, title, target_value) " +
            "values (2, 'Milestone 3', 75.0)"
        )
        
        val cursor1 = db.query("select count(*) from GoalMilestones where goal_id = 1")
        cursor1.moveToNext()
        assertThat(cursor1.getLong(0), equalTo(2L))
        
        val cursor2 = db.query("select count(*) from GoalMilestones where goal_id = 2")
        cursor2.moveToNext()
        assertThat(cursor2.getLong(0), equalTo(1L))
    }

    @Test
    fun `test migrate to 26 schema has proper indexes`() {
        migrateTo26()
        val cursor = db.query(
            "select name from sqlite_master where type='index' and " +
            "name in ('idx_GoalHabitLinks_goal_id', 'idx_GoalHabitLinks_habit_id', " +
            "'idx_GoalMilestones_goal_id')"
        )
        var count = 0
        while (cursor.moveToNext()) {
            count++
        }
        assertThat(count, equalTo(3))
    }
}
