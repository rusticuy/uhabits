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

import android.content.ContentResolver
import android.provider.Settings
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.xml.KonfettiView
import org.isoron.platform.gui.toInt
import org.isoron.uhabits.core.preferences.Preferences
import org.isoron.uhabits.core.ui.ThemeSwitcher
import org.isoron.uhabits.utils.ColorUtils
import java.util.concurrent.TimeUnit

class CelebrationConfettiController(
    private val konfettiView: KonfettiView,
    private val preferences: Preferences,
    private val themeSwitcher: ThemeSwitcher,
    private val contentResolver: ContentResolver
) {
    private var hasPlayedConfetti = false
    private var isStarted = false

    fun onStart(state: CelebrationState) {
        isStarted = true
        if (state.playConfetti && !hasPlayedConfetti && shouldPlayConfetti()) {
            playConfetti(state)
            hasPlayedConfetti = true
        }
    }

    fun onStop() {
        isStarted = false
        konfettiView.stop()
    }

    fun onDestroyView() {
        konfettiView.stop()
        isStarted = false
        hasPlayedConfetti = false
    }

    private fun shouldPlayConfetti(): Boolean {
        if (preferences.isConfettiAnimationDisabled) return false

        val animatorScale = Settings.Global.getFloat(
            contentResolver,
            Settings.Global.ANIMATOR_DURATION_SCALE,
            1f
        )
        return animatorScale != 0f
    }

    private fun playConfetti(state: CelebrationState) {
        val theme = themeSwitcher.currentTheme ?: return
        val baseColor = theme.color(state.palette).toInt()

        val colors = listOf(
            ColorUtils.changeHue(baseColor, 180f),
            ColorUtils.changeHue(baseColor, 20f),
            ColorUtils.changeHue(baseColor, -20f),
            baseColor
        )

        val width = konfettiView.width.toFloat()
        val height = konfettiView.height.toFloat()

        konfettiView.start(
            Party(
                speed = 0f,
                maxSpeed = 30f,
                damping = 0.9f,
                spread = 45,
                angle = 270,
                colors = colors,
                position = Position.Relative(0.0, 0.0).between(Position.Relative(1.0, 0.0)),
                emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100),
                timeToLive = 3000L
            ),
            Party(
                speed = 0f,
                maxSpeed = 35f,
                damping = 0.9f,
                spread = 10,
                angle = 0,
                colors = colors,
                position = Position.Relative(0.5, 0.5),
                emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100),
                timeToLive = 2000L
            )
        )
    }
}
