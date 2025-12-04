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
package org.isoron.uhabits.core.models.sqlite

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.hasSize
import org.isoron.uhabits.core.BaseUnitTest
import org.isoron.uhabits.core.models.Frequency
import org.isoron.uhabits.core.models.HabitTemplate
import org.isoron.uhabits.core.models.NumericalHabitType
import org.isoron.uhabits.core.models.PaletteColor
import org.isoron.uhabits.core.models.TemplateCategory
import org.isoron.uhabits.core.models.sqlite.records.HabitTemplateRecord
import org.junit.Test

class SQLiteHabitTemplateRepositoryTest : BaseUnitTest() {

    private lateinit var repository: SQLiteHabitTemplateRepository

    override fun setUp() {
        super.setUp()
        val repo = modelFactory.buildHabitTemplateRepository()
        repository = SQLiteHabitTemplateRepository(repo)
    }

    @Test
    fun testSaveAndFindById() {
        val template = HabitTemplate(
            name = "Test Template",
            description = "Test Description",
            category = TemplateCategory.HEALTH,
            frequency = Frequency(3, 7),
            targetValue = 30.0,
            targetType = NumericalHabitType.AT_LEAST,
            unit = "minutes",
            iconKey = "test_icon",
            color = PaletteColor(5),
            reminderHour = 9,
            reminderMin = 0,
            reminderDays = 127,
            position = 1
        )

        repository.save(template)

        assertThat(template.id, notNullValue())
        val found = repository.findById(template.id!!)
        assertThat(found, notNullValue())
        assertThat(found!!.name, `is`("Test Template"))
        assertThat(found.description, `is`("Test Description"))
        assertThat(found.category, `is`(TemplateCategory.HEALTH))
        assertThat(found.frequency, `is`(Frequency(3, 7)))
        assertThat(found.targetValue, `is`(30.0))
        assertThat(found.targetType, `is`(NumericalHabitType.AT_LEAST))
        assertThat(found.unit, `is`("minutes"))
        assertThat(found.iconKey, `is`("test_icon"))
        assertThat(found.color, `is`(PaletteColor(5)))
        assertThat(found.reminderHour, `is`(9))
        assertThat(found.reminderMin, `is`(0))
        assertThat(found.reminderDays, `is`(127))
        assertThat(found.position, `is`(1))
    }

    @Test
    fun testFindAll() {
        val template1 = HabitTemplate(name = "Template 1", category = TemplateCategory.HEALTH, position = 2)
        val template2 = HabitTemplate(name = "Template 2", category = TemplateCategory.LEARNING, position = 1)

        repository.save(template1)
        repository.save(template2)

        val all = repository.findAll()
        assertThat(all, hasSize(2))
        assertThat(all[0].name, `is`("Template 2")) // Should be ordered by position, then name
        assertThat(all[1].name, `is`("Template 1"))
    }

    @Test
    fun testFindByCategory() {
        val health1 = HabitTemplate(name = "Health 1", category = TemplateCategory.HEALTH)
        val health2 = HabitTemplate(name = "Health 2", category = TemplateCategory.HEALTH)
        val learning = HabitTemplate(name = "Learning", category = TemplateCategory.LEARNING)

        repository.save(health1)
        repository.save(health2)
        repository.save(learning)

        val healthTemplates = repository.findByCategory(TemplateCategory.HEALTH)
        assertThat(healthTemplates, hasSize(2))
        assertThat(healthTemplates.map { it.name }, containsInAnyOrder("Health 1", "Health 2"))

        val learningTemplates = repository.findByCategory(TemplateCategory.LEARNING)
        assertThat(learningTemplates, hasSize(1))
        assertThat(learningTemplates[0].name, `is`("Learning"))

        val emptyTemplates = repository.findByCategory(TemplateCategory.PRODUCTIVITY)
        assertThat(emptyTemplates, hasSize(0))
    }

    @Test
    fun testSearch() {
        val template1 = HabitTemplate(name = "Exercise", description = "Physical activity")
        val template2 = HabitTemplate(name = "Read Books", description = "Mental exercise")
        val template3 = HabitTemplate(name = "Meditation", description = "Mindfulness practice")

        repository.save(template1)
        repository.save(template2)
        repository.save(template3)

        val searchResults = repository.search("exercise")
        assertThat(searchResults, hasSize(2))
        assertThat(searchResults.map { it.name }, containsInAnyOrder("Exercise", "Read Books"))

        val searchResults2 = repository.search("meditation")
        assertThat(searchResults2, hasSize(1))
        assertThat(searchResults2[0].name, `is`("Meditation"))

        val emptyResults = repository.search("nonexistent")
        assertThat(emptyResults, hasSize(0))
    }

    @Test
    fun testUpdate() {
        val template = HabitTemplate(name = "Original Name", category = TemplateCategory.HEALTH)
        repository.save(template)

        template.name = "Updated Name"
        template.category = TemplateCategory.LEARNING
        template.targetValue = 50.0
        repository.save(template)

        val updated = repository.findById(template.id!!)
        assertThat(updated!!.name, `is`("Updated Name"))
        assertThat(updated.category, `is`(TemplateCategory.LEARNING))
        assertThat(updated.targetValue, `is`(50.0))
    }

    @Test
    fun testDelete() {
        val template = HabitTemplate(name = "To Delete")
        repository.save(template)

        assertThat(repository.findById(template.id!!), notNullValue())

        repository.delete(template)

        assertThat(repository.findById(template.id!!), `is`(null as HabitTemplate?))
    }
}