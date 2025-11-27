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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import org.isoron.uhabits.R
import org.isoron.uhabits.databinding.FragmentLockSetupBinding
import org.isoron.uhabits.security.AppLockConfig
import org.isoron.uhabits.security.LockType
import org.isoron.uhabits.security.PasscodeHasher
import org.isoron.uhabits.HabitsApplication

class LockSetupFragment : Fragment() {
    private var _binding: FragmentLockSetupBinding? = null
    private val binding get() = _binding!!
    private lateinit var appLockConfig: AppLockConfig

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLockSetupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val appContext = requireContext().applicationContext
        if (appContext is HabitsApplication) {
            appLockConfig = appContext.component.appLockConfig
        }

        setupPinMode()
    }

    private fun setupPinMode() {
        binding.apply {
            pinInputFirst.hint = getString(R.string.pin_input_hint)
            pinInputConfirm.hint = getString(R.string.pin_confirm_hint)

            savePinButton.setOnClickListener {
                val pin = pinInputFirst.text.toString().trim()
                val confirmPin = pinInputConfirm.text.toString().trim()

                if (validatePin(pin, confirmPin)) {
                    savePinLock(pin)
                }
            }

            clearButton.setOnClickListener {
                pinInputFirst.text.clear()
                pinInputConfirm.text.clear()
            }
        }
    }

    private fun validatePin(pin: String, confirmPin: String): Boolean {
        return when {
            pin.isEmpty() -> {
                Toast.makeText(
                    requireContext(),
                    R.string.pin_cannot_be_empty,
                    Toast.LENGTH_SHORT
                ).show()
                false
            }
            pin.length < 4 -> {
                Toast.makeText(
                    requireContext(),
                    R.string.pin_too_short,
                    Toast.LENGTH_SHORT
                ).show()
                false
            }
            pin != confirmPin -> {
                Toast.makeText(
                    requireContext(),
                    R.string.pin_mismatch,
                    Toast.LENGTH_SHORT
                ).show()
                false
            }
            else -> true
        }
    }

    private fun savePinLock(pin: String) {
        val salt = PasscodeHasher.generateSalt()
        val hash = PasscodeHasher.hashPasscode(pin, salt)

        appLockConfig.apply {
            setLockType(LockType.PIN)
            setPasscodeSalt(salt)
            setPasscodeHash(hash)
        }

        Toast.makeText(
            requireContext(),
            R.string.lock_setup_complete,
            Toast.LENGTH_SHORT
        ).show()

        requireActivity().onBackPressed()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
