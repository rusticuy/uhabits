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
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatDialogFragment
import org.isoron.uhabits.HabitsApplication
import org.isoron.uhabits.databinding.DialogAchievementCelebrationBinding

class CelebrationDialog : AppCompatDialogFragment() {

    private var _binding: DialogAchievementCelebrationBinding? = null
    private val binding get() = _binding!!
    private var confettiController: CelebrationConfettiController? = null

    var state: CelebrationState = CelebrationState()
        set(value) {
            field = value
            updateUI()
        }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val appComponent = (requireActivity().application as HabitsApplication).component
        val preferences = appComponent.preferences
        val themeSwitcher = appComponent.themeSwitcher

        _binding = DialogAchievementCelebrationBinding.inflate(LayoutInflater.from(context))

        confettiController = CelebrationConfettiController(
            konfettiView = binding.konfettiView,
            preferences = preferences,
            themeSwitcher = themeSwitcher,
            contentResolver = requireActivity().contentResolver
        )

        updateUI()

        binding.celebrationDismissBtn.setOnClickListener {
            dismiss()
        }

        val dialog = Dialog(requireContext())
        dialog.setContentView(binding.root)
        dialog.window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)
        }

        return dialog
    }

    override fun onStart() {
        super.onStart()
        confettiController?.onStart(state)
    }

    override fun onStop() {
        super.onStop()
        confettiController?.onStop()
    }

    override fun onDestroyView() {
        confettiController?.onDestroyView()
        confettiController = null
        _binding = null
        super.onDestroyView()
    }

    private fun updateUI() {
        if (_binding == null) return

        binding.celebrationTitle.text = state.title
        binding.celebrationMessage.text = state.message
    }
}
