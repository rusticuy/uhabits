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
package org.isoron.uhabits.core.achievements

import org.isoron.uhabits.core.models.Timestamp
import org.isoron.uhabits.core.preferences.Preferences
import java.nio.charset.StandardCharsets
import java.util.Base64

class PreferencesAchievementRepository(
    private val storage: Preferences.Storage
) : AchievementRepository {

    private val encoder = Base64.getUrlEncoder().withoutPadding()
    private val decoder = Base64.getUrlDecoder()
    private val cache = mutableListOf<AchievementUnlockRecord>()
    private var loaded = false

    @Synchronized
    override fun save(unlock: AchievementUnlockRecord) {
        load()
        cache.add(unlock)
        persist()
    }

    @Synchronized
    override fun getUnlocks(): List<AchievementUnlockRecord> {
        load()
        return cache.toList()
    }

    @Synchronized
    override fun getUnlocksForHabit(habitId: Long?, habitUuid: String?): List<AchievementUnlockRecord> {
        load()
        return cache.filter { it.belongsTo(habitId, habitUuid) }
    }

    @Synchronized
    override fun isUnlocked(definitionId: String, habitId: Long?, habitUuid: String?): Boolean {
        load()
        return cache.any { it.definitionId == definitionId && it.belongsTo(habitId, habitUuid) }
    }

    @Synchronized
    override fun clear() {
        cache.clear()
        persist()
    }

    private fun AchievementUnlockRecord.belongsTo(habitId: Long?, habitUuid: String?): Boolean {
        if (this.habitId != null && habitId != null && this.habitId == habitId) return true
        if (this.habitUuid != null && habitUuid != null && this.habitUuid == habitUuid) return true
        return false
    }

    private fun load() {
        if (loaded) return
        val serialized = storage.getString(KEY_UNLOCKS, "")
        if (serialized.isNotBlank()) {
            serialized.split('\n').forEach { line ->
                if (line.isBlank()) return@forEach
                decode(line)?.let { cache.add(it) }
            }
        }
        loaded = true
    }

    private fun persist() {
        if (cache.isEmpty()) {
            storage.remove(KEY_UNLOCKS)
            return
        }
        val serialized = cache.joinToString(separator = "\n") { encode(it) }
        storage.putString(KEY_UNLOCKS, serialized)
    }

    private fun encode(record: AchievementUnlockRecord): String {
        return listOf(
            encodeString(record.definitionId),
            encodeLong(record.habitId),
            encodeString(record.habitUuid),
            encodeString(record.habitName),
            record.streakLength.toString(),
            record.unlockedAt.unixTime.toString()
        ).joinToString(separator = "|")
    }

    private fun decode(serialized: String): AchievementUnlockRecord? {
        val parts = serialized.split("|")
        if (parts.size != 6) return null
        val definitionId = decodeString(parts[0]) ?: return null
        val habitId = decodeLong(parts[1])
        val habitUuid = decodeString(parts[2])
        val habitName = decodeString(parts[3]) ?: ""
        val streakLength = parts[4].toIntOrNull() ?: return null
        val unlockedAt = Timestamp(parts[5].toLongOrNull() ?: return null)
        return AchievementUnlockRecord(
            definitionId = definitionId,
            habitId = habitId,
            habitUuid = habitUuid,
            habitName = habitName,
            streakLength = streakLength,
            unlockedAt = unlockedAt
        )
    }

    private fun encodeString(value: String?): String {
        return value?.let {
            encoder.encodeToString(it.toByteArray(StandardCharsets.UTF_8))
        } ?: PLACEHOLDER
    }

    private fun decodeString(value: String): String? {
        if (value == PLACEHOLDER) return null
        return try {
            String(decoder.decode(value), StandardCharsets.UTF_8)
        } catch (ignored: IllegalArgumentException) {
            null
        }
    }

    private fun encodeLong(value: Long?): String {
        return value?.toString() ?: PLACEHOLDER
    }

    private fun decodeLong(value: String): Long? {
        if (value == PLACEHOLDER) return null
        return value.toLongOrNull()
    }

    companion object {
        private const val KEY_UNLOCKS = "achievement_unlocks"
        private const val PLACEHOLDER = "-"
    }
}
