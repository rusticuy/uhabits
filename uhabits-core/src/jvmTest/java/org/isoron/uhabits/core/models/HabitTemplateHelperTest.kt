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
package org.isoron.uhabits.core.models

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.isoron.uhabits.core.BaseUnitTest
import org.isoron.uhabits.core.models.sqlite.records.HabitRecord
import org.isoron.uhabits.core.models.sqlite.records.HabitTemplateRecord
import org.junit.Test

class HabitTemplateHelperTest : BaseUnitTest() {

    @Test
    fun testCreateHabitFromTemplate_YesNoType() {
        val template = HabitTemplate(
            name = "Exercise",
            description = "Daily exercise routine",
            category = TemplateCategory.HEALTH,
            frequency = Frequency(1, 1),
            targetValue = 0.0,
            targetType = NumericalHabitType.AT_LEAST,
            unit = "",
            iconKey = "exercise",
            color = PaletteColor(1),
            reminderHour = 9,
            reminderMin = 0,
            reminderDays = 127,
            position = 1
        )

        val habit = HabitTemplateHelper.createHabitFromTemplate(template, modelFactory)

        assertThat(habit.name, `is`("Exercise"))
        assertThat(habit.description, `is`("Daily exercise routine"))
        assertThat(habit.frequency, `is`(Frequency(1, 1)))
        assertThat(habit.targetValue, `is`(0.0))
        assertThat(habit.targetType, `is`(NumericalHabitType.AT_LEAST))
        assertThat(habit.unit, `is`(""))
        assertThat(habit.color, `is`(PaletteColor(1)))
        assertThat(habit.type, `is`(HabitType.YES_NO))
        assertThat(habit.question, `is`("Did you exercise?"))
        
        assertThat(habit.reminder, notNullValue())
        assertThat(habit.reminder!!.hour, `is`(9))
        assertThat(habit.reminder!!.minute, `is`(0))
        assertThat(habit.reminder!!.days.toInteger(), `is`(127))
    }

    @Test
    fun testCreateHabitFromTemplate_NumericalType() {
        val template = HabitTemplate(
            name = "Drink Water",
            description = "Stay hydrated",
            category = TemplateCategory.HEALTH,
            frequency = Frequency(8, 1),
            targetValue = 8.0,
            targetType = NumericalHabitType.AT_LEAST,
            unit = "glasses",
            iconKey = "water",
            color = PaletteColor(2),
            reminderHour = null,
            reminderMin = null,
            reminderDays = null,
            position = 2
        )

        val habit = HabitTemplateHelper.createHabitFromTemplate(template, modelFactory)

        assertThat(habit.name, `is`("Drink Water"))
        assertThat(habit.description, `is`("Stay hydrated"))
        assertThat(habit.frequency, `is`(Frequency(8, 1)))
        assertThat(habit.targetValue, `is`(8.0))
        assertThat(habit.targetType, `is`(NumericalHabitType.AT_LEAST))
        assertThat(habit.unit, `is`("glasses"))
        assertThat(habit.color, `is`(PaletteColor(2)))
        assertThat(habit.type, `is`(HabitType.NUMERICAL))
        assertThat(habit.question, `is`("Did you drink water?"))
        assertThat(habit.reminder, `is`(null as Reminder?))
    }

    @Test
    fun testCreateHabitRecordFromTemplate_YesNoType() {
        val template = HabitTemplate(
            name = "Meditate",
            description = "Daily meditation",
            category = TemplateCategory.WELLNESS,
            frequency = Frequency(1, 1),
            targetValue = 0.0,
            targetType = NumericalHabitType.AT_LEAST,
            unit = "",
            iconKey = "meditation",
            color = PaletteColor(7),
            reminderHour = 7,
            reminderMin = 30,
            reminderDays = 127,
            position = 3
        )

        val record = HabitTemplateHelper.createHabitRecordFromTemplate(template)

        assertThat(record.name, `is`("Meditate"))
        assertThat(record.description, `is`("Daily meditation"))
        assertThat(record.freqNum, `is`(1))
        assertThat(record.freqDen, `is`(1))
        assertThat(record.targetValue, `is`(0.0))
        assertThat(record.targetType, `is`(NumericalHabitType.AT_LEAST.value))
        assertThat(record.unit, `is`(""))
        assertThat(record.color, `is`(7))
        assertThat(record.type, `is`(HabitType.YES_NO.value))
        assertThat(record.archived, `is`(0))
        assertThat(record.highlight, `is`(0))
        assertThat(record.position, `is`(0))
        
        assertThat(record.reminderHour, `is`(7))
        assertThat(record.reminderMin, `is`(30))
        assertThat(record.reminderDays, `is`(127))
    }

    @Test
    fun testCreateHabitRecordFromTemplate_NumericalType() {
        val template = HabitTemplate(
            name = "Read",
            description = "Read books",
            category = TemplateCategory.LEARNING,
            frequency = Frequency(1, 1),
            targetValue = 30.0,
            targetType = NumericalHabitType.AT_LEAST,
            unit = "minutes",
            iconKey = "book",
            color = PaletteColor(4),
            reminderHour = null,
            reminderMin = null,
            reminderDays = null,
            position = 4
        )

        val record = HabitTemplateHelper.createHabitRecordFromTemplate(template)

        assertThat(record.name, `is`("Read"))
        assertThat(record.description, `is`("Read books"))
        assertThat(record.freqNum, `is`(1))
        assertThat(record.freqDen, `is`(1))
        assertThat(record.targetValue, `is`(30.0))
        assertThat(record.targetType, `is`(NumericalHabitType.AT_LEAST.value))
        assertThat(record.unit, `is`("minutes"))
        assertThat(record.color, `is`(4))
        assertThat(record.type, `is`(HabitType.NUMERICAL.value))
        assertThat(record.reminderHour, `is`(null as Int?))
        assertThat(record.reminderMin, `is`(null as Int?))
        assertThat(record.reminderDays, `is`(null as Int?))
    }

    @Test
    fun testCreateHabitRecordFromTemplate_NoReminder() {
        val template = HabitTemplate(
            name = "Journal",
            description = "Write journal",
            category = TemplateCategory.WELLNESS,
            frequency = Frequency(1, 1),
            targetValue = 0.0,
            targetType = NumericalHabitType.AT_LEAST,
            unit = "",
            iconKey = "journal",
            color = PaletteColor(11),
            reminderHour = null,
            reminderMin = null,
            reminderDays = null,
            position = 5
        )

        val record = HabitTemplateHelper.createHabitRecordFromTemplate(template)

        assertThat(record.reminderHour, `is`(null as Int?))
        assertThat(record.reminderMin, `is`(null as Int?))
        assertThat(record.reminderDays, `is`(null as Int?))
    }
}