package com.amietppawar.numerology.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ---------------------------------------------------------------------------
// The Number Cosmos palette. Change these values to re-colour the whole app.
// ---------------------------------------------------------------------------
val SpaceBlackIndigo = Color(0xFF07060F)
val MidnightViolet = Color(0xFF1A1140)
val ElectricViolet = Color(0xFF7B5CFF)
val CyanGlow = Color(0xFF3EE6FF)
val AntiqueGold = Color(0xFFD9B45A)
val MoonWhite = Color(0xFFECE9F7)

private val DarkColors = darkColorScheme(
    primary = ElectricViolet,
    onPrimary = SpaceBlackIndigo,
    primaryContainer = MidnightViolet,
    onPrimaryContainer = MoonWhite,
    secondary = AntiqueGold,
    onSecondary = SpaceBlackIndigo,
    tertiary = CyanGlow,
    onTertiary = SpaceBlackIndigo,
    background = SpaceBlackIndigo,
    onBackground = MoonWhite,
    surface = MidnightViolet,
    onSurface = MoonWhite,
    surfaceVariant = Color(0xFF241A52),
    onSurfaceVariant = Color(0xFFC3BDD9),
    outline = Color(0xFF6E6791),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

private val LightColors = lightColorScheme(
    primary = Color(0xFF5B3FE0),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE9E3FF),
    onPrimaryContainer = Color(0xFF1B0F52),
    secondary = Color(0xFF7A5C0E),
    onSecondary = Color(0xFFFFFFFF),
    tertiary = Color(0xFF00707F),
    onTertiary = Color(0xFFFFFFFF),
    background = Color(0xFFF8F6FF),
    onBackground = Color(0xFF16122B),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF16122B),
    surfaceVariant = Color(0xFFEDE9FA),
    onSurfaceVariant = Color(0xFF4A4563),
    outline = Color(0xFF8C86A8),
    error = Color(0xFFB3261E),
    onError = Color(0xFFFFFFFF)
)

// Serif for display headings, the system sans-serif for reading text.
// To use custom fonts later, place the .ttf files in res/font and swap the
// two families below.
private val DisplayFamily = FontFamily.Serif
private val BodyFamily = FontFamily.Default

val AppTypography = Typography(
    displayLarge = TextStyle(fontFamily = DisplayFamily, fontWeight = FontWeight.Bold, fontSize = 52.sp, lineHeight = 58.sp),
    displayMedium = TextStyle(fontFamily = DisplayFamily, fontWeight = FontWeight.Bold, fontSize = 42.sp, lineHeight = 48.sp),
    displaySmall = TextStyle(fontFamily = DisplayFamily, fontWeight = FontWeight.Bold, fontSize = 34.sp, lineHeight = 40.sp),
    headlineLarge = TextStyle(fontFamily = DisplayFamily, fontWeight = FontWeight.Bold, fontSize = 30.sp, lineHeight = 36.sp),
    headlineMedium = TextStyle(fontFamily = DisplayFamily, fontWeight = FontWeight.Bold, fontSize = 26.sp, lineHeight = 32.sp),
    headlineSmall = TextStyle(fontFamily = DisplayFamily, fontWeight = FontWeight.Bold, fontSize = 22.sp, lineHeight = 28.sp),
    titleLarge = TextStyle(fontFamily = BodyFamily, fontWeight = FontWeight.Bold, fontSize = 20.sp, lineHeight = 26.sp),
    titleMedium = TextStyle(fontFamily = BodyFamily, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 22.sp),
    titleSmall = TextStyle(fontFamily = BodyFamily, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 20.sp),
    bodyLarge = TextStyle(fontFamily = BodyFamily, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontFamily = BodyFamily, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 21.sp),
    bodySmall = TextStyle(fontFamily = BodyFamily, fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 17.sp),
    labelLarge = TextStyle(fontFamily = BodyFamily, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, lineHeight = 20.sp),
    labelMedium = TextStyle(fontFamily = BodyFamily, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, lineHeight = 16.sp),
    labelSmall = TextStyle(fontFamily = BodyFamily, fontWeight = FontWeight.SemiBold, fontSize = 11.sp, lineHeight = 14.sp)
)

/** True when animations should be replaced by calm, instant alternatives. */
val LocalReduceMotion = compositionLocalOf { false }

/**
 * @param themeMode "dark" (default), "light" or "system".
 */
@Composable
fun AmietPpawarNumerologyTheme(
    themeMode: String = "dark",
    content: @Composable () -> Unit
) {
    val dark = when (themeMode) {
        "light" -> false
        "system" -> isSystemInDarkTheme()
        else -> true
    }
    MaterialTheme(
        colorScheme = if (dark) DarkColors else LightColors,
        typography = AppTypography,
        content = content
    )
}
