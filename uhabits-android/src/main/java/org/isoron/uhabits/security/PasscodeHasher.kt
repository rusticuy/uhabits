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

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

class PasscodeHasher {
    companion object {
        private const val PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA256"
        private const val SALT_LENGTH = 16
        private const val HASH_LENGTH = 32
        private const val ITERATIONS = 10000

        fun generateSalt(): String {
            val random = SecureRandom()
            val salt = ByteArray(SALT_LENGTH)
            random.nextBytes(salt)
            return Base64.getEncoder().encodeToString(salt)
        }

        fun hashPasscode(passcode: String, salt: String): String {
            val saltBytes = Base64.getDecoder().decode(salt)
            val spec = PBEKeySpec(
                passcode.toCharArray(),
                saltBytes,
                ITERATIONS,
                HASH_LENGTH * 8
            )
            val factory = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM)
            val hash = factory.generateSecret(spec).encoded
            return Base64.getEncoder().encodeToString(hash)
        }

        fun verifyPasscode(passcode: String, salt: String, hash: String): Boolean {
            val computedHash = hashPasscode(passcode, salt)
            return constantTimeEquals(computedHash, hash)
        }

        private fun constantTimeEquals(a: String, b: String): Boolean {
            val aBytes = a.toByteArray()
            val bBytes = b.toByteArray()

            if (aBytes.size != bBytes.size) {
                return false
            }

            var result = 0
            for (i in aBytes.indices) {
                result = result or (aBytes[i].toInt() xor bBytes[i].toInt())
            }
            return result == 0
        }
    }
}
