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

package org.isoron.uhabits.activities.settings

import androidx.preference.PreferenceManager
import androidx.test.filters.MediumTest
import org.isoron.uhabits.BaseAndroidTest
import org.isoron.uhabits.R
import org.isoron.uhabits.core.preferences.Preferences
import org.isoron.uhabits.security.AppLockConfig
import org.isoron.uhabits.security.LockType
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@MediumTest
class SettingsFragmentLockTest : BaseAndroidTest() {

    private lateinit var prefs: Preferences
    private lateinit var appLockConfig: AppLockConfig

    @Before
    override fun setUp() {
        super.setUp()
        prefs = component.preferences
        appLockConfig = component.appLockConfig
    }

    @Test
    fun testLockTypeDefaultIsNone() {
        assertEquals(LockType.NONE, appLockConfig.getLockType())
    }

    @Test
    fun testCanSetLockType() {
        appLockConfig.setLockType(LockType.PIN)
        assertEquals(LockType.PIN, appLockConfig.getLockType())
    }

    @Test
    fun testCanSetAndGetLockTimeoutMinutes() {
        appLockConfig.setLockTimeoutMinutes(15)
        assertEquals(15, appLockConfig.getLockTimeoutMinutes())
    }

    @Test
    fun testDefaultLockTimeoutMinutesIsFive() {
        assertEquals(5, appLockConfig.getLockTimeoutMinutes())
    }

    @Test
    fun testCanSetAndGetBiometricAllowed() {
        appLockConfig.setBiometricAllowed(true)
        assertTrue(appLockConfig.isBiometricAllowed())
        appLockConfig.setBiometricAllowed(false)
        assertFalse(appLockConfig.isBiometricAllowed())
    }

    @Test
    fun testCanSetAndGetPasscodeSalt() {
        val testSalt = "testSalt123"
        appLockConfig.setPasscodeSalt(testSalt)
        assertEquals(testSalt, appLockConfig.getPasscodeSalt())
    }

    @Test
    fun testCanSetAndGetPasscodeHash() {
        val testHash = "testHash456"
        appLockConfig.setPasscodeHash(testHash)
        assertEquals(testHash, appLockConfig.getPasscodeHash())
    }

    @Test
    fun testIsLockConfiguredFalseByDefault() {
        assertFalse(appLockConfig.isLockConfigured())
    }

    @Test
    fun testIsLockConfiguredTrueAfterSettingLockType() {
        appLockConfig.setLockType(LockType.PIN)
        assertTrue(appLockConfig.isLockConfigured())
    }

    @Test
    fun testClearLockConfiguration() {
        appLockConfig.setLockType(LockType.PIN)
        appLockConfig.setBiometricAllowed(true)
        appLockConfig.setPasscodeSalt("salt")
        appLockConfig.setPasscodeHash("hash")

        appLockConfig.clearLockConfiguration()

        assertEquals(LockType.NONE, appLockConfig.getLockType())
        assertFalse(appLockConfig.isBiometricAllowed())
        assertEquals("", appLockConfig.getPasscodeSalt())
        assertEquals("", appLockConfig.getPasscodeHash())
    }

    @Test
    fun testLockPreferencesDoNotLeak() {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(targetContext)
        appLockConfig.setPasscodeSalt("secretSalt123")
        appLockConfig.setPasscodeHash("secretHash456")

        val allPrefs = sharedPrefs.all
        val saltFromRegularPrefs = sharedPrefs.getString("pref_passcode_salt", null)
        val hashFromRegularPrefs = sharedPrefs.getString("pref_passcode_hash", null)

        assertTrue(allPrefs.isEmpty() || !allPrefs.containsKey("pref_passcode_salt"))
        assertTrue(allPrefs.isEmpty() || !allPrefs.containsKey("pref_passcode_hash"))
        assertTrue(saltFromRegularPrefs == null || saltFromRegularPrefs.isEmpty())
        assertTrue(hashFromRegularPrefs == null || hashFromRegularPrefs.isEmpty())
    }

    @Test
    fun testMultipleLockConfigurationChanges() {
        appLockConfig.setLockType(LockType.PIN)
        appLockConfig.setLockTimeoutMinutes(10)
        appLockConfig.setBiometricAllowed(true)

        assertEquals(LockType.PIN, appLockConfig.getLockType())
        assertEquals(10, appLockConfig.getLockTimeoutMinutes())
        assertTrue(appLockConfig.isBiometricAllowed())

        appLockConfig.setLockType(LockType.BIOMETRIC)
        appLockConfig.setLockTimeoutMinutes(30)

        assertEquals(LockType.BIOMETRIC, appLockConfig.getLockType())
        assertEquals(30, appLockConfig.getLockTimeoutMinutes())
        assertTrue(appLockConfig.isBiometricAllowed())
    }
}
