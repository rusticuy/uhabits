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
package org.isoron.uhabits.core.tasks

import org.isoron.uhabits.core.insights.Insight
import org.isoron.uhabits.core.insights.InsightsInteractor

class LoadInsightsTask(
    private val insightsInteractor: InsightsInteractor,
    private val onComplete: (List<Insight>) -> Unit
) : Task {

    private var insights: List<Insight> = emptyList()

    override fun doInBackground() {
        insights = insightsInteractor.generateInsights()
        val weeklySummary = insightsInteractor.getWeeklySummary()
        if (weeklySummary != null) {
            insights = insights + weeklySummary
        }
    }

    override fun onPostExecute() {
        onComplete(insights)
    }
}
