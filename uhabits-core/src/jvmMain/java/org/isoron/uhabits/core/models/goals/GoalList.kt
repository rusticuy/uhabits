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
abstract class GoalList : Iterable<Goal> {
    val observable: ModelObservable

    constructor() {
        observable = ModelObservable()
    }

    abstract fun add(goal: Goal)
    abstract fun getById(id: Long): Goal?
    abstract fun getByUUID(uuid: String?): Goal?
    abstract fun getByPosition(position: Int): Goal
    abstract fun indexOf(g: Goal): Int
    abstract fun remove(g: Goal)
    abstract fun reorder(from: Goal, to: Goal)
    abstract fun repair()
    abstract fun size(): Int
    abstract fun update(goals: List<Goal>)
    abstract fun resort()

    val isEmpty: Boolean
        get() = size() == 0

    open fun removeAll() {
        val copy: MutableList<Goal> = LinkedList()
        for (g in this) copy.add(g)
        for (g in copy) remove(g)
        observable.notifyListeners()
    }

    fun update(goal: Goal) {
        update(listOf(goal))
    }
}
