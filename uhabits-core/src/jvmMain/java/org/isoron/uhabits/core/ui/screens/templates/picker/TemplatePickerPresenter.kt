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

import org.isoron.uhabits.core.commands.CommandRunner
import org.isoron.uhabits.core.commands.CreateHabitCommand
import org.isoron.uhabits.core.models.HabitList
import org.isoron.uhabits.core.models.ModelFactory
import org.isoron.uhabits.core.models.Template
import org.isoron.uhabits.core.models.TemplateHelper
import org.isoron.uhabits.core.models.TemplateRepository
import javax.inject.Inject

data class TemplateCategoryState(
    val categoryName: String,
    val templates: List<TemplateState>
)

data class TemplateState(
    val id: String,
    val name: String,
    val description: String,
    val icon: String,
    val category: String
)

class TemplatePickerPresenter @Inject constructor(
    private val templateRepository: TemplateRepository,
    private val templateHelper: TemplateHelper,
    private val modelFactory: ModelFactory,
    private val habitList: HabitList,
    private val commandRunner: CommandRunner,
    private val screen: Screen
) {
    private var currentSearch: String = ""
    private var selectedCategory: String? = null

    fun onStart() {
        updateDisplay()
    }

    fun setSearchQuery(query: String) {
        currentSearch = query
        updateDisplay()
    }

    fun setSelectedCategory(category: String?) {
        selectedCategory = category
        updateDisplay()
    }

    fun onTemplateQuickAdd(templateId: String) {
        val template = templateRepository.getById(templateId) ?: return
        val habit = templateHelper.cloneTemplateToHabit(template)
        val command = CreateHabitCommand(
            modelFactory,
            habitList,
            habit
        )
        commandRunner.run(command)
        screen.close()
    }

    fun onTemplateCustomize(templateId: String) {
        val template = templateRepository.getById(templateId) ?: return
        screen.openEditHabitWithTemplate(template)
    }

    fun onCreateFromScratch() {
        screen.openSelectHabitType()
    }

    private fun updateDisplay() {
        val templates = if (currentSearch.isNotEmpty()) {
            templateRepository.search(currentSearch)
        } else {
            templateRepository.getAll()
        }

        val filteredTemplates = if (selectedCategory != null) {
            templates.filter { it.category == selectedCategory }
        } else {
            templates
        }

        val grouped = filteredTemplates
            .groupBy { it.category }
            .toSortedMap()

        val categories = if (currentSearch.isEmpty()) {
            templateRepository.getCategories()
        } else {
            grouped.keys.toList()
        }

        val state = grouped.map { (category, categoryTemplates) ->
            TemplateCategoryState(
                categoryName = category,
                templates = categoryTemplates.map {
                    TemplateState(
                        id = it.id,
                        name = it.name,
                        description = it.description,
                        icon = it.icon,
                        category = it.category
                    )
                }
            )
        }

        screen.setState(state, categories)
    }

    interface Screen {
        fun setState(categories: List<TemplateCategoryState>, availableCategories: List<String>)
        fun close()
        fun openEditHabitWithTemplate(template: Template)
        fun openSelectHabitType()
    }
}
