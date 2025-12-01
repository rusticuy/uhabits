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

import java.util.LinkedList

class MemoryGoalList : GoalList() {
    private val goals = LinkedList<Goal>()
    private val habitLinks = LinkedList<GoalHabitLink>()
    private val milestones = LinkedList<GoalMilestone>()
    private var nextId = 1L

    override fun add(goal: Goal) {
        synchronized(this) {
            if (goal.id == null) goal.id = nextId++
            goals.add(goal)
            observable.notifyListeners()
        }
    }

    override fun getById(id: Long): Goal? {
        synchronized(this) {
            return goals.find { it.id == id }
        }
    }

    override fun getByUUID(uuid: String?): Goal? {
        synchronized(this) {
            return goals.find { it.uuid == uuid }
        }
    }

    override fun getByPosition(position: Int): Goal {
        synchronized(this) {
            return goals[position]
        }
    }

    override fun indexOf(goal: Goal): Int {
        synchronized(this) {
            return goals.indexOf(goal)
        }
    }

    override fun remove(goal: Goal) {
        synchronized(this) {
            goals.remove(goal)
            habitLinks.removeAll { it.goalId == goal.id }
            milestones.removeAll { it.goalId == goal.id }
            observable.notifyListeners()
        }
    }

    override fun reorder(from: Goal, to: Goal) {
        synchronized(this) {
            require(indexOf(from) >= 0) { "list does not contain (from) goal" }
            val toPos = indexOf(to)
            require(toPos >= 0) { "list does not contain (to) goal" }
            goals.remove(from)
            goals.add(toPos, from)
            var position = 0
            for (g in goals) g.position = position++
            observable.notifyListeners()
        }
    }

    override fun repair() {
        synchronized(this) {
            resort()
            observable.notifyListeners()
        }
    }

    override fun size(): Int {
        synchronized(this) {
            return goals.size
        }
    }

    override fun update(goals: List<Goal>) {
        synchronized(this) {
            observable.notifyListeners()
        }
    }

    override fun resort() {
        synchronized(this) {
            goals.sortWith { g1, g2 -> g1.position.compareTo(g2.position) }
            observable.notifyListeners()
        }
    }

    override fun iterator(): Iterator<Goal> {
        synchronized(this) {
            return LinkedList(goals).iterator()
        }
    }

    override fun getLinkedHabits(goalId: Long): List<GoalHabitLink> {
        synchronized(this) {
            return habitLinks.filter { it.goalId == goalId }
        }
    }

    override fun addHabitLink(link: GoalHabitLink) {
        synchronized(this) {
            if (link.id == null) link.id = nextId++
            habitLinks.add(link)
            observable.notifyListeners()
        }
    }

    override fun removeHabitLink(link: GoalHabitLink) {
        synchronized(this) {
            habitLinks.remove(link)
            observable.notifyListeners()
        }
    }

    override fun updateHabitLink(link: GoalHabitLink) {
        synchronized(this) {
            observable.notifyListeners()
        }
    }

    override fun getMilestones(goalId: Long): List<GoalMilestone> {
        synchronized(this) {
            return milestones.filter { it.goalId == goalId }
        }
    }

    override fun addMilestone(milestone: GoalMilestone) {
        synchronized(this) {
            if (milestone.id == null) milestone.id = nextId++
            milestones.add(milestone)
            observable.notifyListeners()
        }
    }

    override fun removeMilestone(milestone: GoalMilestone) {
        synchronized(this) {
            milestones.remove(milestone)
            observable.notifyListeners()
        }
    }

    override fun updateMilestone(milestone: GoalMilestone) {
        synchronized(this) {
            observable.notifyListeners()
        }
    }
}
