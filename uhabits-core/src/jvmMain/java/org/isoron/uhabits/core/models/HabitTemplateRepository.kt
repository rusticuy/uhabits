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
 * Repository interface for habit templates.
 */
interface HabitTemplateRepository {
    /**
     * Get all habit templates.
     */
    fun findAll(): List<HabitTemplate>

    /**
     * Get habit templates by category.
     */
    fun findByCategory(category: TemplateCategory): List<HabitTemplate>

    /**
     * Get a habit template by ID.
     */
    fun findById(id: Long): HabitTemplate?

    /**
     * Search habit templates by name or description.
     */
    fun search(query: String): List<HabitTemplate>

    /**
     * Save a habit template (create or update).
     */
    fun save(template: HabitTemplate)

    /**
     * Delete a habit template.
     */
    fun delete(template: HabitTemplate)
}