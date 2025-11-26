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

package org.isoron.uhabits.security

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ProcessLifecycleOwner
import org.isoron.uhabits.core.AppScope
import org.isoron.uhabits.core.preferences.Preferences
import org.isoron.uhabits.core.preferences.Preferences.Companion.LOCK_TYPE_NONE
import javax.inject.Inject

@AppScope
class AppLockManager @Inject constructor(
    private val preferences: Preferences
) : LifecycleEventObserver {

    private var lastUnlockTime: Long = System.currentTimeMillis()
    private var isAppLocked: Boolean = false

    fun initialize() {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun onStateChanged(source: androidx.lifecycle.LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_STOP -> {
                isAppLocked = false
            }
            Lifecycle.Event.ON_START -> {
                if (preferences.lockType != LOCK_TYPE_NONE) {
                    val timeoutSeconds = preferences.lockTimeout
                    val currentTime = System.currentTimeMillis()
                    val elapsedSeconds = (currentTime - lastUnlockTime) / 1000

                    if (elapsedSeconds > timeoutSeconds) {
                        isAppLocked = true
                    }
                }
            }
            else -> {}
        }
    }

    fun requireUnlock(activity: Activity) {
        if (preferences.lockType == LOCK_TYPE_NONE) {
            return
        }

        if (isAppLocked) {
            val intent = Intent(activity, LockActivity::class.java)
            activity.startActivity(intent)
            activity.finish()
        }
    }

    fun unlock() {
        lastUnlockTime = System.currentTimeMillis()
        isAppLocked = false
    }

    fun isLocked(): Boolean = isAppLocked

    fun setLockPin(pin: String) {
        preferences.lockPin = PasswordUtils.hash(pin)
        preferences.lockType = Preferences.LOCK_TYPE_PIN
    }

    fun verifyPin(pin: String): Boolean {
        return PasswordUtils.verify(pin, preferences.lockPin)
    }

    fun setLockPattern(pattern: String) {
        preferences.lockPattern = PasswordUtils.hash(pattern)
        preferences.lockType = Preferences.LOCK_TYPE_PATTERN
    }

    fun verifyPattern(pattern: String): Boolean {
        return PasswordUtils.verify(pattern, preferences.lockPattern)
    }

    fun disableLock() {
        preferences.lockType = LOCK_TYPE_NONE
        preferences.lockPin = ""
        preferences.lockPattern = ""
        isAppLocked = false
    }
}
