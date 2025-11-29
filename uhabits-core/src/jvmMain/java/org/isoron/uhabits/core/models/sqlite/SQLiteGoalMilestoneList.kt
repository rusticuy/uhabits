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
import org.isoron.uhabits.core.models.goals.GoalMilestone
import org.isoron.uhabits.core.models.goals.GoalMilestoneList
import org.isoron.uhabits.core.models.memory.MemoryGoalMilestoneList
import org.isoron.uhabits.core.models.sqlite.records.GoalMilestoneRecord

class SQLiteGoalMilestoneList(
    private val goalId: Long?,
    database: Database
) : GoalMilestoneList() {
    private val repository: Repository<GoalMilestoneRecord> = Repository(GoalMilestoneRecord::class.java, database)
    private val list: MemoryGoalMilestoneList = MemoryGoalMilestoneList()
    private var loaded = false

    private fun loadRecords() {
        if (loaded) return
        loaded = true
        list.removeAll()
        val records = repository.findAll("where goal_id = ? order by position", goalId.toString())
        for (rec in records) {
            val m = GoalMilestone()
            rec.copyTo(m)
            list.add(m)
        }
    }

    @Synchronized
    override fun add(milestone: GoalMilestone) {
        loadRecords()
        milestone.goalId = goalId
        milestone.position = list.size()
        val record = GoalMilestoneRecord()
        record.copyFrom(milestone)
        repository.save(record)
        milestone.id = record.id
        list.add(milestone)
        observable.notifyListeners()
    }

    @Synchronized
    override fun getById(id: Long): GoalMilestone? {
        loadRecords()
        return list.getById(id)
    }

    @Synchronized
    override fun getByPosition(position: Int): GoalMilestone {
        loadRecords()
        return list.getByPosition(position)
    }

    @Synchronized
    override fun indexOf(m: GoalMilestone): Int {
        loadRecords()
        return list.indexOf(m)
    }

    @Synchronized
    override fun iterator(): Iterator<GoalMilestone> {
        loadRecords()
        return list.iterator()
    }

    @Synchronized
    override fun remove(m: GoalMilestone) {
        loadRecords()
        list.remove(m)
        val record = repository.find(m.id!!) ?: throw RuntimeException("milestone not in database")
        repository.remove(record)
        observable.notifyListeners()
    }

    @Synchronized
    override fun size(): Int {
        loadRecords()
        return list.size()
    }

    @Synchronized
    override fun update(milestones: List<GoalMilestone>) {
        loadRecords()
        list.update(milestones)
        for (m in milestones) {
            val record = repository.find(m.id!!) ?: continue
            record.copyFrom(m)
            repository.save(record)
        }
        observable.notifyListeners()
    }

    @Synchronized
    override fun clear() {
        loadRecords()
        list.removeAll()
        repository.execSQL("delete from GoalMilestones where goal_id = ?", goalId.toString())
        observable.notifyListeners()
    }

    @Synchronized
    fun reload() {
        loaded = false
    }
}
