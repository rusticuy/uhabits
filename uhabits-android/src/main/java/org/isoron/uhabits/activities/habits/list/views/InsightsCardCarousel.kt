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
package org.isoron.uhabits.activities.habits.list.views

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import org.isoron.uhabits.R
import org.isoron.uhabits.core.insights.Insight
import org.isoron.uhabits.inject.ActivityContext
import org.isoron.uhabits.inject.ActivityScope
import javax.inject.Inject

@ActivityScope
class InsightsCardCarousel @Inject constructor(
    @ActivityContext context: Context
) : RecyclerView(context) {

    private val adapter = InsightsAdapter()
    var onInsightClickListener: ((Insight) -> Unit)? = null
    var onApplySuggestionListener: ((Insight) -> Unit)? = null
    var onViewDetailsClickListener: (() -> Unit)? = null

    init {
        layoutManager = LinearLayoutManager(context, HORIZONTAL, false)
        setAdapter(adapter)
        clipToPadding = false
        setPadding(8, 8, 8, 8)
    }

    fun setInsights(insights: List<Insight>) {
        adapter.insights = insights
        adapter.notifyDataSetChanged()
        visibility = if (insights.isEmpty()) GONE else VISIBLE
    }

    private inner class InsightsAdapter : RecyclerView.Adapter<InsightViewHolder>() {
        var insights: List<Insight> = emptyList()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InsightViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.insight_card, parent, false)
            return InsightViewHolder(view)
        }

        override fun onBindViewHolder(holder: InsightViewHolder, position: Int) {
            holder.bind(insights[position])
        }

        override fun getItemCount() = insights.size
    }

    private inner class InsightViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val titleView: TextView = view.findViewById(R.id.insightTitle)
        private val bodyView: TextView = view.findViewById(R.id.insightBody)
        private val viewDetailsButton: MaterialButton = view.findViewById(R.id.viewDetailsButton)
        private val applySuggestionButton: MaterialButton = view.findViewById(R.id.applySuggestionButton)

        fun bind(insight: Insight) {
            titleView.text = insight.title
            bodyView.text = insight.body

            viewDetailsButton.setOnClickListener {
                onViewDetailsClickListener?.invoke()
            }

            if (insight.suggestion != null) {
                applySuggestionButton.visibility = VISIBLE
                applySuggestionButton.setOnClickListener {
                    onApplySuggestionListener?.invoke(insight)
                }
            } else {
                applySuggestionButton.visibility = GONE
            }

            itemView.setOnClickListener {
                onInsightClickListener?.invoke(insight)
            }
        }
    }
}
