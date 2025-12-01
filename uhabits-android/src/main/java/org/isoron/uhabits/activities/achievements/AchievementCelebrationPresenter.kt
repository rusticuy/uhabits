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

package org.isoron.uhabits.activities.achievements

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import java.util.ArrayDeque
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import org.isoron.uhabits.R
import org.isoron.uhabits.activities.habits.list.ListHabitsRootView
import org.isoron.uhabits.core.achievements.AchievementDetector
import org.isoron.uhabits.core.achievements.AchievementUnlocked
import org.isoron.uhabits.core.models.PaletteColor
import org.isoron.uhabits.core.preferences.Preferences
import org.isoron.uhabits.core.ui.ThemeSwitcher
import org.isoron.uhabits.inject.ActivityContext
import org.isoron.uhabits.inject.ActivityScope
import org.isoron.uhabits.utils.ColorUtils

@ActivityScope
class AchievementCelebrationPresenter @Inject constructor(
    @ActivityContext context: Context,
    private val detector: AchievementDetector,
    private val preferences: Preferences,
    private val themeSwitcher: ThemeSwitcher,
    private val rootView: ListHabitsRootView
) {
    private val activity = context as AppCompatActivity
    private val queue = ArrayDeque<AchievementUnlocked>()
    private val fallbackNotifiedIds = mutableSetOf<String>()
    private var currentDialog: AlertDialog? = null
    private var isAttached = false
    private var isShowing = false

    private val listener = AchievementDetector.Listener { event ->
        activity.runOnUiThread {
            queue.addLast(event)
            maybeShowNext()
        }
    }

    fun attach() {
        if (isAttached) return
        isAttached = true
        detector.addListener(listener)
        maybeShowNext()
    }

    fun detach() {
        if (!isAttached) return
        isAttached = false
        detector.removeListener(listener)
        dismissDialogSafely(advanceQueue = false)
    }

    private fun maybeShowNext() {
        if (!isAttached) return
        if (isShowing) return
        val next = queue.firstOrNull() ?: return
        if (!canShowCelebration()) {
            if (fallbackNotifiedIds.add(next.id)) {
                showFallback(next)
            }
            return
        }
        showCelebration(next)
    }

    private fun canShowCelebration(): Boolean {
        if (activity.isFinishing || activity.isDestroyed) return false
        return activity.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)
    }

    private fun showCelebration(event: AchievementUnlocked) {
        val inflater = LayoutInflater.from(activity)
        val view = inflater.inflate(R.layout.dialog_achievement_celebration, rootView, false)

        view.findViewById<ImageView>(R.id.celebrationIcon).setImageResource(resolveIcon(event.iconKey))
        view.findViewById<TextView>(R.id.celebrationTitle).text = event.title
        view.findViewById<TextView>(R.id.celebrationDescription).text = event.description
        view.findViewById<TextView>(R.id.celebrationHabit).text =
            activity.getString(R.string.achievement_habit_label, event.habitName)
        view.findViewById<TextView>(R.id.celebrationStreak).text =
            activity.resources.getQuantityString(
                R.plurals.achievement_streak_days,
                event.streakLength,
                event.streakLength
            )

        val dialog = MaterialAlertDialogBuilder(activity)
            .setView(view)
            .setCancelable(false)
            .create()

        view.findViewById<MaterialButton>(R.id.celebrationShareButton).setOnClickListener {
            shareAchievement(event)
        }
        view.findViewById<MaterialButton>(R.id.celebrationDismissButton).setOnClickListener {
            dialog.dismiss()
        }

        dialog.setOnDismissListener {
            clearDialogState(advanceQueue = true)
        }
        dialog.show()

        fallbackNotifiedIds.remove(event.id)
        currentDialog = dialog
        isShowing = true
        fireConfetti(event.paletteColor)
    }

    private fun showFallback(event: AchievementUnlocked) {
        val message = activity.getString(R.string.achievement_unlocked_snackbar, event.title)
        if (rootView.isShown) {
            Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).show()
        } else {
            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun dismissDialogSafely(advanceQueue: Boolean) {
        val dialog = currentDialog ?: return
        dialog.setOnDismissListener(null)
        dialog.dismiss()
        clearDialogState(advanceQueue)
    }

    private fun clearDialogState(advanceQueue: Boolean) {
        if (!isShowing) {
            currentDialog = null
            return
        }
        isShowing = false
        currentDialog = null
        if (advanceQueue && queue.isNotEmpty()) {
            queue.removeFirst()
            maybeShowNext()
        }
    }

    private fun shareAchievement(event: AchievementUnlocked) {
        val shareText = activity.getString(
            R.string.achievement_share_message,
            event.habitName,
            event.title,
            event.streakLength
        )
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        try {
            val chooserTitle = activity.getString(R.string.achievement_share_title)
            activity.startActivity(Intent.createChooser(intent, chooserTitle))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(activity, R.string.achievement_share_error, Toast.LENGTH_SHORT).show()
        }
    }

    private fun fireConfetti(color: PaletteColor) {
        if (preferences.isConfettiAnimationDisabled) return
        val animatorScale = Settings.Global.getFloat(
            activity.contentResolver,
            Settings.Global.ANIMATOR_DURATION_SCALE,
            1f
        )
        if (animatorScale == 0f) return
        val theme = themeSwitcher.currentTheme ?: return
        val baseColor = theme.color(color).toInt()
        rootView.markConfettiTriggered()
        rootView.konfettiView.start(
            Party(
                speed = 0f,
                maxSpeed = 14f,
                damping = 0.9f,
                spread = 360,
                position = Position.Relative(0.5, 0.1),
                colors = listOf(
                    ColorUtils.changeHue(baseColor, 16f),
                    ColorUtils.changeHue(baseColor, -16f),
                    ColorUtils.changeHue(baseColor, 48f),
                    baseColor
                ),
                emitter = Emitter(duration = 200, TimeUnit.MILLISECONDS).max(50),
                timeToLive = 3000
            )
        )
    }

    private fun resolveIcon(iconKey: String): Int {
        return when (iconKey.lowercase(Locale.ROOT)) {
            "streak" -> R.drawable.ic_achievement_trophy
            "perfect" -> R.drawable.ic_achievement_trophy
            else -> R.drawable.ic_achievement_trophy
        }
    }
}
