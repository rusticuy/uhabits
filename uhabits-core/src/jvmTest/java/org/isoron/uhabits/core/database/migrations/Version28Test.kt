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
import org.hamcrest.CoreMatchers.hasItems
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.isoron.uhabits.core.BaseUnitTest
import org.isoron.uhabits.core.database.Database
import org.isoron.uhabits.core.database.JdbcDatabase
import org.isoron.uhabits.core.database.MigrationHelper
import org.isoron.uhabits.core.models.HabitTemplateRepository
import org.isoron.uhabits.core.models.TemplateCategory
import org.isoron.uhabits.core.models.sqlite.SQLiteHabitTemplateRepository
import org.isoron.uhabits.core.models.sqlite.SQLModelFactory
import org.junit.Test
import java.sql.DriverManager

class Version28Test : BaseUnitTest() {

    private lateinit var db: Database
    private lateinit var helper: MigrationHelper

    override fun setUp() {
        super.setUp()
        db = JdbcDatabase(DriverManager.getConnection("jdbc:sqlite::memory:"))
        db.execute("pragma user_version=8;")
        helper = MigrationHelper(db)
        helper.migrateTo(27)
    }

    private fun migrateTo28() = helper.migrateTo(28)

    @Test
    fun `test migrate to 28 creates habit_templates table`() {
        migrateTo28()
        db.query("select id, name, description, category, freq_num, freq_den, target_value, target_type, unit, icon_key, color, reminder_hour, reminder_min, reminder_days, position from habit_templates where 0")
    }

    @Test
    fun `test migrate to 28 creates indexes`() {
        migrateTo28()
        val indexNames = mutableListOf<String>()
        db.query("SELECT name FROM sqlite_master WHERE type='index' AND name LIKE 'idx_habit_templates_%'") {
            indexNames.add(it.getString(0))
        }
        assertThat(indexNames, hasItems("idx_habit_templates_category", "idx_habit_templates_position"))
    }

    @Test
    fun `test migrate to 28 seeds default templates`() {
        migrateTo28()
        
        val templateNames = mutableListOf<String>()
        db.query("SELECT name FROM habit_templates ORDER BY position") {
            templateNames.add(it.getString(0))
        }
        
        assertThat(templateNames.size, equalTo(12))
        assertThat(templateNames, hasItems(
            "Drink Water",
            "Exercise", 
            "Take Vitamins",
            "Read",
            "Practice Language",
            "Learn to Code",
            "Meditate",
            "Plan Your Day",
            "No Social Media",
            "Sleep 8 Hours",
            "Journal",
            "Gratitude"
        ))
    }

    @Test
    fun `test migrate to 28 seeds health templates`() {
        migrateTo28()
        
        val healthTemplates = mutableListOf<Map<String, Any>>()
        db.query("SELECT name, description, category, freq_num, freq_den, target_value, target_type, unit, icon_key, color, reminder_hour, reminder_min, reminder_days, position FROM habit_templates WHERE category = 0 ORDER BY position") {
            healthTemplates.add(mapOf(
                "name" to it.getString(0),
                "description" to it.getString(1),
                "category" to it.getInt(2),
                "freq_num" to it.getInt(3),
                "freq_den" to it.getInt(4),
                "target_value" to it.getDouble(5),
                "target_type" to it.getInt(6),
                "unit" to it.getString(7),
                "icon_key" to it.getString(8),
                "color" to it.getInt(9),
                "reminder_hour" to it.getInt(10),
                "reminder_min" to it.getInt(11),
                "reminder_days" to it.getInt(12),
                "position" to it.getInt(13)
            ))
        }
        
        assertThat(healthTemplates.size, equalTo(3))
        
        // Check Drink Water template
        val drinkWater = healthTemplates[0]
        assertThat(drinkWater["name"], equalTo("Drink Water"))
        assertThat(drinkWater["description"], equalTo("Stay hydrated by drinking water throughout the day"))
        assertThat(drinkWater["category"], equalTo(0)) // HEALTH
        assertThat(drinkWater["freq_num"], equalTo(8))
        assertThat(drinkWater["freq_den"], equalTo(1))
        assertThat(drinkWater["target_value"], equalTo(8.0))
        assertThat(drinkWater["target_type"], equalTo(1)) // AT_LEAST
        assertThat(drinkWater["unit"], equalTo("glasses"))
        assertThat(drinkWater["icon_key"], equalTo("water"))
        assertThat(drinkWater["color"], equalTo(2))
        assertThat(drinkWater["reminder_hour"], equalTo(9))
        assertThat(drinkWater["reminder_min"], equalTo(0))
        assertThat(drinkWater["reminder_days"], equalTo(127))
        assertThat(drinkWater["position"], equalTo(1))
    }

    @Test
    fun `test migrate to 28 seeds learning templates`() {
        migrateTo28()
        
        val learningTemplates = mutableListOf<String>()
        db.query("SELECT name FROM habit_templates WHERE category = 1 ORDER BY position") {
            learningTemplates.add(it.getString(0))
        }
        
        assertThat(learningTemplates.size, equalTo(3))
        assertThat(learningTemplates, hasItems("Read", "Practice Language", "Learn to Code"))
    }

    @Test
    fun `test migrate to 28 seeds productivity templates`() {
        migrateTo28()
        
        val productivityTemplates = mutableListOf<String>()
        db.query("SELECT name FROM habit_templates WHERE category = 2 ORDER BY position") {
            productivityTemplates.add(it.getString(0))
        }
        
        assertThat(productivityTemplates.size, equalTo(3))
        assertThat(productivityTemplates, hasItems("Meditate", "Plan Your Day", "No Social Media"))
    }

    @Test
    fun `test migrate to 28 seeds wellness templates`() {
        migrateTo28()
        
        val wellnessTemplates = mutableListOf<String>()
        db.query("SELECT name FROM habit_templates WHERE category = 3 ORDER BY position") {
            wellnessTemplates.add(it.getString(0))
        }
        
        assertThat(wellnessTemplates.size, equalTo(3))
        assertThat(wellnessTemplates, hasItems("Sleep 8 Hours", "Journal", "Gratitude"))
    }

    @Test
    fun `test repository can access seeded templates`() {
        migrateTo28()
        
        val modelFactory = SQLModelFactory(db)
        val repository = SQLiteHabitTemplateRepository(modelFactory.buildHabitTemplateRepository())
        
        val allTemplates = repository.findAll()
        assertThat(allTemplates.size, equalTo(12))
        
        val healthTemplates = repository.findByCategory(org.isoron.uhabits.core.models.TemplateCategory.HEALTH)
        assertThat(healthTemplates.size, equalTo(3))
        
        val searchResults = repository.search("water")
        assertThat(searchResults.size, equalTo(1))
        assertThat(searchResults[0].name, equalTo("Drink Water"))
    }

    @Test
    fun `test migrate to 28 preserves existing data`() {
        // First migrate to 27 and add some test data
        migrateTo28()
        
        // Add a custom template
        val customTemplate = org.isoron.uhabits.core.models.HabitTemplate(
            name = "Custom Template",
            description = "Custom description",
            category = TemplateCategory.CUSTOM,
            position = 100
        )
        
        val modelFactory = SQLModelFactory(db)
        val repository = SQLiteHabitTemplateRepository(modelFactory.buildHabitTemplateRepository())
        repository.save(customTemplate)
        
        // Verify all templates are present
        val allTemplates = repository.findAll()
        assertThat(allTemplates.size, equalTo(13)) // 12 seeded + 1 custom
        
        val customFound = repository.findById(customTemplate.id!!)
        assertThat(customFound, notNullValue())
        assertThat(customFound!!.name, equalTo("Custom Template"))
    }
}