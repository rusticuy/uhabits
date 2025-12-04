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

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.isoron.uhabits.core.ui.screens.templates.picker.TemplateCategoryState
import org.isoron.uhabits.core.ui.screens.templates.picker.TemplateState
import org.isoron.uhabits.databinding.ItemTemplateCategoryBinding
import org.isoron.uhabits.databinding.ItemTemplateBinding

class TemplateCategoryAdapter(
    private val onQuickAdd: (String) -> Unit,
    private val onCustomize: (String) -> Unit
) : ListAdapter<TemplateCategoryState, TemplateCategoryAdapter.CategoryViewHolder>(CategoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemTemplateCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding, onQuickAdd, onCustomize)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CategoryViewHolder(
        private val binding: ItemTemplateCategoryBinding,
        private val onQuickAdd: (String) -> Unit,
        private val onCustomize: (String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(category: TemplateCategoryState) {
            binding.categoryTitle.text = category.categoryName

            val templateAdapter = TemplateAdapter(onQuickAdd, onCustomize)
            binding.templatesRecyclerview.layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
            binding.templatesRecyclerview.adapter = templateAdapter
            templateAdapter.submitList(category.templates)
        }
    }

    private class CategoryDiffCallback : DiffUtil.ItemCallback<TemplateCategoryState>() {
        override fun areItemsTheSame(oldItem: TemplateCategoryState, newItem: TemplateCategoryState) =
            oldItem.categoryName == newItem.categoryName

        override fun areContentsTheSame(oldItem: TemplateCategoryState, newItem: TemplateCategoryState) =
            oldItem == newItem
    }
}

class TemplateAdapter(
    private val onQuickAdd: (String) -> Unit,
    private val onCustomize: (String) -> Unit
) : ListAdapter<TemplateState, TemplateAdapter.TemplateViewHolder>(TemplateDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TemplateViewHolder {
        val binding = ItemTemplateBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TemplateViewHolder(binding, onQuickAdd, onCustomize)
    }

    override fun onBindViewHolder(holder: TemplateViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class TemplateViewHolder(
        private val binding: ItemTemplateBinding,
        private val onQuickAdd: (String) -> Unit,
        private val onCustomize: (String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(template: TemplateState) {
            binding.templateIcon.text = template.icon
            binding.templateName.text = template.name
            binding.templateDescription.text = template.description

            binding.quickAddButton.setOnClickListener {
                onQuickAdd(template.id)
            }

            binding.customizeButton.setOnClickListener {
                onCustomize(template.id)
            }
        }
    }

    private class TemplateDiffCallback : DiffUtil.ItemCallback<TemplateState>() {
        override fun areItemsTheSame(oldItem: TemplateState, newItem: TemplateState) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: TemplateState, newItem: TemplateState) =
            oldItem == newItem
    }
}
