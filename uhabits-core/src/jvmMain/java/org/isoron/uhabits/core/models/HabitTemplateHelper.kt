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
 */
package org.isoron.uhabits.core.models

import org.isoron.uhabits.core.models.sqlite.records.HabitRecord

/**
 * Helper class to convert habit templates to fully initialized habits.
 */
object HabitTemplateHelper {

    /**
     * Creates a fully initialized [Habit] from a [HabitTemplate].
     * The habit will have all the properties from the template, plus
     * the necessary initialized collections and default values.
     */
    fun createHabitFromTemplate(
        template: HabitTemplate,
        modelFactory: ModelFactory
    ): Habit {
        val habit = modelFactory.buildHabit()
        
        // Copy properties from template
        habit.name = template.name
        habit.description = template.description
        habit.frequency = template.frequency
        habit.targetValue = template.targetValue
        habit.targetType = template.targetType
        habit.unit = template.unit
        habit.color = template.color
        habit.question = "Did you ${template.name.lowercase()}?"
        
        // Set reminder if template has one
        if (template.reminderHour != null && template.reminderMin != null && template.reminderDays != null) {
            habit.reminder = Reminder(
                template.reminderHour!!,
                template.reminderMin!!,
                WeekdayList(template.reminderDays!!)
            )
        }
        
        // Set type based on whether it has a target value
        habit.type = if (template.targetValue > 0) {
            HabitType.NUMERICAL
        } else {
            HabitType.YES_NO
        }
        
        return habit
    }

    /**
     * Creates a [HabitRecord] from a [HabitTemplate] for saving to database.
     */
    fun createHabitRecordFromTemplate(template: HabitTemplate): HabitRecord {
        val record = HabitRecord()
        
        record.name = template.name
        record.description = template.description
        record.color = template.color.paletteIndex
        record.archived = 0
        record.highlight = 0
        record.position = 0
        
        val (numerator, denominator) = template.frequency
        record.freqNum = numerator
        record.freqDen = denominator
        
        record.targetValue = template.targetValue
        record.targetType = template.targetType.value
        record.unit = template.unit
        
        // Set type based on whether it has a target value
        record.type = if (template.targetValue > 0) {
            HabitType.NUMERICAL.value
        } else {
            HabitType.YES_NO.value
        }
        
        // Set reminder if template has one
        if (template.reminderHour != null && template.reminderMin != null && template.reminderDays != null) {
            record.reminderHour = template.reminderHour
            record.reminderMin = template.reminderMin
            record.reminderDays = template.reminderDays
        }
        
        return record
    }
}