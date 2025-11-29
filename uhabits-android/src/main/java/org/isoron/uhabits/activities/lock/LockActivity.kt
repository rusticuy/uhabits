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

package org.isoron.uhabits.activities.lock

import android.os.Bundle
import android.widget.EditText
import android.widget.GridLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import com.google.android.material.button.MaterialButton
import org.isoron.uhabits.HabitsApplication
import org.isoron.uhabits.R
import org.isoron.uhabits.security.AppLockManager
import org.isoron.uhabits.utils.EdgeToEdge.enableEdgeToEdge
import java.util.concurrent.Executor

class LockActivity : AppCompatActivity() {

    private lateinit var appLockManager: AppLockManager
    private lateinit var pinInput: EditText
    private lateinit var unlockButton: MaterialButton
    private lateinit var biometricButton: MaterialButton
    private var biometricPrompt: BiometricPrompt? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lock)
        enableEdgeToEdge()

        val component = (applicationContext as HabitsApplication).component
        appLockManager = component.appLockManager

        pinInput = findViewById(R.id.pinInput)
        unlockButton = findViewById(R.id.unlockButton)
        biometricButton = findViewById(R.id.biometricButton)

        setupKeypad()
        setupButtons()
        setupBiometric()
    }

    private fun setupKeypad() {
        val keypad = findViewById<GridLayout>(R.id.keypad)
        for (i in 0 until keypad.childCount) {
            val button = keypad.getChildAt(i)
            if (button is MaterialButton) {
                when (button.id) {
                    R.id.btnBackspace -> {
                        button.setOnClickListener {
                            val text = pinInput.text
                            if (text.isNotEmpty()) {
                                pinInput.text.delete(text.length - 1, text.length)
                            }
                        }
                    }
                    else -> {
                        button.setOnClickListener {
                            val currentText = pinInput.text.toString()
                            if (currentText.length < 6) {
                                pinInput.append(button.text)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupButtons() {
        unlockButton.setOnClickListener {
            val pin = pinInput.text.toString()
            if (pin.isEmpty()) {
                Toast.makeText(this, "Please enter a PIN", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (appLockManager.verifyPin(pin)) {
                appLockManager.unlock()
                finish()
            } else {
                Toast.makeText(this, "Incorrect PIN", Toast.LENGTH_SHORT).show()
                pinInput.text.clear()
            }
        }
    }

    private fun setupBiometric() {
        val biometricManager = BiometricManager.from(this)
        val canAuthenticate = biometricManager.canAuthenticate(BIOMETRIC_STRONG)

        if (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS) {
            biometricButton.setOnClickListener {
                showBiometricPrompt()
            }
        } else {
            biometricButton.isEnabled = false
        }
    }

    private fun showBiometricPrompt() {
        val executor = Executor { command -> command.run() }

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                appLockManager.unlock()
                finish()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Toast.makeText(this@LockActivity, "Biometric error: $errString", Toast.LENGTH_SHORT).show()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Toast.makeText(this@LockActivity, "Biometric authentication failed", Toast.LENGTH_SHORT).show()
            }
        }

        biometricPrompt = BiometricPrompt(this, executor, callback)

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Unlock App")
            .setSubtitle("Use your biometric to unlock")
            .setAllowedAuthenticators(BIOMETRIC_STRONG)
            .setNegativeButtonText("Cancel")
            .build()

        biometricPrompt?.authenticate(promptInfo)
    }

    override fun onBackPressed() {
        // Prevent back button from exiting the lock screen
    }
}
