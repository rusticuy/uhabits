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

package org.isoron.uhabits.activities.common.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import org.isoron.uhabits.core.ui.screens.habits.show.views.TrendDataPoint
import kotlin.math.max
import kotlin.math.min

class TrendChartView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    private var trendData: List<TrendDataPoint> = emptyList()
    private var color: Int = 0xFF4CAF50.toInt()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val pointPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        paint.style = Paint.Style.FILL
        paint.color = color

        linePaint.style = Paint.Style.STROKE
        linePaint.color = color
        linePaint.strokeWidth = 4f

        pointPaint.style = Paint.Style.FILL
        pointPaint.color = color
    }

    fun setTrendData(data: List<TrendDataPoint>) {
        this.trendData = data
        invalidate()
    }

    fun setColor(color: Int) {
        this.color = color
        paint.color = color
        linePaint.color = color
        pointPaint.color = color
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (trendData.isEmpty()) {
            return
        }

        val width = width.toFloat()
        val height = height.toFloat()
        val padding = 32f

        if (width <= 2 * padding || height <= 2 * padding) {
            return
        }

        val graphWidth = width - 2 * padding
        val graphHeight = height - 2 * padding

        val maxValue = trendData.maxOfOrNull { it.value } ?: 1.0
        val minValue = trendData.minOfOrNull { it.value } ?: 0.0
        val valueRange = maxValue - minValue
        val adjustedRange = if (valueRange == 0.0) 1.0 else valueRange * 1.1

        val points = mutableListOf<Pair<Float, Float>>()
        for ((index, point) in trendData.withIndex()) {
            val x = padding + (index.toFloat() / (trendData.size - 1)) * graphWidth
            val normalizedValue = (point.value - minValue + adjustedRange * 0.05) / (adjustedRange * 1.1)
            val y = padding + (1 - normalizedValue) * graphHeight
            points.add(Pair(x, y))
        }

        drawPath(canvas, points)
        drawPoints(canvas, points)
    }

    private fun drawPath(canvas: Canvas, points: List<Pair<Float, Float>>) {
        if (points.size < 2) return

        val path = Path()
        path.moveTo(points[0].first, points[0].second)

        for (i in 1 until points.size) {
            val cp1x = (points[i - 1].first + points[i].first) / 2
            val cp1y = points[i - 1].second
            val cp2x = cp1x
            val cp2y = points[i].second

            path.cubicTo(cp1x, cp1y, cp2x, cp2y, points[i].first, points[i].second)
        }

        canvas.drawPath(path, linePaint)
    }

    private fun drawPoints(canvas: Canvas, points: List<Pair<Float, Float>>) {
        for ((x, y) in points) {
            canvas.drawCircle(x, y, 6f, pointPaint)
        }
    }
}
