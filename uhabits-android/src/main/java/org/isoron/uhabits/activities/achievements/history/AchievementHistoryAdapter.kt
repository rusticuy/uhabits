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

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.isoron.uhabits.core.models.AchievementHistoryItem
import org.isoron.uhabits.core.models.AchievementType
import org.isoron.uhabits.databinding.ItemAchievementEntryBinding
import org.isoron.uhabits.databinding.ItemAchievementHeaderBinding

class AchievementHistoryAdapter(
    private val onAchievementClicked: (AchievementHistoryItem.AchievementEntry) -> Unit
) : ListAdapter<AchievementHistoryItem, RecyclerView.ViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<AchievementHistoryItem>() {
        override fun areItemsTheSame(
            oldItem: AchievementHistoryItem,
            newItem: AchievementHistoryItem
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: AchievementHistoryItem,
            newItem: AchievementHistoryItem
        ): Boolean {
            return when {
                oldItem is AchievementHistoryItem.SectionHeader && 
                newItem is AchievementHistoryItem.SectionHeader -> {
                    oldItem.title == newItem.title && oldItem.count == newItem.count
                }
                oldItem is AchievementHistoryItem.AchievementEntry && 
                newItem is AchievementHistoryItem.AchievementEntry -> {
                    oldItem.achievement == newItem.achievement && 
                    oldItem.habit.name == newItem.habit.name
                }
                else -> false
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is AchievementHistoryItem.SectionHeader -> VIEW_TYPE_HEADER
            is AchievementHistoryItem.AchievementEntry -> VIEW_TYPE_ENTRY
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_HEADER -> {
                val binding = ItemAchievementHeaderBinding.inflate(inflater, parent, false)
                HeaderViewHolder(binding)
            }
            VIEW_TYPE_ENTRY -> {
                val binding = ItemAchievementEntryBinding.inflate(inflater, parent, false)
                EntryViewHolder(binding, onAchievementClicked)
            }
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is HeaderViewHolder -> holder.bind(item as AchievementHistoryItem.SectionHeader)
            is EntryViewHolder -> holder.bind(item as AchievementHistoryItem.AchievementEntry)
        }
    }

    class HeaderViewHolder(
        private val binding: ItemAchievementHeaderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(header: AchievementHistoryItem.SectionHeader) {
            binding.textDate.text = header.title
            binding.textCount.text = binding.root.context.resources.getQuantityString(
                org.isoron.uhabits.R.plurals.achievement_count,
                header.count,
                header.count
            )
        }
    }

    class EntryViewHolder(
        private val binding: ItemAchievementEntryBinding,
        private val onAchievementClicked: (AchievementHistoryItem.AchievementEntry) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(entry: AchievementHistoryItem.AchievementEntry) {
            val achievement = entry.achievement
            val habit = entry.habit
            
            // Set habit name and color
            binding.textHabitName.text = habit.name
            binding.colorIndicator.setBackgroundColor(
                binding.root.context.getColor(
                    when (habit.color.ordinal) {
                        0 -> org.isoron.uhabits.R.color.pallet0
                        1 -> org.isoron.uhabits.R.color.pallet1
                        2 -> org.isoron.uhabits.R.color.pallet2
                        3 -> org.isoron.uhabits.R.color.pallet3
                        4 -> org.isoron.uhabits.R.color.pallet4
                        5 -> org.isoron.uhabits.R.color.pallet5
                        6 -> org.isoron.uhabits.R.color.pallet6
                        7 -> org.isoron.uhabits.R.color.pallet7
                        8 -> org.isoron.uhabits.R.color.pallet8
                        9 -> org.isoron.uhabits.R.color.pallet9
                        10 -> org.isoron.uhabits.R.color.pallet10
                        11 -> org.isoron.uhabits.R.color.pallet11
                        12 -> org.isoron.uhabits.R.color.pallet12
                        13 -> org.isoron.uhabits.R.color.pallet13
                        14 -> org.isoron.uhabits.R.color.pallet14
                        15 -> org.isoron.uhabits.R.color.pallet15
                        16 -> org.isoron.uhabits.R.color.pallet16
                        17 -> org.isoron.uhabits.R.color.pallet17
                        18 -> org.isoron.uhabits.R.color.pallet18
                        19 -> org.isoron.uhabits.R.color.pallet19
                        20 -> org.isoron.uhabits.R.color.pallet20
                        21 -> org.isoron.uhabits.R.color.pallet21
                        22 -> org.isoron.uhabits.R.color.pallet22
                        23 -> org.isoron.uhabits.R.color.pallet23
                        else -> org.isoron.uhabits.R.color.pallet24
                    }
                )
            )
            
            // Set achievement title and description
            binding.textAchievementTitle.text = achievement.title
            binding.textAchievementDescription.text = achievement.description
            
            // Set achievement icon based on type
            binding.imageAchievementIcon.setImageResource(getIconForType(achievement.type))
            
            // Show indicator if unseen
            binding.unseenIndicator.visibility = if (achievement.isSeen) View.GONE else View.VISIBLE
            
            // Set click listener
            binding.root.setOnClickListener {
                onAchievementClicked(entry)
            }
        }
        
        private fun getIconForType(type: AchievementType): Int {
            return when (type) {
                AchievementType.STREAK -> org.isoron.uhabits.R.drawable.ic_streak
                AchievementType.PERFECT_WEEK -> org.isoron.uhabits.R.drawable.ic_calendar_check
                AchievementType.PERFECT_MONTH -> org.isoron.uhabits.R.drawable.ic_calendar_check
                AchievementType.TOTAL_CHECKS -> org.isoron.uhabits.R.drawable.ic_checkmark
                AchievementType.LONGEST_STREAK -> org.isoron.uhabits.R.drawable.ic_trophy
                AchievementType.EARLY_BIRD -> org.isoron.uhabits.R.drawable.ic_sunrise
                AchievementType.NIGHT_OWL -> org.isoron.uhabits.R.drawable.ic_moon
                AchievementType.CONSISTENCY -> org.isoron.uhabits.R.drawable.ic_chart
                AchievementType.MILESTONE -> org.isoron.uhabits.R.drawable.ic_star
            }
        }
    }

    private companion object {
        const val VIEW_TYPE_HEADER = 0
        const val VIEW_TYPE_ENTRY = 1
    }
}