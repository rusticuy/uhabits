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
package org.isoron.uhabits.core.achievements

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.isoron.uhabits.core.AppScope
import org.isoron.uhabits.core.commands.Command
import org.isoron.uhabits.core.commands.CommandRunner
import org.isoron.uhabits.core.commands.CreateRepetitionCommand
import org.isoron.uhabits.core.models.Habit
import org.isoron.uhabits.core.models.Streak
import org.isoron.uhabits.core.models.Timestamp
import javax.inject.Inject

@AppScope
class AchievementDetector @Inject constructor(
    private val commandRunner: CommandRunner,
    private val repository: AchievementRepository,
    definitionProvider: AchievementDefinitionProvider
) : CommandRunner.Listener {

    private val definitions: List<AchievementDefinition> = definitionProvider.getDefinitions()
    private val definitionsById = definitions.associateBy { it.id }

    private val events = MutableSharedFlow<AchievementUnlocked>(
        extraBufferCapacity = 8
    )

    val unlockedEvents: SharedFlow<AchievementUnlocked> = events.asSharedFlow()

    fun startListening() {
        commandRunner.addListener(this)
    }

    fun stopListening() {
        commandRunner.removeListener(this)
    }

    override fun onCommandFinished(command: Command) {
        if (command !is CreateRepetitionCommand) return
        evaluate(command.habit, command.timestamp)
    }

    fun getUnlockedAchievements(): List<AchievementUnlocked> {
        return repository.getUnlocks().mapNotNull { toUnlocked(it) }
    }

    fun getUnlockedAchievements(habit: Habit): List<AchievementUnlocked> {
        return repository.getUnlocksForHabit(habit.id, habit.uuid).mapNotNull { toUnlocked(it) }
    }

    private fun evaluate(habit: Habit, timestamp: Timestamp) {
        val streak = findStreak(habit, timestamp) ?: return
        val streakLength = streak.length
        val habitId = habit.id
        val habitUuid = habit.uuid

        for (definition in definitions) {
            if (!definition.matches(habit)) continue
            if (streakLength < definition.requiredStreak) continue
            if (repository.isUnlocked(definition.id, habitId, habitUuid)) continue

            val record = AchievementUnlockRecord(
                definitionId = definition.id,
                habitId = habitId,
                habitUuid = habitUuid,
                habitName = habit.name,
                streakLength = streakLength,
                unlockedAt = timestamp
            )
            repository.save(record)
            events.tryEmit(toUnlocked(record, definition))
        }
    }

    private fun findStreak(habit: Habit, timestamp: Timestamp): Streak? {
        return habit.streaks.getTimeline().firstOrNull { streak ->
            timestamp >= streak.start && timestamp <= streak.end
        }
    }

    private fun AchievementDefinition.matches(habit: Habit): Boolean {
        if (habitId != null && habit.id != habitId) return false
        if (habitUuid != null && habit.uuid != habitUuid) return false
        return true
    }

    private fun toUnlocked(record: AchievementUnlockRecord): AchievementUnlocked? {
        val definition = definitionsById[record.definitionId] ?: return null
        return toUnlocked(record, definition)
    }

    private fun toUnlocked(
        record: AchievementUnlockRecord,
        definition: AchievementDefinition
    ): AchievementUnlocked {
        return AchievementUnlocked(
            definition = definition,
            habitId = record.habitId,
            habitUuid = record.habitUuid,
            habitName = record.habitName,
            streakLength = record.streakLength,
            unlockedAt = record.unlockedAt
        )
    }
}
