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

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.isoron.uhabits.HabitsApplication
import org.isoron.uhabits.R
import org.isoron.uhabits.core.models.AchievementFilter
import org.isoron.uhabits.core.models.AchievementHistoryItem
import org.isoron.uhabits.databinding.ActivityAchievementHistoryBinding
import org.isoron.uhabits.utils.applyRootViewInsets
import org.isoron.uhabits.utils.enableEdgeToEdge
import javax.inject.Inject

@AndroidEntryPoint
class AchievementHistoryActivity : AppCompatActivity() {

    @Inject
    lateinit var presenter: AchievementHistoryPresenter
    @Inject
    lateinit var themeSwitcher: org.isoron.uhabits.core.ui.ThemeSwitcher
    
    private lateinit var binding: ActivityAchievementHistoryBinding
    private lateinit var adapter: AchievementHistoryAdapter
    private var selectedFilter: AchievementFilter = AchievementFilter.ALL
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Setup view binding
        binding = ActivityAchievementHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.root.applyRootViewInsets()
        
        // Apply theme
        themeSwitcher.apply()
        enableEdgeToEdge()
        
        // Setup RecyclerView
        setupRecyclerView()
        
        // Setup filter chips
        setupFilterChips()
        
        // Setup toolbar
        setupToolbar()
        
        // Restore state if available
        savedInstanceState?.let {
            selectedFilter = AchievementFilter.valueOf(
                it.getString(KEY_SELECTED_FILTER, AchievementFilter.ALL.name)
            )
        }
        
        // Start observing state
        observeState()
    }
    
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_SELECTED_FILTER, selectedFilter.name)
    }
    
    private fun setupRecyclerView() {
        adapter = AchievementHistoryAdapter { achievement ->
            presenter.onAchievementClicked(achievement)
        }
        
        binding.recyclerViewAchievements.apply {
            layoutManager = LinearLayoutManager(this@AchievementHistoryActivity)
            adapter = this@AchievementHistoryActivity.adapter
        }
    }
    
    private fun setupFilterChips() {
        binding.chipGroupFilter.setOnCheckedChangeListener { _, checkedId ->
            val filter = when (checkedId) {
                R.id.chipAll -> AchievementFilter.ALL
                R.id.chipStreaks -> AchievementFilter.STREAKS
                R.id.chipPerfect -> AchievementFilter.PERFECT
                R.id.chipMilestones -> AchievementFilter.MILESTONES
                R.id.chipUnseen -> AchievementFilter.UNSEEN
                else -> AchievementFilter.ALL
            }
            
            if (filter != selectedFilter) {
                selectedFilter = filter
                presenter.onFilterChanged(filter)
            }
        }
        
        // Set initial selection
        when (selectedFilter) {
            AchievementFilter.ALL -> binding.chipAll.isChecked = true
            AchievementFilter.STREAKS -> binding.chipStreaks.isChecked = true
            AchievementFilter.PERFECT -> binding.chipPerfect.isChecked = true
            AchievementFilter.MILESTONES -> binding.chipMilestones.isChecked = true
            AchievementFilter.UNSEEN -> binding.chipUnseen.isChecked = true
        }
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = getString(R.string.achievement_history)
        }
    }
    
    private fun observeState() {
        lifecycleScope.launch {
            presenter.state.collect { state ->
                updateUI(state)
            }
        }
    }
    
    private fun updateUI(state: AchievementHistoryState) {
        // Update loading state
        binding.progressBar.isVisible = state.isLoading
        
        // Update empty state
        binding.layoutEmptyState.isVisible = state.isEmpty && !state.isLoading
        binding.recyclerViewAchievements.isVisible = !state.isEmpty && !state.isLoading
        
        // Update adapter
        adapter.submitList(state.achievements)
        
        // Mark all as seen if this is the first time viewing
        if (state.achievements.any { it is AchievementHistoryItem.AchievementEntry && !it.achievement.isSeen }) {
            presenter.markAllAsSeen()
        }
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
    
    companion object {
        private const val KEY_SELECTED_FILTER = "selected_filter"
    }
}