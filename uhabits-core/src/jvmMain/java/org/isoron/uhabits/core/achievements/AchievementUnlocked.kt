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

import org.isoron.uhabits.core.models.PaletteColor

/**
 * Represents a user achievement that has just been unlocked.
 *
 * The [iconKey] can be mapped by the platform layer to a drawable or lottie
 * asset. When no mapping exists, the UI should fall back to a generic trophy
 * icon.
 */
data class AchievementUnlocked(
    val id: String,
    val title: String,
    val description: String,
    val streakLength: Int,
    val habitName: String,
    val paletteColor: PaletteColor,
    val iconKey: String = "trophy"
)
