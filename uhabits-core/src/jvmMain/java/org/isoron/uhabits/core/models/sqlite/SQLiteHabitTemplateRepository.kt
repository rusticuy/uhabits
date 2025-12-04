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

import org.isoron.uhabits.core.database.Database
import org.isoron.uhabits.core.database.Repository
import org.isoron.uhabits.core.models.HabitTemplate
import org.isoron.uhabits.core.models.HabitTemplateRepository
import org.isoron.uhabits.core.models.TemplateCategory
import org.isoron.uhabits.core.models.sqlite.records.HabitTemplateRecord

/**
 * SQLite implementation of HabitTemplateRepository.
 */
class SQLiteHabitTemplateRepository(
    private val repository: Repository<HabitTemplateRecord>
) : HabitTemplateRepository {

    override fun findAll(): List<HabitTemplate> {
        val records = repository.findAll("order by position, name")
        return records.map { record ->
            HabitTemplate().apply { record.copyTo(this) }
        }
    }

    override fun findByCategory(category: TemplateCategory): List<HabitTemplate> {
        val records = repository.findAll("category = ? order by position, name", category.ordinal.toString())
        return records.map { record ->
            HabitTemplate().apply { record.copyTo(this) }
        }
    }

    override fun findById(id: Long): HabitTemplate? {
        val record = repository.findById(id) ?: return null
        return HabitTemplate().apply { record.copyTo(this) }
    }

    override fun search(query: String): List<HabitTemplate> {
        val searchQuery = "%$query%"
        val records = repository.findAll(
            "name like ? or description like ? order by position, name",
            searchQuery, searchQuery
        )
        return records.map { record ->
            HabitTemplate().apply { record.copyTo(this) }
        }
    }

    override fun save(template: HabitTemplate) {
        val record = HabitTemplateRecord()
        record.copyFrom(template)
        repository.save(record)
        record.copyTo(template)
    }

    override fun delete(template: HabitTemplate) {
        template.id?.let { id ->
            repository.remove(id)
        }
    }
}