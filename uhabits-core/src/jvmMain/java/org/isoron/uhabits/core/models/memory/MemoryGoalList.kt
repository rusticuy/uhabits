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

import org.isoron.uhabits.core.models.goals.Goal
import org.isoron.uhabits.core.models.goals.GoalList
import java.util.ArrayList
import java.util.LinkedList
import java.util.Objects

class MemoryGoalList : GoalList() {
    private val list = LinkedList<Goal>()

    @Synchronized
    @Throws(IllegalArgumentException::class)
    override fun add(goal: Goal) {
        require(!list.contains(goal)) { "goal already added" }
        val id = goal.id
        if (id != null && getById(id) != null) throw RuntimeException("duplicate id")
        if (id == null) goal.id = list.size.toLong()
        list.addLast(goal)
        resort()
    }

    @Synchronized
    override fun getById(id: Long): Goal? {
        for (g in list) {
            checkNotNull(g.id)
            if (g.id == id) return g
        }
        return null
    }

    @Synchronized
    override fun getByUUID(uuid: String?): Goal? {
        for (g in list) if (Objects.requireNonNull(g.uuid) == uuid) return g
        return null
    }

    @Synchronized
    override fun getByPosition(position: Int): Goal {
        return list[position]
    }

    @Synchronized
    override fun indexOf(g: Goal): Int {
        return list.indexOf(g)
    }

    @Synchronized
    override fun iterator(): Iterator<Goal> {
        return ArrayList(list).iterator()
    }

    @Synchronized
    override fun remove(g: Goal) {
        list.remove(g)
        observable.notifyListeners()
    }

    @Synchronized
    override fun reorder(from: Goal, to: Goal) {
        require(indexOf(from) >= 0) { "list does not contain (from) goal" }
        val toPos = indexOf(to)
        require(toPos >= 0) { "list does not contain (to) goal" }
        list.remove(from)
        list.add(toPos, from)
        var position = 0
        for (g in list) g.position = position++
        observable.notifyListeners()
    }

    @Synchronized
    override fun repair() {
        resort()
        observable.notifyListeners()
    }

    @Synchronized
    override fun size(): Int {
        return list.size
    }

    @Synchronized
    override fun update(goals: List<Goal>) {
        resort()
    }

    @Synchronized
    override fun resort() {
        list.sortWith { g1, g2 -> g1.position.compareTo(g2.position) }
        observable.notifyListeners()
    }
}
