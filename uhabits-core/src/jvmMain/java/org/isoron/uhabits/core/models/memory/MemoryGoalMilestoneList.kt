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

import org.isoron.uhabits.core.models.goals.GoalMilestone
import org.isoron.uhabits.core.models.goals.GoalMilestoneList
import java.util.LinkedList

class MemoryGoalMilestoneList : GoalMilestoneList() {
    private val list = LinkedList<GoalMilestone>()

    @Synchronized
    override fun add(milestone: GoalMilestone) {
        require(!list.contains(milestone)) { "milestone already added" }
        val id = milestone.id
        if (id != null && getById(id) != null) throw RuntimeException("duplicate id")
        if (id == null) milestone.id = list.size.toLong()
        list.addLast(milestone)
        observable.notifyListeners()
    }

    @Synchronized
    override fun getById(id: Long): GoalMilestone? {
        for (m in list) {
            checkNotNull(m.id)
            if (m.id == id) return m
        }
        return null
    }

    @Synchronized
    override fun getByPosition(position: Int): GoalMilestone {
        return list[position]
    }

    @Synchronized
    override fun indexOf(m: GoalMilestone): Int {
        return list.indexOf(m)
    }

    @Synchronized
    override fun iterator(): Iterator<GoalMilestone> {
        return ArrayList(list).iterator()
    }

    @Synchronized
    override fun remove(m: GoalMilestone) {
        list.remove(m)
        observable.notifyListeners()
    }

    @Synchronized
    override fun size(): Int {
        return list.size
    }

    @Synchronized
    override fun update(milestones: List<GoalMilestone>) {
        observable.notifyListeners()
    }

    @Synchronized
    override fun clear() {
        list.clear()
        observable.notifyListeners()
    }
}
