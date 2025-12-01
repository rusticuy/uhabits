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
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.isoron.uhabits.achievements

import org.isoron.uhabits.core.AppScope
import org.isoron.uhabits.core.commands.Command
import org.isoron.uhabits.core.commands.CommandRunner
import org.isoron.uhabits.core.commands.CreateRepetitionCommand
import org.isoron.uhabits.core.models.HabitList
import org.isoron.uhabits.core.models.achievements.AchievementDetector
import javax.inject.Inject

/**
 * Listens for command events and triggers achievement detection.
 */
@AppScope
class AchievementCommandListener
@Inject constructor(
    private val achievementDetector: AchievementDetector,
    private val habitList: HabitList
) : CommandRunner.Listener {

    private var isListening = false

    fun startListening() {
        if (!isListening) {
            isListening = true
        }
    }

    fun stopListening() {
        if (isListening) {
            isListening = false
        }
    }

    override fun onCommandFinished(command: Command) {
        if (!isListening) return
        
        when (command) {
            is CreateRepetitionCommand -> {
                // Trigger achievement detection when a repetition is created
                achievementDetector.onHabitChecked(
                    command.habit,
                    command.timestamp,
                    habitList
                )
            }
        }
    }
}