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

class TemplateHelper(private val modelFactory: ModelFactory) {
    fun cloneTemplateToHabit(template: Template): Habit {
        val habit = modelFactory.buildHabit()
        habit.name = template.name
        habit.description = template.description
        habit.type = template.habitType
        habit.color = template.color
        habit.frequency = Frequency(template.frequencyNum, template.frequencyDen)
        habit.unit = template.unit
        habit.targetValue = template.targetValue
        habit.targetType = template.targetType
        return habit
    }
}
