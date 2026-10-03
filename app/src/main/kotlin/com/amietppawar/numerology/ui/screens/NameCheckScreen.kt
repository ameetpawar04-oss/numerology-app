package com.amietppawar.numerology.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amietppawar.numerology.AppConfig
import com.amietppawar.numerology.core.ChaldeanNumerology
import com.amietppawar.numerology.core.NameNumber
import com.amietppawar.numerology.data.local.NumerologyProfile
import com.amietppawar.numerology.data.repository.ProfileRepository
import com.amietppawar.numerology.ui.components.BodyText
import com.amietppawar.numerology.ui.components.CosmosButton
import com.amietppawar.numerology.ui.components.CosmosField
import com.amietppawar.numerology.ui.components.CosmosScreen
import com.amietppawar.numerology.ui.components.GlassCard
import com.amietppawar.numerology.ui.components.MutedText
import com.amietppawar.numerology.ui.components.NumberPair
import com.amietppawar.numerology.ui.components.SectionTitle
import com.amietppawar.numerology.ui.components.digitNode
import com.amietppawar.numerology.ui.components.drawLabel
import com.amietppawar.numerology.ui.components.drawSigil
import com.amietppawar.numerology.ui.theme.LocalReduceMotion
import com.amietppawar.numerology.ui.utils.ShareUtils
import kotlinx.coroutines.launch
import kotlin.math.min

/**
 * Name Check: type a name, watch the Reveal, read the full breakdown.
 * An optional second field compares another spelling side by side.
 */
@Composable
fun NameCheckScreen(
    profileRepository: ProfileRepository,
    pendingName: String,
    onPendingConsumed: () -> Unit,
    onBook: () -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val haptics = LocalHapticFeedback.current
    val reduceMotion = LocalReduceMotion.current
    val scope = rememberCoroutineScope()

    var input by rememberSaveable { mutableStateOf("") }
    var alternate by rememberSaveable { mutableStateOf("") }
    var shownName by rememberSaveable { mutableStateOf("") }
    var shownAlternate by rememberSaveable { mutableStateOf("") }
    var message by rememberSaveable { mutableStateOf("") }
    var runCount by remember { mutableStateOf(0) }
    val progress = remember { Animatable(if (shownName.isNotEmpty()) 1f else 0f) }
    var revealDone by remember { mutableStateOf(shownName.isNotEmpty()) }

    fun reveal(name: String, other: String) {
        val cleaned = name.trim()
        if (ChaldeanNumerology.calculateName(cleaned).isEmpty()) {
            message = "Please enter a name with at least one letter."
            return
        }
        message = ""
        shownName = cleaned
        shownAlternate = other.trim()
        runCount = runCount + 1
        focusManager.clearFocus()
    }

    // A name handed over from the home screen
    LaunchedEffect(pendingName) {
        if (pendingName.isNotBlank()) {
            input = pendingName
            alternate = ""
            reveal(pendingName, "")
            onPendingConsumed()
        }
    }

    // Run the Reveal each time a new name is submitted
    LaunchedEffect(runCount) {
        if (runCount > 0) {
            if (reduceMotion) {
                progress.snapTo(1f)
                revealDone = true
            } else {
                revealDone = false
                progress.snapTo(0f)
                progress.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 2800, easing = LinearEasing)
                )
                revealDone = true
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
            }
        }
    }

    CosmosScreen(
        title = "Name Check",
        subtitle = "Each letter has a Chaldean value from 1 to 8."
    ) {
        CosmosField(
            value = input,
            onValueChange = { input = it },
            label = "Full name",
            capitalization = KeyboardCapitalization.Words
        )
        Spacer(modifier = Modifier.height(12.dp))
        CosmosField(
            value = alternate,
            onValueChange = { alternate = it },
            label = "Another spelling to compare (optional)",
            capitalization = KeyboardCapitalization.Words
        )
        Spacer(modifier = Modifier.height(14.dp))
        CosmosButton(
            text = "Reveal",
            onClick = { reveal(input, alternate) },
            enabled = input.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        )
        if (message.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }

        if (shownName.isNotEmpty()) {
            val result = remember(shownName) { ChaldeanNumerology.calculateName(shownName) }
            val values = remember(shownName) { result.letters.map { it.value } }

            Spacer(modifier = Modifier.height(18.dp))
            RevealCanvas(result = result, progress = { progress.value })

            if (revealDone) {
                Spacer(modifier = Modifier.height(16.dp))
                ResultCard(title = shownName, result = result)

                val other = remember(shownAlternate) { ChaldeanNumerology.calculateName(shownAlternate) }
                if (!other.isEmpty()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    ResultCard(title = shownAlternate, result = other)
                    Spacer(modifier = Modifier.height(10.dp))
                    MutedText(
                        text = if (other.compound == result.compound) {
                            "Both spellings give the same compound number."
                        } else {
                            "\"" + shownName + "\" gives " + result.compound + " (" + result.reduced +
                                "). \"" + shownAlternate + "\" gives " + other.compound +
                                " (" + other.reduced + ")."
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CosmosButton(
                        text = "Save profile",
                        primary = false,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            scope.launch {
                                profileRepository.saveProfile(
                                    NumerologyProfile(
                                        name = shownName,
                                        nameNumber = result.reduced,
                                        nameNumberCompound = result.compound
                                    )
                                )
                                message = "Saved to your profiles."
                            }
                        }
                    )
                    CosmosButton(
                        text = "Share",
                        primary = false,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            val card = ShareUtils.createResultCard(
                                name = shownName,
                                compound = result.compound,
                                single = result.reduced,
                                letterValues = values
                            )
                            if (!ShareUtils.shareBitmap(context, card)) {
                                message = "Sharing is not available on this device."
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))
                MutedText(
                    text = "This is a basic name-number check. A full reading considers your " +
                        "date of birth, compound numbers and their combination."
                )
                Spacer(modifier = Modifier.height(10.dp))
                CosmosButton(
                    text = "Book a consultation",
                    onClick = onBook,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        MutedText(text = AppConfig.DISCLAIMER)
    }
}

/**
 * The Reveal. Letters lift off, fly to their digit on the ring, the sigil is
 * drawn, and the single number appears at the centre.
 */
@Composable
private fun RevealCanvas(result: NameNumber, progress: () -> Float) {
    val scheme = MaterialTheme.colorScheme
    val letterColor = scheme.onBackground
    val ringColor = scheme.primary
    val lineColor = scheme.tertiary
    val goldColor = scheme.secondary
    val backColor = scheme.background
    val letters = result.letters
    val values = remember(result) { letters.map { it.value } }

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(340.dp)
            .semantics {
                contentDescription = "Reveal animation. Compound number " + result.compound +
                    ", single number " + result.reduced + "."
            }
    ) {
        val now = progress()
        val count = letters.size
        val center = Offset(size.width / 2f, size.height * 0.58f)
        val ringRadius = min(size.width, size.height * 0.80f) / 2f * 0.86f
        val rowY = 22.dp.toPx()
        val spacing = min(26.dp.toPx(), size.width / (count + 1))
        val rowStart = size.width / 2f - spacing * (count - 1) / 2f

        // Ring and its eight digit nodes
        drawCircle(
            color = ringColor.copy(alpha = 0.45f),
            radius = ringRadius,
            center = center,
            style = Stroke(width = 1.dp.toPx())
        )
        val arrivals = IntArray(9)

        // Letters in flight
        for (i in 0 until count) {
            val letter = letters[i]
            val startAt = 0.55f * i / count
            val local = ((now - startAt) / 0.22f).coerceIn(0f, 1f)
            val eased = local * local * (3f - 2f * local)
            if (local >= 1f) {
                arrivals[letter.value] = arrivals[letter.value] + 1
            } else {
                val from = Offset(rowStart + spacing * i, rowY)
                val to = digitNode(center, ringRadius, letter.value)
                val x = from.x + (to.x - from.x) * eased
                val y = from.y + (to.y - from.y) * eased
                if (local > 0f) {
                    drawCircle(color = lineColor.copy(alpha = 0.25f), radius = 12.dp.toPx(), center = Offset(x, y))
                }
                drawLabel(
                    text = letter.letter.toString(),
                    x = x,
                    y = y,
                    color = letterColor,
                    sizePx = 16.sp.toPx(),
                    bold = true
                )
            }
        }

        for (digit in 1..8) {
            val node = digitNode(center, ringRadius, digit)
            val hits = arrivals[digit]
            val lit = hits > 0
            if (lit) {
                drawCircle(
                    color = goldColor.copy(alpha = 0.28f),
                    radius = (15 + min(hits, 5) * 2).dp.toPx(),
                    center = node
                )
            }
            drawCircle(color = backColor, radius = 12.dp.toPx(), center = node)
            drawCircle(
                color = if (lit) goldColor else ringColor,
                radius = 12.dp.toPx(),
                center = node,
                style = Stroke(width = 1.5.dp.toPx())
            )
            drawLabel(
                text = digit.toString(),
                x = node.x,
                y = node.y,
                color = if (lit) goldColor else ringColor,
                sizePx = 13.sp.toPx(),
                bold = true
            )
        }

        // The sigil, then the number
        val sigilProgress = ((now - 0.78f) / 0.17f).coerceIn(0f, 1f)
        if (sigilProgress > 0f) {
            drawSigil(
                values = values,
                center = center,
                radius = ringRadius * 0.62f,
                lineColor = lineColor,
                accentColor = goldColor,
                progress = sigilProgress,
                strokePx = 2.dp.toPx()
            )
        }
        val numberAlpha = ((now - 0.93f) / 0.07f).coerceIn(0f, 1f)
        if (numberAlpha > 0f) {
            drawCircle(color = backColor.copy(alpha = 0.80f * numberAlpha), radius = 30.dp.toPx(), center = center)
            drawLabel(
                text = result.reduced.toString(),
                x = center.x,
                y = center.y,
                color = goldColor.copy(alpha = numberAlpha),
                sizePx = 40.sp.toPx(),
                bold = true
            )
        }
    }
}

/** Letter values, word totals, compound and single number, and the meaning. */
@Composable
private fun ResultCard(title: String, result: NameNumber) {
    val scheme = MaterialTheme.colorScheme
    val words = remember(title) { ChaldeanNumerology.calculateWords(title) }

    GlassCard(accent = scheme.secondary) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = scheme.onSurface
        )
        Spacer(modifier = Modifier.height(12.dp))

        result.letters.chunked(8).forEach { rowLetters ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                rowLetters.forEach { item ->
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .width(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(scheme.surfaceVariant)
                            .padding(vertical = 5.dp)
                            .semantics {
                                contentDescription = item.letter.toString() + " is " + item.value
                            },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = item.letter.toString(),
                            style = MaterialTheme.typography.titleSmall,
                            color = scheme.onSurface
                        )
                        Text(
                            text = item.value.toString(),
                            style = MaterialTheme.typography.labelMedium,
                            color = scheme.tertiary
                        )
                    }
                }
            }
        }

        if (words.size > 1) {
            Spacer(modifier = Modifier.height(10.dp))
            MutedText(
                text = words.joinToString(separator = "   ") { it.word + " = " + it.total }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))
        NumberPair(title = "Name number", compound = result.compound, single = result.reduced)

        SectionTitle(text = "What " + result.reduced + " suggests")
        BodyText(text = AppConfig.NUMBER_MEANINGS[result.reduced] ?: "")
    }
}
