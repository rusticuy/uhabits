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
import androidx.recyclerview.widget.RecyclerView
import org.isoron.uhabits.R
import org.isoron.uhabits.core.models.Achievement
import org.isoron.uhabits.core.models.AchievementList
import org.isoron.uhabits.core.models.HabitList
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AchievementCardListAdapter(
    private val achievementList: AchievementList,
    private val habitList: HabitList,
    private var selectedHabitId: Long? = null
) : RecyclerView.Adapter<AchievementCardViewHolder>() {

    private var achievements = mutableListOf<Achievement>()

    init {
        updateAchievements()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AchievementCardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.achievement_card, parent, false)
        return AchievementCardViewHolder(view, habitList)
    }

    override fun onBindViewHolder(holder: AchievementCardViewHolder, position: Int) {
        holder.bind(achievements[position])
    }

    override fun getItemCount(): Int = achievements.size

    fun setFilterHabit(habitId: Long?) {
        selectedHabitId = habitId
        updateAchievements()
    }

    fun filterByStreakTier(streakTier: Int) {
        achievements = if (streakTier > 0) {
            achievementList.getByStreakTier(streakTier)
                .filter { selectedHabitId == null || it.habitId == selectedHabitId }
                .toMutableList()
        } else {
            if (selectedHabitId != null) {
                achievementList.getByHabit(selectedHabitId!!)
            } else {
                achievementList.getAll()
            }.toMutableList()
        }
        notifyDataSetChanged()
    }

    fun refresh() {
        updateAchievements()
    }

    private fun updateAchievements() {
        achievements = if (selectedHabitId != null) {
            achievementList.getByHabit(selectedHabitId!!)
        } else {
            achievementList.getAll()
        }.toMutableList()
        notifyDataSetChanged()
    }
}

class AchievementCardViewHolder(
    private val itemView: View,
    private val habitList: HabitList
) : RecyclerView.ViewHolder(itemView) {

    private val achievementIcon: android.widget.ImageView = itemView.findViewById(R.id.achievementIcon)
    private val achievementStreakTier: android.widget.TextView = itemView.findViewById(R.id.achievementStreakTier)
    private val achievementStatus: android.widget.TextView = itemView.findViewById(R.id.achievementStatus)
    private val achievementStatusContainer: android.widget.LinearLayout = itemView.findViewById(R.id.achievementStatusContainer)
    private val achievementHabitName: android.widget.TextView = itemView.findViewById(R.id.achievementHabitName)
    private val achievementProgress: android.widget.ProgressBar = itemView.findViewById(R.id.achievementProgress)

    fun bind(achievement: Achievement) {
        val habit = achievement.habitId?.let { habitList.getById(it) }
        val habitName = habit?.name ?: "Unknown Habit"
        val streakTierLabel = achievement.streakTier.toString()
        val context = itemView.context

        achievementStreakTier.text = context.getString(
            R.string.achievement_streak_tier,
            streakTierLabel
        )
        achievementHabitName.text = habitName

        if (achievement.isUnlocked && achievement.unlockedAt != null) {
            achievementStatus.text = context.getString(
                R.string.achievement_unlocked_at,
                formatDate(achievement.unlockedAt!!)
            )
            achievementProgress.visibility = View.GONE
            achievementStatusContainer.setBackgroundColor(
                context.getColor(R.color.achievement_unlocked_bg)
            )
        } else {
            achievementStatus.text = context.getString(R.string.achievement_locked)
            achievementProgress.visibility = View.VISIBLE
            achievementStatusContainer.setBackgroundColor(
                context.getColor(R.color.achievement_locked_bg)
            )
        }

        val iconDrawableId = getAchievementIconForStreakTier(achievement.streakTier)
        achievementIcon.setImageDrawable(
            context.getDrawable(iconDrawableId)
        )
    }

    private fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    private fun getAchievementIconForStreakTier(streakTier: Int): Int {
        return when {
            streakTier >= 365 -> R.drawable.ic_achievement_365
            streakTier >= 100 -> R.drawable.ic_achievement_100
            streakTier >= 50 -> R.drawable.ic_achievement_50
            streakTier >= 20 -> R.drawable.ic_achievement_20
            streakTier >= 7 -> R.drawable.ic_achievement_7
            else -> R.drawable.ic_achievement_1
        }
    }
}
