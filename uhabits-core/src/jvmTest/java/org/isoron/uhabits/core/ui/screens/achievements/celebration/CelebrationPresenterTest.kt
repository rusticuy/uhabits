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
package org.isoron.uhabits.core.ui.screens.achievements.celebration

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.isoron.uhabits.core.BaseUnitTest
import org.isoron.uhabits.core.models.achievements.Achievement
import org.isoron.uhabits.core.models.achievements.AchievementType
import org.isoron.uhabits.core.models.achievements.FakeAchievementRepository
import org.isoron.uhabits.core.preferences.MemoryPreferencesStorage
import org.isoron.uhabits.core.preferences.Preferences
import org.isoron.uhabits.core.utils.FakeClock
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify

@OptIn(ExperimentalCoroutinesApi::class)
class CelebrationPresenterTest : BaseUnitTest() {
    private lateinit var presenter: CelebrationPresenter
    private lateinit var repository: FakeAchievementRepository
    private lateinit var preferences: Preferences
    private lateinit var clock: FakeClock
    private lateinit var screen: CelebrationPresenter.Screen
    private lateinit var testScope: TestScope
    
    @Before
    override fun setUp() {
        super.setUp()
        repository = FakeAchievementRepository()
        preferences = Preferences(MemoryPreferencesStorage())
        clock = FakeClock(System.currentTimeMillis())
        screen = mock()
        testScope = TestScope(StandardTestDispatcher())
    }
    
    @Test
    fun `emitting unlock produces expected state`() = runTest {
        presenter = createPresenter()
        
        val achievement = createTestAchievement("test_1", "First Achievement")
        
        repository.emitUnlock(achievement)
        advanceUntilIdle()
        
        val state = presenter.state.value
        assertThat(state, notNullValue())
        assertThat(state?.achievementId, equalTo("test_1"))
        assertThat(state?.title, equalTo("Achievement Unlocked!"))
        assertThat(state?.subtitle, equalTo("First Achievement"))
        assertThat(state?.description, equalTo("Test description"))
        assertThat(state?.showShareButton, equalTo(true))
        assertThat(state?.showHistoryButton, equalTo(true))
        assertThat(state?.playConfetti, equalTo(true))
    }
    
    @Test
    fun `acknowledge marks achievement as shown and clears state`() = runTest {
        presenter = createPresenter()
        
        val achievement = createTestAchievement("test_1", "First Achievement")
        repository.emitUnlock(achievement)
        advanceUntilIdle()
        
        assertThat(presenter.state.value, notNullValue())
        
        presenter.acknowledge()
        advanceUntilIdle()
        
        assertThat(presenter.state.value, nullValue())
        assertThat(repository.getShownAchievements(), equalTo(listOf("test_1")))
        verify(screen).dismiss()
    }
    
    @Test
    fun `same achievement is not shown twice`() = runTest {
        presenter = createPresenter()
        
        val achievement = createTestAchievement("test_1", "First Achievement")
        
        repository.emitUnlock(achievement)
        advanceUntilIdle()
        assertThat(presenter.state.value, notNullValue())
        
        presenter.acknowledge()
        advanceUntilIdle()
        assertThat(presenter.state.value, nullValue())
        
        repository.emitUnlock(achievement)
        advanceUntilIdle()
        assertThat(presenter.state.value, nullValue())
    }
    
    @Test
    fun `achievement shown in previous session is not shown again`() = runTest {
        val achievement = createTestAchievement("test_1", "First Achievement")
        repository.markAsShown("test_1")
        
        presenter = createPresenter()
        
        repository.emitUnlock(achievement)
        advanceUntilIdle()
        
        assertThat(presenter.state.value, nullValue())
        verify(screen, never()).dismiss()
    }
    
    @Test
    fun `confetti is disabled when preference is set`() = runTest {
        preferences.isConfettiAnimationDisabled = true
        presenter = createPresenter()
        
        val achievement = createTestAchievement("test_1", "First Achievement")
        repository.emitUnlock(achievement)
        advanceUntilIdle()
        
        val state = presenter.state.value
        assertThat(state, notNullValue())
        assertThat(state?.playConfetti, equalTo(false))
    }
    
    @Test
    fun `confetti is enabled when preference is not set`() = runTest {
        preferences.isConfettiAnimationDisabled = false
        presenter = createPresenter()
        
        val achievement = createTestAchievement("test_1", "First Achievement")
        repository.emitUnlock(achievement)
        advanceUntilIdle()
        
        val state = presenter.state.value
        assertThat(state, notNullValue())
        assertThat(state?.playConfetti, equalTo(true))
    }
    
    @Test
    fun `onShareTapped triggers share with correct text`() = runTest {
        presenter = createPresenter()
        
        val achievement = createTestAchievement("test_1", "First Achievement")
        repository.emitUnlock(achievement)
        advanceUntilIdle()
        
        presenter.onShareTapped()
        
        verify(screen).shareText(any())
    }
    
    @Test
    fun `onHistoryTapped shows achievement history`() = runTest {
        presenter = createPresenter()
        
        val achievement = createTestAchievement("test_1", "First Achievement")
        repository.emitUnlock(achievement)
        advanceUntilIdle()
        
        presenter.onHistoryTapped()
        
        verify(screen).showAchievementHistory()
    }
    
    @Test
    fun `state contains correct share text`() = runTest {
        presenter = createPresenter()
        
        val achievement = createTestAchievement("test_1", "First Achievement")
        repository.emitUnlock(achievement)
        advanceUntilIdle()
        
        val state = presenter.state.value
        assertThat(state, notNullValue())
        assertThat(
            state?.shareText,
            equalTo("I just unlocked the \"First Achievement\" achievement in Loop Habit Tracker! 🎉")
        )
    }
    
    @Test
    fun `multiple different achievements are shown in sequence`() = runTest {
        presenter = createPresenter()
        
        val achievement1 = createTestAchievement("test_1", "First Achievement")
        repository.emitUnlock(achievement1)
        advanceUntilIdle()
        
        assertThat(presenter.state.value?.achievementId, equalTo("test_1"))
        
        presenter.acknowledge()
        advanceUntilIdle()
        
        val achievement2 = createTestAchievement("test_2", "Second Achievement")
        repository.emitUnlock(achievement2)
        advanceUntilIdle()
        
        assertThat(presenter.state.value?.achievementId, equalTo("test_2"))
        assertThat(repository.getShownAchievements(), equalTo(listOf("test_1", "test_2")))
    }
    
    @Test
    fun `auto dismiss triggers after delay`() = runTest {
        presenter = createPresenter()
        
        val achievement = createTestAchievement("test_1", "First Achievement")
        repository.emitUnlock(achievement)
        advanceUntilIdle()
        
        assertThat(presenter.state.value, notNullValue())
        
        advanceTimeBy(5000L)
        
        assertThat(presenter.state.value, nullValue())
        assertThat(repository.getShownAchievements(), equalTo(listOf("test_1")))
        verify(screen).dismiss()
    }
    
    @Test
    fun `auto dismiss does not trigger if manually acknowledged first`() = runTest {
        presenter = createPresenter()
        
        val achievement = createTestAchievement("test_1", "First Achievement")
        repository.emitUnlock(achievement)
        advanceUntilIdle()
        
        presenter.acknowledge()
        advanceUntilIdle()
        
        verify(screen).dismiss()
        
        advanceTimeBy(5000L)
        
        assertThat(repository.getShownAchievements().size, equalTo(1))
    }
    
    @Test
    fun `state contains confetti colors`() = runTest {
        presenter = createPresenter()
        
        val achievement = createTestAchievement("test_1", "First Achievement")
        repository.emitUnlock(achievement)
        advanceUntilIdle()
        
        val state = presenter.state.value
        assertThat(state, notNullValue())
        assertThat(state?.confettiColors?.isNotEmpty(), equalTo(true))
    }
    
    private fun createPresenter(): CelebrationPresenter {
        return CelebrationPresenter(repository, preferences, clock, screen)
    }
    
    private fun createTestAchievement(
        id: String,
        title: String,
        type: AchievementType = AchievementType.FIRST_HABIT
    ): Achievement {
        return Achievement(
            id = id,
            type = type,
            title = title,
            description = "Test description",
            iconId = 1,
            unlockedAt = clock.currentTimeMillis()
        )
    }
}
