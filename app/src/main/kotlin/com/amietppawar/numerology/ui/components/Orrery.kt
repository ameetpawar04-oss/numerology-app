package com.amietppawar.numerology.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amietppawar.numerology.ui.theme.LocalReduceMotion
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * The Orrery: eight rings, one for each digit 1 to 8, turning around a bright
 * core, with a field of drifting points. Swipe sideways to turn it by hand.
 */
@Composable
fun Orrery(modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    val background = scheme.background
    val ringColors = listOf(
        scheme.primary, scheme.tertiary, scheme.secondary, scheme.primary,
        scheme.tertiary, scheme.secondary, scheme.primary, scheme.tertiary
    )
    val coreColor = scheme.secondary
    val dustColor = scheme.onBackground

    val reduceMotion = LocalReduceMotion.current
    val time = rememberCosmosTime(running = !reduceMotion)
    var turned by remember { mutableStateOf(0f) }

    Canvas(
        modifier = modifier
            .pointerInput(Unit) {
                detectHorizontalDragGestures { _, dragAmount ->
                    turned += dragAmount * 0.35f
                }
            }
            .semantics {
                contentDescription = "Animated orrery of eight numbered rings. Swipe sideways to turn it."
            }
    ) {
        val seconds = time.value
        val center = Offset(size.width / 2f, size.height / 2f)
        val maxRadius = min(size.width, size.height) / 2f * 0.92f
        val coreRadius = maxRadius * 0.11f

        // Soft glow behind everything
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(ringColors[0].copy(alpha = 0.30f), Color.Transparent),
                center = center,
                radius = maxRadius * 1.1f
            ),
            radius = maxRadius * 1.1f,
            center = center
        )

        // Drifting points
        for (k in 0 until 44) {
            val spread = ((k * 0.618f) % 1f)
            val radius = maxRadius * (0.18f + 0.86f * spread)
            val degrees = k * 137.5f + seconds * (3f + (k % 5)) + turned * 0.5f
            val angle = Math.toRadians(degrees.toDouble())
            val twinkle = 0.25f + 0.25f * sin(seconds * 1.3f + k)
            drawCircle(
                color = dustColor.copy(alpha = twinkle.coerceIn(0.05f, 0.6f)),
                radius = (1f + (k % 3) * 0.5f).dp.toPx(),
                center = Offset(
                    center.x + radius * cos(angle).toFloat(),
                    center.y + radius * sin(angle).toFloat()
                )
            )
        }

        // Rings and their orbiting digits
        var previous: Offset? = null
        for (i in 0 until 8) {
            val color = ringColors[i]
            val radius = coreRadius + (maxRadius - coreRadius) * (i + 1) / 8f
            drawCircle(
                color = color.copy(alpha = 0.28f),
                radius = radius,
                center = center,
                style = Stroke(width = 1.dp.toPx())
            )

            val direction = if (i % 2 == 0) 1f else -1f
            val speed = 24f - i * 2.2f
            val degrees = i * 47f + direction * speed * seconds + turned
            val angle = Math.toRadians(degrees.toDouble())
            val position = Offset(
                center.x + radius * cos(angle).toFloat(),
                center.y + radius * sin(angle).toFloat()
            )

            val last = previous
            if (last != null) {
                drawLine(
                    color = color.copy(alpha = 0.16f),
                    start = last,
                    end = position,
                    strokeWidth = 1.dp.toPx()
                )
            }
            previous = position

            drawCircle(color = color.copy(alpha = 0.22f), radius = 15.dp.toPx(), center = position)
            drawCircle(color = background, radius = 10.dp.toPx(), center = position)
            drawCircle(
                color = color,
                radius = 10.dp.toPx(),
                center = position,
                style = Stroke(width = 1.5.dp.toPx())
            )
            drawLabel(
                text = (i + 1).toString(),
                x = position.x,
                y = position.y,
                color = color,
                sizePx = 12.sp.toPx(),
                bold = true
            )
        }

        // Luminous core
        val pulse = 1f + 0.08f * sin(seconds * 1.6f)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(coreColor.copy(alpha = 0.9f), Color.Transparent),
                center = center,
                radius = coreRadius * 2.6f * pulse
            ),
            radius = coreRadius * 2.6f * pulse,
            center = center
        )
        drawCircle(color = coreColor, radius = coreRadius * 0.7f, center = center)
        drawCircle(color = Color.White.copy(alpha = 0.75f), radius = coreRadius * 0.32f, center = center)
    }
}
