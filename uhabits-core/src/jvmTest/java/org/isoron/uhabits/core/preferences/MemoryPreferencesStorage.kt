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
package org.isoron.uhabits.core.preferences

class MemoryPreferencesStorage : Preferences.Storage {
    private val storage = mutableMapOf<String, Any>()
    
    override fun clear() {
        storage.clear()
    }
    
    override fun getBoolean(key: String, defValue: Boolean): Boolean {
        return storage[key] as? Boolean ?: defValue
    }
    
    override fun getInt(key: String, defValue: Int): Int {
        return storage[key] as? Int ?: defValue
    }
    
    override fun getLong(key: String, defValue: Long): Long {
        return storage[key] as? Long ?: defValue
    }
    
    override fun getString(key: String, defValue: String): String {
        return storage[key] as? String ?: defValue
    }
    
    override fun onAttached(preferences: Preferences) {
    }
    
    override fun putBoolean(key: String, value: Boolean) {
        storage[key] = value
    }
    
    override fun putInt(key: String, value: Int) {
        storage[key] = value
    }
    
    override fun putLong(key: String, value: Long) {
        storage[key] = value
    }
    
    override fun putString(key: String, value: String) {
        storage[key] = value
    }
    
    override fun remove(key: String) {
        storage.remove(key)
    }
}
