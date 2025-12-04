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

package org.isoron.uhabits.activities.achievements.celebration

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import org.isoron.uhabits.HabitsApplication
import org.isoron.uhabits.R
import org.isoron.uhabits.core.models.PaletteColor
import org.isoron.uhabits.core.ui.ThemeSwitcher
import org.isoron.uhabits.core.ui.screens.achievements.celebration.AchievementType
import org.isoron.uhabits.core.ui.screens.achievements.celebration.CelebrationPresenter
import org.isoron.uhabits.core.ui.screens.achievements.celebration.CelebrationState
import org.isoron.uhabits.databinding.DialogAchievementCelebrationBinding
import org.isoron.uhabits.utils.ColorUtils
import org.isoron.uhabits.utils.InterfaceUtils
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AchievementCelebrationDialog : AppCompatDialogFragment() {

    @Inject
    lateinit var presenter: CelebrationPresenter

    @Inject
    lateinit var themeSwitcher: ThemeSwitcher

    private var _binding: DialogAchievementCelebrationBinding? = null
    private val binding get() = _binding!!

    private var screenCallback: ScreenCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appComponent = (requireActivity().application as HabitsApplication).component
        appComponent.inject(this)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAchievementCelebrationBinding.inflate(LayoutInflater.from(context))
        
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .setBackgroundInsetStart(0)
            .setBackgroundInsetEnd(0)
            .setBackgroundInsetTop(0)
            .setBackgroundInsetBottom(0)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        
        setupClickListeners()
        observePresenterState()
        
        return dialog
    }

    private fun setupClickListeners() {
        binding.shareButton.setOnClickListener {
            presenter.onShareClicked()
        }

        binding.historyButton.setOnClickListener {
            presenter.onViewHistoryClicked()
        }

        binding.dismissButton.setOnClickListener {
            presenter.hideCelebration()
            dismiss()
        }
    }

    private fun observePresenterState() {
        presenter.state.onEach { state ->
            updateUI(state)
            
            if (!state.isVisible) {
                dismiss()
            }
            
            if (state.shareRequested) {
                handleShare(state)
                presenter.onShareHandled()
            }
            
            if (state.historyRequested) {
                handleHistory(state)
                presenter.onHistoryHandled()
            }
        }.launchIn(lifecycleScope)
    }

    private fun updateUI(state: CelebrationState) {
        binding.subtitleText.text = getAchievementTitle(state.achievementType)
        binding.descriptionText.text = state.description
        
        // Update icon and colors based on achievement type
        updateAchievementIcon(state.achievementType, state.habitColor)
        
        // Start confetti if visible
        if (state.isVisible) {
            startConfetti(state.habitColor)
        }
    }

    private fun updateAchievementIcon(achievementType: AchievementType, color: PaletteColor) {
        val theme = themeSwitcher.currentTheme
        val colorInt = theme.color(color).toInt()
        
        val iconRes = when (achievementType) {
            AchievementType.STREAK -> R.drawable.ic_trophy
            AchievementType.PERFECT_WEEK -> R.drawable.ic_star
            AchievementType.PERFECT_MONTH -> R.drawable.ic_star
            AchievementType.MILESTONE -> R.drawable.ic_milestone
        }
        
        binding.achievementIcon.setImageResource(iconRes)
        binding.achievementIcon.backgroundTintList = InterfaceUtils.getColorStateList(colorInt)
    }

    private fun getAchievementTitle(achievementType: AchievementType): String {
        return when (achievementType) {
            AchievementType.STREAK -> getString(R.string.streak_achieved)
            AchievementType.PERFECT_WEEK -> getString(R.string.perfect_week_achieved)
            AchievementType.PERFECT_MONTH -> getString(R.string.perfect_month_achieved)
            AchievementType.MILESTONE -> getString(R.string.milestone_achieved)
        }
    }

    private fun startConfetti(color: PaletteColor) {
        val theme = themeSwitcher.currentTheme
        val baseColor = theme.color(color).toInt()
        
        binding.konfettiView.start(
            Party(
                speed = 0f,
                maxSpeed = 16f,
                damping = 0.9f,
                spread = 360,
                angle = 0,
                colors = listOf(
                    ColorUtils.changeHue(baseColor, 180f),
                    ColorUtils.changeHue(baseColor, 20f),
                    ColorUtils.changeHue(baseColor, -20f),
                    baseColor
                ),
                position = Position.Relative(0.5, 0.5),
                emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(50),
                timeToLive = 0
            )
        )
    }

    private fun handleShare(state: CelebrationState) {
        val shareText = buildShareText(state)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.achievement_share_subject))
        }
        
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_achievement)))
    }

    private fun handleHistory(state: CelebrationState) {
        screenCallback?.onViewHistoryRequested(state.habitName)
    }

    private fun buildShareText(state: CelebrationState): String {
        val achievementName = getAchievementTitle(state.achievementType)
        return getString(R.string.achievement_share_text, achievementName, state.habitName, state.description)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setScreenCallback(callback: ScreenCallback) {
        screenCallback = callback
    }

    interface ScreenCallback {
        fun onViewHistoryRequested(habitName: String)
    }
}