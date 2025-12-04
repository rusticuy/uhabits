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

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import org.isoron.uhabits.R
import org.isoron.uhabits.activities.achievements.history.AchievementHistoryActivity

/**
 * Dialog shown when user achieves something significant.
 * Provides option to view achievement history.
 */
class AchievementCelebrationDialog : DialogFragment() {

    interface Listener {
        fun onViewHistoryClicked()
        fun onDismissClicked()
    }

    private var listener: Listener? = null

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = requireContext()
        
        return AlertDialog.Builder(context)
            .setTitle(R.string.achievement_unlocked)
            .setMessage(R.string.achievement_celebration_message)
            .setPositiveButton(R.string.view_history) { _, _ ->
                listener?.onViewHistoryClicked()
            }
            .setNegativeButton(R.string.ok) { _, _ ->
                listener?.onDismissClicked()
            }
            .create()
    }

    companion object {
        fun newInstance(): AchievementCelebrationDialog {
            return AchievementCelebrationDialog()
        }

        fun show(context: Context, listener: Listener) {
            val dialog = newInstance()
            dialog.setListener(listener)
            dialog.show((context as androidx.fragment.app.FragmentActivity).supportFragmentManager, "celebration")
        }
    }
}