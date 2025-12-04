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
package org.isoron.uhabits.core.models.sqlite.records

import org.isoron.uhabits.core.database.Column
import org.isoron.uhabits.core.database.Table
import org.isoron.uhabits.core.models.Frequency
import org.isoron.uhabits.core.models.HabitTemplate
import org.isoron.uhabits.core.models.NumericalHabitType
import org.isoron.uhabits.core.models.PaletteColor
import org.isoron.uhabits.core.models.TemplateCategory

/**
 * The SQLite database record corresponding to a [HabitTemplate].
 */
@Table(name = "habit_templates")
class HabitTemplateRecord {
    @field:Column
    var name: String? = null

    @field:Column
    var description: String? = null

    @field:Column
    var category: Int? = null

    @field:Column(name = "freq_num")
    var freqNum: Int? = null

    @field:Column(name = "freq_den")
    var freqDen: Int? = null

    @field:Column(name = "target_value")
    var targetValue: Double? = null

    @field:Column(name = "target_type")
    var targetType: Int? = null

    @field:Column
    var unit: String? = null

    @field:Column(name = "icon_key")
    var iconKey: String? = null

    @field:Column
    var color: Int? = null

    @field:Column(name = "reminder_hour")
    var reminderHour: Int? = null

    @field:Column(name = "reminder_min")
    var reminderMin: Int? = null

    @field:Column(name = "reminder_days")
    var reminderDays: Int? = null

    @field:Column
    var position: Int? = null

    @field:Column
    var id: Long? = null

    fun copyFrom(model: HabitTemplate) {
        id = model.id
        name = model.name
        description = model.description
        category = model.category.ordinal
        val (numerator, denominator) = model.frequency
        freqNum = numerator
        freqDen = denominator
        targetValue = model.targetValue
        targetType = model.targetType.value
        unit = model.unit
        iconKey = model.iconKey
        color = model.color.paletteIndex
        reminderHour = model.reminderHour
        reminderMin = model.reminderMin
        reminderDays = model.reminderDays
        position = model.position
    }

    fun copyTo(template: HabitTemplate) {
        template.id = id
        template.name = name ?: ""
        template.description = description ?: ""
        template.category = TemplateCategory.values()[category ?: 0]
        template.frequency = Frequency(freqNum ?: 1, freqDen ?: 1)
        template.targetValue = targetValue ?: 0.0
        template.targetType = NumericalHabitType.fromInt(targetType ?: 0)
        template.unit = unit ?: ""
        template.iconKey = iconKey ?: ""
        template.color = PaletteColor(color ?: 8)
        template.reminderHour = reminderHour
        template.reminderMin = reminderMin
        template.reminderDays = reminderDays
        template.position = position ?: 0
    }
}