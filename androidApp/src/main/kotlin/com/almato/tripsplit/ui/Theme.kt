package com.almato.tripsplit.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = Color(0xFFDCE4FF),
    onPrimaryContainer = Color(0xFF002A78),
    inversePrimary = DarkPrimary,
    // Secondary: owed to you. Tertiary: you owe. Error stays for invalid input.
    secondary = LightOwedToYou,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFB7F1E2),
    onSecondaryContainer = Color(0xFF002019),
    tertiary = LightYouOwe,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFDCC5),
    onTertiaryContainer = Color(0xFF321200),
    background = LightBackground,
    onBackground = LightText,
    surface = LightSurface,
    onSurface = LightText,
    surfaceVariant = Color(0xFFE9EDF5),
    onSurfaceVariant = LightSecondaryText,
    surfaceTint = LightPrimary,
    inverseSurface = DarkSurface,
    inverseOnSurface = DarkText,
    surfaceBright = LightSurface,
    surfaceDim = Color(0xFFD8DDE7),
    surfaceContainerLowest = LightSurface,
    surfaceContainerLow = LightBackground,
    surfaceContainer = LightSurface,
    surfaceContainerHigh = Color(0xFFE9EDF5),
    surfaceContainerHighest = LightDivider,
    outline = LightSecondaryText,
    outlineVariant = LightDivider,
    error = LightError,
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
)

private val DarkColors = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = Color(0xFF1943AD),
    onPrimaryContainer = Color(0xFFDCE4FF),
    inversePrimary = LightPrimary,
    secondary = DarkOwedToYou,
    onSecondary = Color(0xFF00382D),
    secondaryContainer = Color(0xFF005143),
    onSecondaryContainer = Color(0xFFB7F1E2),
    tertiary = DarkYouOwe,
    onTertiary = Color(0xFF522300),
    tertiaryContainer = Color(0xFF763400),
    onTertiaryContainer = Color(0xFFFFDCC5),
    background = DarkBackground,
    onBackground = DarkText,
    surface = DarkSurface,
    onSurface = DarkText,
    surfaceVariant = Color(0xFF303642),
    onSurfaceVariant = DarkSecondaryText,
    surfaceTint = DarkPrimary,
    inverseSurface = LightSurface,
    inverseOnSurface = LightText,
    surfaceBright = Color(0xFF373D49),
    surfaceDim = DarkBackground,
    surfaceContainerLowest = Color(0xFF0C0E13),
    surfaceContainerLow = Color(0xFF171B22),
    surfaceContainer = DarkSurface,
    surfaceContainerHigh = Color(0xFF272D37),
    surfaceContainerHighest = Color(0xFF323945),
    outline = DarkSecondaryText,
    outlineVariant = DarkDivider,
    error = DarkError,
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
)

/** Material colours follow system appearance; pass darkTheme explicitly for previews. */
@Composable
fun TripSplitTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content,
    )
}
