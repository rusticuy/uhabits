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

package org.isoron.uhabits.activities.achievements.history

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ActivityScoped
import org.isoron.uhabits.core.models.AchievementHistoryPresenter
import org.isoron.uhabits.core.models.AchievementList
import org.isoron.uhabits.core.models.HabitList
import org.isoron.uhabits.core.models.MemoryAchievementList
import javax.inject.Qualifier

@Module
@InstallIn(ActivityComponent::class)
abstract class AchievementHistoryModule {

    @Binds
    @ActivityScoped
    abstract fun bindAchievementList(
        memoryAchievementList: MemoryAchievementList
    ): AchievementList

    companion object {
        @Provides
        @ActivityScoped
        fun provideAchievementHistoryPresenter(
            factory: AchievementHistoryPresenter.Factory,
            screen: AchievementHistoryScreen
        ): AchievementHistoryPresenter {
            return factory.create(screen)
        }
    }
}