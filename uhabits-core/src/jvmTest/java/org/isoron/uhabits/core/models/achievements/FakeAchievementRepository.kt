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
package org.isoron.uhabits.core.models.achievements

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class FakeAchievementRepository : AchievementRepository {
    private val unlockFlow = MutableSharedFlow<Achievement>(replay = 0)
    private val shownAchievements = mutableListOf<String>()
    private var lastShownId: String? = null
    
    override fun observeUnlocks(): Flow<Achievement> = unlockFlow
    
    override fun markAsShown(achievementId: String) {
        shownAchievements.add(achievementId)
        lastShownId = achievementId
    }
    
    override fun getLastShownId(): String? = lastShownId
    
    suspend fun emitUnlock(achievement: Achievement) {
        unlockFlow.emit(achievement)
    }
    
    fun getShownAchievements(): List<String> = shownAchievements.toList()
}
