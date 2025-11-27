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

import org.isoron.uhabits.core.AppScope
import org.isoron.uhabits.core.preferences.Preferences
import javax.inject.Inject

@AppScope
class AppLockConfig @Inject constructor(
    private val preferences: Preferences
) {
    fun isLockConfigured(): Boolean {
        return getLockType() != LockType.NONE
    }

    fun getLockType(): LockType {
        return try {
            LockType.valueOf(preferences.lockType)
        } catch (e: IllegalArgumentException) {
            LockType.NONE
        }
    }

    fun setLockType(lockType: LockType) {
        preferences.lockType = lockType.name
    }

    fun getLockTimeoutMinutes(): Int {
        return preferences.lockTimeoutMinutes
    }

    fun setLockTimeoutMinutes(minutes: Int) {
        preferences.lockTimeoutMinutes = minutes
    }

    fun isBiometricAllowed(): Boolean {
        return preferences.biometricAllowed
    }

    fun setBiometricAllowed(allowed: Boolean) {
        preferences.biometricAllowed = allowed
    }

    fun getPasscodeSalt(): String {
        return preferences.passcodeSalt
    }

    fun setPasscodeSalt(salt: String) {
        preferences.passcodeSalt = salt
    }

    fun getPasscodeHash(): String {
        return preferences.passcodeHash
    }

    fun setPasscodeHash(hash: String) {
        preferences.passcodeHash = hash
    }

    fun clearLockConfiguration() {
        preferences.lockType = LockType.NONE.name
        preferences.biometricAllowed = false
        preferences.passcodeSalt = ""
        preferences.passcodeHash = ""
    }
}

enum class LockType {
    NONE,
    PIN,
    PATTERN,
    BIOMETRIC
}
