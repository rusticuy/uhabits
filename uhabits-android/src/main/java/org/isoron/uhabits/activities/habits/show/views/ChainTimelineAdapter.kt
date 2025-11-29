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

package org.isoron.uhabits.activities.habits.show.views

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import org.isoron.uhabits.core.ui.screens.habits.show.views.ChainTimelineItem
import org.isoron.uhabits.databinding.ChainTimelineChipBinding
import java.text.SimpleDateFormat
import java.util.Locale

class ChainTimelineAdapter(
    private val context: Context,
    private val items: List<ChainTimelineItem>
) : RecyclerView.Adapter<ChainTimelineAdapter.ViewHolder>() {

    private val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ChainTimelineChipBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(private val binding: ChainTimelineChipBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ChainTimelineItem) {
            val chip = binding.chip
            chip.text = "${item.length} days"
            chip.contentDescription = "Chain from ${dateFormat.format(item.startDate.toJavaLocalDate())} " +
                "to ${dateFormat.format(item.endDate.toJavaLocalDate())} (${item.length} days)"
        }
    }
}

private fun org.isoron.platform.time.LocalDate.toJavaLocalDate(): java.time.LocalDate {
    return java.time.LocalDate.of(year, month, day)
}
