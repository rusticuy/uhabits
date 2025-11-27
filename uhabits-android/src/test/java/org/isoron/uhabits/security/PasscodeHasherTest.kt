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

import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class PasscodeHasherTest {

    @Test
    fun testGenerateSaltProducesDifferentValues() {
        val salt1 = PasscodeHasher.generateSalt()
        val salt2 = PasscodeHasher.generateSalt()
        assertNotEquals(salt1, salt2)
    }

    @Test
    fun testSaltIsNotEmpty() {
        val salt = PasscodeHasher.generateSalt()
        assertTrue(salt.isNotEmpty())
    }

    @Test
    fun testHashPasscodeProducesConsistentOutput() {
        val passcode = "1234"
        val salt = PasscodeHasher.generateSalt()
        val hash1 = PasscodeHasher.hashPasscode(passcode, salt)
        val hash2 = PasscodeHasher.hashPasscode(passcode, salt)
        assertTrue(hash1 == hash2)
    }

    @Test
    fun testDifferentPasscodesDifferentHashes() {
        val salt = PasscodeHasher.generateSalt()
        val hash1 = PasscodeHasher.hashPasscode("1234", salt)
        val hash2 = PasscodeHasher.hashPasscode("5678", salt)
        assertNotEquals(hash1, hash2)
    }

    @Test
    fun testVerifyPasscodeSucceedsWithCorrectPasscode() {
        val passcode = "1234"
        val salt = PasscodeHasher.generateSalt()
        val hash = PasscodeHasher.hashPasscode(passcode, salt)
        assertTrue(PasscodeHasher.verifyPasscode(passcode, salt, hash))
    }

    @Test
    fun testVerifyPasscodeFailsWithWrongPasscode() {
        val passcode = "1234"
        val wrongPasscode = "5678"
        val salt = PasscodeHasher.generateSalt()
        val hash = PasscodeHasher.hashPasscode(passcode, salt)
        assertFalse(PasscodeHasher.verifyPasscode(wrongPasscode, salt, hash))
    }

    @Test
    fun testVerifyPasscodeFailsWithWrongSalt() {
        val passcode = "1234"
        val salt1 = PasscodeHasher.generateSalt()
        val salt2 = PasscodeHasher.generateSalt()
        val hash = PasscodeHasher.hashPasscode(passcode, salt1)
        assertFalse(PasscodeHasher.verifyPasscode(passcode, salt2, hash))
    }

    @Test
    fun testVerifyPasscodeFailsWithModifiedHash() {
        val passcode = "1234"
        val salt = PasscodeHasher.generateSalt()
        val hash = PasscodeHasher.hashPasscode(passcode, salt)
        val modifiedHash = hash.substring(0, hash.length - 1) + "A"
        assertFalse(PasscodeHasher.verifyPasscode(passcode, salt, modifiedHash))
    }

    @Test
    fun testLongPasscode() {
        val passcode = "123456789012345"
        val salt = PasscodeHasher.generateSalt()
        val hash = PasscodeHasher.hashPasscode(passcode, salt)
        assertTrue(PasscodeHasher.verifyPasscode(passcode, salt, hash))
    }

    @Test
    fun testPasscodeWithSpecialCharacters() {
        val passcode = "pass@123#word"
        val salt = PasscodeHasher.generateSalt()
        val hash = PasscodeHasher.hashPasscode(passcode, salt)
        assertTrue(PasscodeHasher.verifyPasscode(passcode, salt, hash))
    }
}
