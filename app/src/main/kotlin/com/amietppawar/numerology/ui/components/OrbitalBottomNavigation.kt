package com.amietppawar.numerology.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

private class OrbitalItem(val route: String, val label: String)

private val orbitalItems = listOf(
    OrbitalItem("home", "Home"),
    OrbitalItem("namecheck", "Name"),
    OrbitalItem("numbers", "Numbers"),
    OrbitalItem("learn", "Learn"),
    OrbitalItem("consult", "Consult")
)

/** Moves to a main section without stacking up copies of the same screen. */
fun NavHostController.goTo(route: String) {
    navigate(route) {
        popUpTo("home") {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

/**
 * Bottom navigation shaped like a shallow orbit: the five destinations sit
 * along an arc, with the centre one raised.
 */
@Composable
fun OrbitalBottomNavigation(navController: NavHostController) {
    val scheme = MaterialTheme.colorScheme
    val entry by navController.currentBackStackEntryAsState()
    val currentRoute = entry?.destination?.route
    val arcColor = scheme.primary.copy(alpha = 0.45f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(92.dp)
            .background(scheme.background)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawArc(
                color = arcColor,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(-size.width * 0.15f, 10.dp.toPx()),
                size = Size(size.width * 1.3f, size.height * 1.7f),
                style = Stroke(width = 1.dp.toPx())
            )
        }
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            orbitalItems.forEachIndexed { index, item ->
                val lift: Dp = when (index) {
                    2 -> 16.dp
                    1, 3 -> 9.dp
                    else -> 0.dp
                }
                OrbitalOrb(
                    item = item,
                    index = index,
                    selected = currentRoute == item.route,
                    lift = lift,
                    onClick = { navController.goTo(item.route) }
                )
            }
        }
    }
}

@Composable
private fun OrbitalOrb(
    item: OrbitalItem,
    index: Int,
    selected: Boolean,
    lift: Dp,
    onClick: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    val color = if (selected) scheme.secondary else scheme.onSurfaceVariant
    val glow = scheme.primary.copy(alpha = 0.35f)
    val description = if (selected) item.label + ", selected" else item.label

    Column(
        modifier = Modifier
            .padding(bottom = 4.dp + lift)
            .widthIn(min = 60.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable(role = Role.Tab, onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 4.dp)
            .semantics { contentDescription = description },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Canvas(modifier = Modifier.size(38.dp)) {
            if (selected) {
                drawCircle(color = glow, radius = size.minDimension / 2f)
            }
            drawOrbGlyph(index, color)
        }
        Text(
            text = item.label,
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}

/** Hand-drawn glyphs, so the navigation uses no stock icons. */
private fun DrawScope.drawOrbGlyph(index: Int, color: Color) {
    val w = size.minDimension
    val c = Offset(size.width / 2f, size.height / 2f)
    val stroke = Stroke(width = 1.6.dp.toPx())
    when (index) {
        0 -> {
            drawCircle(color = color, radius = w * 0.30f, center = c, style = stroke)
            drawCircle(color = color, radius = w * 0.09f, center = c)
        }
        1 -> drawLabel(text = "Aa", x = c.x, y = c.y, color = color, sizePx = 15.sp.toPx(), bold = true)
        2 -> {
            drawCircle(color = color, radius = w * 0.32f, center = c, style = stroke)
            drawLabel(text = "8", x = c.x, y = c.y, color = color, sizePx = 14.sp.toPx(), bold = true)
        }
        3 -> {
            val widths = listOf(0.52f, 0.36f, 0.46f)
            widths.forEachIndexed { line, fraction ->
                val y = c.y + (line - 1) * w * 0.17f
                drawLine(
                    color = color,
                    start = Offset(c.x - w * 0.26f, y),
                    end = Offset(c.x - w * 0.26f + w * fraction, y),
                    strokeWidth = 1.8.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }
        else -> {
            drawCircle(color = color, radius = w * 0.20f, center = Offset(c.x - w * 0.11f, c.y), style = stroke)
            drawCircle(color = color, radius = w * 0.20f, center = Offset(c.x + w * 0.11f, c.y), style = stroke)
        }
    }
}
