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

/**
 * Represents a habit template that can be used to create new habits with predefined settings.
 */
data class HabitTemplate(
    var id: Long? = null,
    var name: String = "",
    var description: String = "",
    var category: TemplateCategory = TemplateCategory.CUSTOM,
    var frequency: Frequency = Frequency.DAILY,
    var targetValue: Double = 0.0,
    var targetType: NumericalHabitType = NumericalHabitType.AT_LEAST,
    var unit: String = "",
    var iconKey: String = "",
    var color: PaletteColor = PaletteColor(8),
    var reminderHour: Int? = null,
    var reminderMin: Int? = null,
    var reminderDays: Int? = null,
    var position: Int = 0
) {
    var observable = ModelObservable()
}