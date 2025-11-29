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

import javax.inject.Inject

class GoalList @Inject constructor() {

    private val goals = mutableListOf<Goal>()
    private var nextId = 1L
    val observable = ModelObservable()

    fun add(goal: Goal) {
        if (goal.id == null) {
            goal.id = nextId++
        }
        goals.add(goal)
        observable.notifyListeners()
    }

    fun getById(id: Long): Goal? = goals.find { it.id == id }

    fun getAll(): List<Goal> = goals.toList()

    fun remove(goal: Goal) {
        goals.remove(goal)
        observable.notifyListeners()
    }

    fun update(goal: Goal) {
        val index = goals.indexOfFirst { it.id == goal.id }
        if (index >= 0) {
            goals[index] = goal
            observable.notifyListeners()
        }
    }

    operator fun iterator() = goals.iterator()

    fun size(): Int = goals.size

    fun getLinkedHabitsCount(goal: Goal): Int = goal.linkedHabits.size
}
