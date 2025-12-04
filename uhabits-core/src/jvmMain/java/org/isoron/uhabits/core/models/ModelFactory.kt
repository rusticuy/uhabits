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

import org.isoron.uhabits.core.database.Repository
import org.isoron.uhabits.core.models.goals.Goal
import org.isoron.uhabits.core.models.goals.GoalList
import org.isoron.uhabits.core.models.goals.GoalMilestoneList
import org.isoron.uhabits.core.models.sqlite.records.AchievementRecord
import org.isoron.uhabits.core.models.sqlite.records.AchievementUnlockRecord
import org.isoron.uhabits.core.models.sqlite.records.EntryRecord
import org.isoron.uhabits.core.models.sqlite.records.GoalHabitLinkRecord
import org.isoron.uhabits.core.models.sqlite.records.GoalMilestoneRecord
import org.isoron.uhabits.core.models.sqlite.records.GoalRecord
import org.isoron.uhabits.core.models.sqlite.records.HabitRecord
import org.isoron.uhabits.core.models.sqlite.records.HabitTemplateRecord

/**
 * Interface implemented by factories that provide concrete implementations of
 * the core model classes.
 */
interface ModelFactory {

    fun buildHabit(): Habit {
        val scores = buildScoreList()
        val streaks = buildStreakList()
        return Habit(
            scores = scores,
            streaks = streaks,
            originalEntries = buildOriginalEntries(),
            computedEntries = buildComputedEntries()
        )
    }

    fun buildGoal(): Goal {
        return Goal(milestones = buildGoalMilestoneList())
    }

    fun buildComputedEntries(): EntryList
    fun buildOriginalEntries(): EntryList
    fun buildHabitList(): HabitList
    fun buildGoalList(): GoalList
    fun buildScoreList(): ScoreList
    fun buildStreakList(): StreakList
    fun buildGoalMilestoneList(): GoalMilestoneList
    fun buildHabitListRepository(): Repository<HabitRecord>
    fun buildRepetitionListRepository(): Repository<EntryRecord>
    fun buildGoalListRepository(): Repository<GoalRecord>
    fun buildGoalHabitLinkRepository(): Repository<GoalHabitLinkRecord>
    fun buildGoalMilestoneRepository(): Repository<GoalMilestoneRecord>
    fun buildAchievementRepository(): Repository<AchievementRecord>
    fun buildAchievementUnlockRepository(): Repository<AchievementUnlockRecord>
    fun buildHabitTemplateRepository(): Repository<HabitTemplateRecord>
}
