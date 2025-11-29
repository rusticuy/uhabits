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
import org.isoron.uhabits.core.models.goals.Goal
import org.isoron.uhabits.core.models.goals.GoalList
import org.isoron.uhabits.core.models.memory.MemoryGoalList
import org.isoron.uhabits.core.models.sqlite.records.GoalRecord
import javax.inject.Inject

class SQLiteGoalList @Inject constructor(private val database: Database) : GoalList() {
    private val repository: Repository<GoalRecord> = Repository(GoalRecord::class.java, database)
    private val list: MemoryGoalList = MemoryGoalList()
    private var loaded = false

    private fun loadRecords() {
        if (loaded) return
        loaded = true
        list.removeAll()
        val records = repository.findAll("order by position")
        var shouldRebuildOrder = false
        for ((expectedPosition, rec) in records.withIndex()) {
            if (rec.position != expectedPosition) shouldRebuildOrder = true
            val g = Goal(
                milestones = SQLiteGoalMilestoneList(rec.id, database)
            )
            rec.copyTo(g)
            list.add(g)
        }
        if (shouldRebuildOrder) rebuildOrder()
    }

    @Synchronized
    override fun add(goal: Goal) {
        loadRecords()
        goal.position = size()
        val record = GoalRecord()
        record.copyFrom(goal)
        repository.save(record)
        goal.id = record.id
        goal.milestones as SQLiteGoalMilestoneList // ensure it has correct goalId
        list.add(goal)
        observable.notifyListeners()
    }

    @Synchronized
    override fun getById(id: Long): Goal? {
        loadRecords()
        return list.getById(id)
    }

    @Synchronized
    override fun getByUUID(uuid: String?): Goal? {
        loadRecords()
        return list.getByUUID(uuid)
    }

    @Synchronized
    override fun getByPosition(position: Int): Goal {
        loadRecords()
        return list.getByPosition(position)
    }

    @Synchronized
    override fun indexOf(g: Goal): Int {
        loadRecords()
        return list.indexOf(g)
    }

    @Synchronized
    override fun iterator(): Iterator<Goal> {
        loadRecords()
        return list.iterator()
    }

    @Synchronized
    override fun remove(g: Goal) {
        loadRecords()
        list.remove(g)
        val record = repository.find(g.id!!) ?: throw RuntimeException("goal not in database")
        repository.executeAsTransaction {
            (g.milestones as SQLiteGoalMilestoneList).clear()
            repository.execSQL("delete from GoalHabitLinks where goal_id = ?", g.id.toString())
            repository.remove(record)
        }
        rebuildOrder()
        observable.notifyListeners()
    }

    @Synchronized
    override fun reorder(from: Goal, to: Goal) {
        loadRecords()
        list.reorder(from, to)
        val fromRecord = repository.find(from.id!!)
        val toRecord = repository.find(to.id!!)
        if (fromRecord == null) throw RuntimeException("goal not in database")
        if (toRecord == null) throw RuntimeException("goal not in database")
        if (toRecord.position!! < fromRecord.position!!) {
            repository.execSQL(
                "update Goals set position = position + 1 " +
                    "where position >= ? and position < ?",
                toRecord.position!!,
                fromRecord.position!!
            )
        } else {
            repository.execSQL(
                "update Goals set position = position - 1 " +
                    "where position > ? and position <= ?",
                fromRecord.position!!,
                toRecord.position!!
            )
        }
        fromRecord.position = toRecord.position
        repository.save(fromRecord)
        observable.notifyListeners()
    }

    @Synchronized
    override fun repair() {
        loadRecords()
        rebuildOrder()
        observable.notifyListeners()
    }

    @Synchronized
    override fun size(): Int {
        loadRecords()
        return list.size()
    }

    @Synchronized
    override fun update(goals: List<Goal>) {
        loadRecords()
        list.update(goals)
        for (g in goals) {
            val record = repository.find(g.id!!) ?: continue
            record.copyFrom(g)
            repository.save(record)
        }
        observable.notifyListeners()
    }

    @Synchronized
    override fun resort() {
        list.resort()
        observable.notifyListeners()
    }

    @Synchronized
    private fun rebuildOrder() {
        val records = repository.findAll("order by position")
        repository.executeAsTransaction {
            for ((pos, r) in records.withIndex()) {
                if (r.position != pos) {
                    r.position = pos
                    repository.save(r)
                }
            }
        }
    }

    @Synchronized
    fun reload() {
        loaded = false
    }
}
