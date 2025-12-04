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

package org.isoron.uhabits.core.models.memory

import org.isoron.uhabits.core.models.HabitType
import org.isoron.uhabits.core.models.NumericalHabitType
import org.isoron.uhabits.core.models.PaletteColor
import org.isoron.uhabits.core.models.Template
import org.isoron.uhabits.core.models.TemplateRepository

class MemoryTemplateRepository : TemplateRepository {
    private val templates = listOf(
        // Health & Fitness
        Template(
            id = "1",
            name = "Morning Run",
            description = "5 km morning run",
            category = "Health & Fitness",
            icon = "🏃",
            habitType = HabitType.YES_NO,
            frequencyNum = 3,
            frequencyDen = 7,
            color = PaletteColor(1)
        ),
        Template(
            id = "2",
            name = "Drink Water",
            description = "Drink 8 glasses of water daily",
            category = "Health & Fitness",
            icon = "💧",
            habitType = HabitType.NUMERICAL,
            frequencyNum = 1,
            frequencyDen = 1,
            color = PaletteColor(2),
            unit = "glasses",
            targetValue = 8.0,
            targetType = NumericalHabitType.AT_LEAST
        ),
        Template(
            id = "3",
            name = "Yoga",
            description = "30 minutes of yoga",
            category = "Health & Fitness",
            icon = "🧘",
            habitType = HabitType.YES_NO,
            frequencyNum = 3,
            frequencyDen = 7,
            color = PaletteColor(3)
        ),
        Template(
            id = "4",
            name = "Sleep 8 Hours",
            description = "Get 8 hours of sleep",
            category = "Health & Fitness",
            icon = "😴",
            habitType = HabitType.YES_NO,
            frequencyNum = 7,
            frequencyDen = 7,
            color = PaletteColor(4)
        ),
        // Learning
        Template(
            id = "5",
            name = "Read",
            description = "Read for 30 minutes",
            category = "Learning",
            icon = "📚",
            habitType = HabitType.YES_NO,
            frequencyNum = 4,
            frequencyDen = 7,
            color = PaletteColor(5)
        ),
        Template(
            id = "6",
            name = "Study",
            description = "Study for 1 hour",
            category = "Learning",
            icon = "📖",
            habitType = HabitType.YES_NO,
            frequencyNum = 5,
            frequencyDen = 7,
            color = PaletteColor(6)
        ),
        Template(
            id = "7",
            name = "Practice Language",
            description = "Practice language learning",
            category = "Learning",
            icon = "🌐",
            habitType = HabitType.NUMERICAL,
            frequencyNum = 1,
            frequencyDen = 1,
            color = PaletteColor(7),
            unit = "minutes",
            targetValue = 30.0,
            targetType = NumericalHabitType.AT_LEAST
        ),
        // Productivity
        Template(
            id = "8",
            name = "Meditate",
            description = "Meditate for 10 minutes",
            category = "Productivity",
            icon = "🧠",
            habitType = HabitType.YES_NO,
            frequencyNum = 7,
            frequencyDen = 7,
            color = PaletteColor(8)
        ),
        Template(
            id = "9",
            name = "Morning Journal",
            description = "Write morning journal",
            category = "Productivity",
            icon = "✍️",
            habitType = HabitType.YES_NO,
            frequencyNum = 5,
            frequencyDen = 7,
            color = PaletteColor(9)
        ),
        Template(
            id = "10",
            name = "Work Focused Hours",
            description = "Focus hours without distractions",
            category = "Productivity",
            icon = "💼",
            habitType = HabitType.NUMERICAL,
            frequencyNum = 1,
            frequencyDen = 1,
            color = PaletteColor(10),
            unit = "hours",
            targetValue = 4.0,
            targetType = NumericalHabitType.AT_LEAST
        ),
        // Social & Relationships
        Template(
            id = "11",
            name = "Call Family",
            description = "Call family members",
            category = "Social & Relationships",
            icon = "📞",
            habitType = HabitType.YES_NO,
            frequencyNum = 1,
            frequencyDen = 7,
            color = PaletteColor(11)
        ),
        Template(
            id = "12",
            name = "Meet Friends",
            description = "Spend time with friends",
            category = "Social & Relationships",
            icon = "👥",
            habitType = HabitType.YES_NO,
            frequencyNum = 2,
            frequencyDen = 7,
            color = PaletteColor(0)
        ),
        // Creative
        Template(
            id = "13",
            name = "Draw",
            description = "Practice drawing",
            category = "Creative",
            icon = "🎨",
            habitType = HabitType.YES_NO,
            frequencyNum = 3,
            frequencyDen = 7,
            color = PaletteColor(1)
        ),
        Template(
            id = "14",
            name = "Write",
            description = "Write creatively",
            category = "Creative",
            icon = "✏️",
            habitType = HabitType.YES_NO,
            frequencyNum = 3,
            frequencyDen = 7,
            color = PaletteColor(2)
        )
    )

    override fun getAll(): List<Template> = templates

    override fun getById(id: String): Template? = templates.find { it.id == id }

    override fun getByCategory(category: String): List<Template> =
        templates.filter { it.category == category }

    override fun getCategories(): List<String> =
        templates.map { it.category }.distinct().sorted()

    override fun search(query: String): List<Template> {
        val lowerQuery = query.lowercase()
        return templates.filter {
            it.name.lowercase().contains(lowerQuery) ||
            it.description.lowercase().contains(lowerQuery) ||
            it.category.lowercase().contains(lowerQuery)
        }
    }
}
