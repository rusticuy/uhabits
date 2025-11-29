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

package org.isoron.uhabits.activities.goals.edit

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import org.isoron.uhabits.R
import org.isoron.uhabits.core.models.Goal

class EditGoalView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
    val currentGoal: Goal? = null
) : FrameLayout(context, attrs, defStyle) {

    lateinit var goalNameInput: TextInputEditText
    lateinit var goalDescriptionInput: TextInputEditText
    lateinit var saveButton: MaterialButton

    init {
        inflate(context, R.layout.edit_goal, this)
        setupViews()
    }

    private fun setupViews() {
        goalNameInput = findViewById(R.id.goalNameInput)
        goalDescriptionInput = findViewById(R.id.goalDescriptionInput)
        saveButton = findViewById(R.id.saveButton)

        currentGoal?.let {
            goalNameInput.setText(it.name)
            goalDescriptionInput.setText(it.description)
        }
    }

    fun setOnSaveClickListener(listener: OnClickListener) {
        saveButton.setOnClickListener(listener)
    }

    fun getGoalName(): String = goalNameInput.text.toString()

    fun getGoalDescription(): String = goalDescriptionInput.text.toString()
}
