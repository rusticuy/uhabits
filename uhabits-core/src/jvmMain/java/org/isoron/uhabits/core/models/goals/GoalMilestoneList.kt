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
package org.isoron.uhabits.core.models.goals

import org.isoron.uhabits.core.models.ModelObservable
import java.util.LinkedList
import javax.annotation.concurrent.ThreadSafe

@ThreadSafe
abstract class GoalMilestoneList : Iterable<GoalMilestone> {
    val observable: ModelObservable

    constructor() {
        observable = ModelObservable()
    }

    abstract fun add(milestone: GoalMilestone)
    abstract fun getById(id: Long): GoalMilestone?
    abstract fun getByPosition(position: Int): GoalMilestone
    abstract fun indexOf(m: GoalMilestone): Int
    abstract fun remove(m: GoalMilestone)
    abstract fun size(): Int
    abstract fun update(milestones: List<GoalMilestone>)
    abstract fun clear()

    fun isEmpty(): Boolean = size() == 0

    fun removeAll() {
        val copy: MutableList<GoalMilestone> = LinkedList()
        for (m in this) copy.add(m)
        for (m in copy) remove(m)
        observable.notifyListeners()
    }

    fun update(milestone: GoalMilestone) {
        update(listOf(milestone))
    }
}
