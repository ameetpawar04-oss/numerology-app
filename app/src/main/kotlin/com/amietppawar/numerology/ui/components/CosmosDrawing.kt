package com.amietppawar.numerology.ui.components

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import kotlin.math.cos
import kotlin.math.sin

private val labelPaint = Paint()

/**
 * Seconds elapsed while [running] is true. Reading the value inside a Canvas
 * only redraws the Canvas, so the rest of the screen stays still.
 */
@Composable
fun rememberCosmosTime(running: Boolean): State<Float> {
    val time = remember { mutableStateOf(0f) }
    LaunchedEffect(running) {
        if (running) {
            val start = withFrameNanos { it }
            while (true) {
                withFrameNanos { now ->
                    time.value = (now - start) / 1000000000f
                }
            }
        }
    }
    return time
}

/** Draws text centred on the given point. */
fun DrawScope.drawLabel(
    text: String,
    x: Float,
    y: Float,
    color: Color,
    sizePx: Float,
    bold: Boolean = false
) {
    drawIntoCanvas { canvas ->
        labelPaint.isAntiAlias = true
        labelPaint.color = color.toArgb()
        labelPaint.textSize = sizePx
        labelPaint.textAlign = Paint.Align.CENTER
        labelPaint.typeface = if (bold) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
        val baseline = y - (labelPaint.descent() + labelPaint.ascent()) / 2f
        canvas.nativeCanvas.drawText(text, x, baseline, labelPaint)
    }
}

/** Position of digit 1..8 on a ring: 1 sits at the top, then clockwise. */
fun digitNode(center: Offset, radius: Float, digit: Int): Offset {
    val angle = Math.toRadians((-90.0 + (digit - 1) * 45.0))
    return Offset(
        center.x + radius * cos(angle).toFloat(),
        center.y + radius * sin(angle).toFloat()
    )
}

/**
 * The points of a name's sigil. Each letter moves to its digit's direction,
 * spiralling inwards, so every name traces its own shape.
 */
fun sigilPoints(values: List<Int>, center: Offset, radius: Float): List<Offset> {
    val count = values.size
    if (count == 0) return emptyList()
    return values.mapIndexed { index, value ->
        val shrink = 1f - 0.6f * index.toFloat() / count.toFloat()
        digitNode(center, radius * shrink, value)
    }
}

/**
 * Draws the sigil. [progress] 0..1 draws the line gradually.
 */
fun DrawScope.drawSigil(
    values: List<Int>,
    center: Offset,
    radius: Float,
    lineColor: Color,
    accentColor: Color,
    progress: Float,
    strokePx: Float
) {
    val points = sigilPoints(values, center, radius)
    if (points.isEmpty()) return

    drawCircle(
        color = accentColor.copy(alpha = 0.55f),
        radius = radius * 1.12f,
        center = center,
        style = Stroke(width = strokePx * 0.6f)
    )

    val segments = points.size - 1
    val drawn = progress.coerceIn(0f, 1f) * segments
    for (i in 0 until segments) {
        val amount = (drawn - i).coerceIn(0f, 1f)
        if (amount > 0f) {
            val from = points[i]
            val to = points[i + 1]
            val end = Offset(
                from.x + (to.x - from.x) * amount,
                from.y + (to.y - from.y) * amount
            )
            drawLine(
                color = lineColor,
                start = from,
                end = end,
                strokeWidth = strokePx,
                cap = StrokeCap.Round
            )
        }
    }

    drawCircle(color = accentColor, radius = strokePx * 2.2f, center = points.first())
    if (progress >= 1f && points.size > 1) {
        drawCircle(
            color = lineColor,
            radius = strokePx * 2.2f,
            center = points.last(),
            style = Stroke(width = strokePx)
        )
    }
}
