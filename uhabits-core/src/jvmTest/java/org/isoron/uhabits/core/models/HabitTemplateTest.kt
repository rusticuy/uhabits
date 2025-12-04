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
import org.hamcrest.MatcherAssert.assertThat
import org.isoron.uhabits.core.BaseUnitTest
import org.junit.Test

class HabitTemplateTest : BaseUnitTest() {

    @Test
    fun testCreateTemplate() {
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

        assertThat(template.name, `is`("Test Template"))
        assertThat(template.description, `is`("Test Description"))
        assertThat(template.category, `is`(TemplateCategory.HEALTH))
        assertThat(template.frequency, `is`(Frequency(3, 7)))
        assertThat(template.targetValue, `is`(30.0))
        assertThat(template.targetType, `is`(NumericalHabitType.AT_LEAST))
        assertThat(template.unit, `is`("minutes"))
        assertThat(template.iconKey, `is`("test_icon"))
        assertThat(template.color, `is`(PaletteColor(5)))
        assertThat(template.reminderHour, `is`(9))
        assertThat(template.reminderMin, `is`(0))
        assertThat(template.reminderDays, `is`(127))
        assertThat(template.position, `is`(1))
    }

    @Test
    fun testDefaultValues() {
        val template = HabitTemplate()

        assertThat(template.name, `is`(""))
        assertThat(template.description, `is`(""))
        assertThat(template.category, `is`(TemplateCategory.CUSTOM))
        assertThat(template.frequency, `is`(Frequency.DAILY))
        assertThat(template.targetValue, `is`(0.0))
        assertThat(template.targetType, `is`(NumericalHabitType.AT_LEAST))
        assertThat(template.unit, `is`(""))
        assertThat(template.iconKey, `is`(""))
        assertThat(template.color, `is`(PaletteColor(8)))
        assertThat(template.reminderHour, `is`(null as Int?))
        assertThat(template.reminderMin, `is`(null as Int?))
        assertThat(template.reminderDays, `is`(null as Int?))
        assertThat(template.position, `is`(0))
    }

    @Test
    fun testObservable() {
        val template = HabitTemplate()
        val observable = template.observable
        assertThat(observable, `is`(template.observable))
    }
}