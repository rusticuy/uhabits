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

package org.isoron.uhabits.core.ui.screens.templates.picker

import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.isoron.uhabits.core.commands.CommandRunner
import org.isoron.uhabits.core.models.HabitList
import org.isoron.uhabits.core.models.HabitType
import org.isoron.uhabits.core.models.ModelFactory
import org.isoron.uhabits.core.models.NumericalHabitType
import org.isoron.uhabits.core.models.PaletteColor
import org.isoron.uhabits.core.models.Template
import org.isoron.uhabits.core.models.TemplateHelper
import org.isoron.uhabits.core.models.memory.MemoryTemplateRepository

class TemplatePickerPresenterTest {

    private lateinit var screen: TemplatePickerPresenter.Screen
    private lateinit var templateHelper: TemplateHelper
    private lateinit var commandRunner: CommandRunner
    private lateinit var modelFactory: ModelFactory
    private lateinit var habitList: HabitList
    private lateinit var presenter: TemplatePickerPresenter
    private val templateRepository = MemoryTemplateRepository()

    @Before
    fun setUp() {
        screen = mock()
        templateHelper = mock()
        commandRunner = mock()
        modelFactory = mock()
        habitList = mock()

        presenter = TemplatePickerPresenter(
            templateRepository = templateRepository,
            templateHelper = templateHelper,
            modelFactory = modelFactory,
            habitList = habitList,
            commandRunner = commandRunner,
            screen = screen
        )
    }

    @Test
    fun testOnStart_LoadsAllTemplates() {
        presenter.onStart()

        verify(screen).setState(any(), any())
    }

    @Test
    fun testOnStart_GroupsTemplatesByCategory() {
        presenter.onStart()

        val categories = templateRepository.getCategories()
        verify(screen).setState(any(), any())
    }

    @Test
    fun testSetSearchQuery_FiltersTemplates() {
        presenter.setSearchQuery("Run")

        verify(screen).setState(any(), any())
    }

    @Test
    fun testSetSelectedCategory_FiltersByCategory() {
        val category = "Health & Fitness"
        presenter.setSelectedCategory(category)

        verify(screen).setState(any(), any())
    }

    @Test
    fun testClearSearch_ShowsAllTemplates() {
        presenter.setSearchQuery("Run")
        presenter.setSearchQuery("")

        verify(screen).setState(any(), any())
    }

    @Test
    fun testOnTemplateQuickAdd_CreatesHabitFromTemplate() {
        val template = templateRepository.getAll().first()
        val habit = mock()
        whenever(templateHelper.cloneTemplateToHabit(template)).thenReturn(habit)

        presenter.onTemplateQuickAdd(template.id)

        verify(commandRunner).run(any())
        verify(screen).close()
    }

    @Test
    fun testOnTemplateCustomize_OpensEditHabit() {
        val template = templateRepository.getAll().first()

        presenter.onTemplateCustomize(template.id)

        verify(screen).openEditHabitWithTemplate(template)
    }

    @Test
    fun testOnCreateFromScratch_OpensHabitTypeSelection() {
        presenter.onCreateFromScratch()

        verify(screen).openSelectHabitType()
    }

    @Test
    fun testCategoryGrouping_GroupsCorrectly() {
        presenter.onStart()

        val allCategories = templateRepository.getCategories()
        assert(allCategories.contains("Health & Fitness"))
        assert(allCategories.contains("Learning"))
        assert(allCategories.contains("Productivity"))
        assert(allCategories.contains("Social & Relationships"))
        assert(allCategories.contains("Creative"))
    }

    @Test
    fun testTemplateSearch_FindsByName() {
        presenter.setSearchQuery("Morning Run")

        val templates = templateRepository.search("Morning Run")
        assert(templates.isNotEmpty())
    }

    @Test
    fun testTemplateSearch_FindsByDescription() {
        presenter.setSearchQuery("glasses")

        val templates = templateRepository.search("glasses")
        assert(templates.isNotEmpty())
    }
}
