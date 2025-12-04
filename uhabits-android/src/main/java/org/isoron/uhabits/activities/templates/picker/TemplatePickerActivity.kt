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

package org.isoron.uhabits.activities.templates.picker

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.recyclerview.widget.LinearLayoutManager
import org.isoron.uhabits.HabitsApplication
import org.isoron.uhabits.R
import org.isoron.uhabits.activities.habits.edit.EditHabitActivity
import org.isoron.uhabits.activities.habits.edit.HabitTypeDialog
import org.isoron.uhabits.core.models.HabitType
import org.isoron.uhabits.core.models.Template
import org.isoron.uhabits.core.ui.screens.templates.picker.TemplateCategoryState
import org.isoron.uhabits.core.ui.screens.templates.picker.TemplatePickerPresenter
import org.isoron.uhabits.databinding.ActivityTemplatePickerBinding
import org.isoron.uhabits.inject.ActivityContextModule
import org.isoron.uhabits.inject.DaggerHabitsActivityComponent
import org.isoron.uhabits.inject.HabitsActivityComponent
import org.isoron.uhabits.utils.EdgeToEdge
import org.isoron.uhabits.utils.applyRootViewInsets
import org.isoron.uhabits.utils.applyToolbarInsets

class TemplatePickerActivity : AppCompatActivity(), TemplatePickerPresenter.Screen {

    private lateinit var binding: ActivityTemplatePickerBinding
    private lateinit var appComponent: HabitsApplication
    private lateinit var component: HabitsActivityComponent
    private lateinit var presenter: TemplatePickerPresenter
    private lateinit var adapter: TemplateCategoryAdapter
    private var allCategories: List<String> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = (application as HabitsApplication)
        appComponent = app
        component = DaggerHabitsActivityComponent
            .builder()
            .activityContextModule(ActivityContextModule(this))
            .habitsApplicationComponent(app.component)
            .build()
        component.themeSwitcher.apply()
        EdgeToEdge.enableEdgeToEdge()

        binding = ActivityTemplatePickerBinding.inflate(layoutInflater)
        binding.root.applyRootViewInsets()
        binding.toolbar.applyToolbarInsets()
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.title = getString(R.string.templates)

        presenter = TemplatePickerPresenter(
            templateRepository = app.component.templateRepository,
            templateHelper = app.component.templateHelper,
            modelFactory = app.component.modelFactory,
            habitList = app.component.habitList,
            commandRunner = app.component.commandRunner,
            screen = this
        )

        adapter = TemplateCategoryAdapter(
            onQuickAdd = { presenter.onTemplateQuickAdd(it) },
            onCustomize = { presenter.onTemplateCustomize(it) }
        )

        binding.recyclerview.layoutManager = LinearLayoutManager(this)
        binding.recyclerview.adapter = adapter

        binding.createFromScratchButton.setOnClickListener {
            presenter.onCreateFromScratch()
        }

        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.template_picker, menu)
                val searchItem = menu.findItem(R.id.action_search)
                val searchView = searchItem?.actionView as? SearchView
                searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean = false
                    override fun onQueryTextChange(newText: String?): Boolean {
                        presenter.setSearchQuery(newText ?: "")
                        return true
                    }
                })
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    android.R.id.home -> {
                        finish()
                        true
                    }
                    else -> false
                }
            }
        })

        presenter.onStart()
    }

    override fun setState(categories: List<TemplateCategoryState>, availableCategories: List<String>) {
        this.allCategories = availableCategories
        adapter.submitList(categories)
    }

    override fun close() {
        finish()
    }

    override fun openEditHabitWithTemplate(template: Template) {
        val intent = Intent(this, EditHabitActivity::class.java).apply {
            putExtra("habitType", template.habitType.value)
            putExtra("templateColor", template.color.paletteIndex)
            putExtra("templateName", template.name)
            putExtra("templateDescription", template.description)
            putExtra("templateFreqNum", template.frequencyNum)
            putExtra("templateFreqDen", template.frequencyDen)
            putExtra("templateUnit", template.unit)
            putExtra("templateTargetValue", template.targetValue)
            putExtra("templateTargetType", template.targetType.value)
        }
        startActivity(intent)
        finish()
    }

    override fun openSelectHabitType() {
        val dialog = HabitTypeDialog()
        dialog.setListener { habitType: HabitType ->
            val intent = Intent(this, EditHabitActivity::class.java).apply {
                putExtra("habitType", habitType.value)
            }
            startActivity(intent)
            finish()
        }
        dialog.show(supportFragmentManager, "habitTypeDialog")
    }
}
