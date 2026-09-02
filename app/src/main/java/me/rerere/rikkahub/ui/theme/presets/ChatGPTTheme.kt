package me.rerere.rikkahub.ui.theme.presets

import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import me.rerere.rikkahub.ui.theme.PresetTheme

/**
 * ChatGPT 100% clone - pure white light, true dark #212121, pill input aura.
 * Light: #FFFFFF background, #F4F4F5 user bubble, #FFFFFF pill with #E5E5E5 border
 * Dark: #212121 background, #2F2F2F surface, #303030 pill, #565656 border - 100% ChatGPT iOS
 */
val ChatGPTThemePreset by lazy {
    PresetTheme(
        id = "chatgpt",
        name = {
            Text("ChatGPT")
        },
        standardLight = lightScheme,
        standardDark = darkScheme,
    )
}

private val primaryLight = Color(0xFF000000)
private val onPrimaryLight = Color(0xFFFFFFFF)
private val primaryContainerLight = Color(0xFFF4F4F4)
private val onPrimaryContainerLight = Color(0xFF000000)
private val secondaryLight = Color(0xFF6B6B6B)
private val onSecondaryLight = Color(0xFFFFFFFF)
private val secondaryContainerLight = Color(0xFFF4F4F4)
private val onSecondaryContainerLight = Color(0xFF0D0D0D)
private val tertiaryLight = Color(0xFF000000)
private val onTertiaryLight = Color(0xFFFFFFFF)
private val tertiaryContainerLight = Color(0xFFE8E8E8)
private val onTertiaryContainerLight = Color(0xFF000000)
private val errorLight = Color(0xFFD93A32)
private val onErrorLight = Color(0xFFFFFFFF)
private val errorContainerLight = Color(0xFFFDE7E5)
private val onErrorContainerLight = Color(0xFFA31710)
private val backgroundLight = Color(0xFFFFFFFF)
private val onBackgroundLight = Color(0xFF0D0D0D)
private val surfaceLight = Color(0xFFFFFFFF)
private val onSurfaceLight = Color(0xFF0D0D0D)
private val surfaceVariantLight = Color(0xFFF4F4F4)
private val onSurfaceVariantLight = Color(0xFF6B6B6B)
private val outlineLight = Color(0xFFE5E5E5)
private val outlineVariantLight = Color(0xFFE8E8E8)
private val scrimLight = Color(0xFF000000)
private val inverseSurfaceLight = Color(0xFF212121)
private val inverseOnSurfaceLight = Color(0xFFFFFFFF)
private val inversePrimaryLight = Color(0xFFFFFFFF)
private val surfaceDimLight = Color(0xFFF5F5F5)
private val surfaceBrightLight = Color(0xFFFFFFFF)
private val surfaceContainerLowestLight = Color(0xFFFFFFFF)
private val surfaceContainerLowLight = Color(0xFFF9F9F9)
private val surfaceContainerLight = Color(0xFFF4F4F4)
private val surfaceContainerHighLight = Color(0xFFECECEC)
private val surfaceContainerHighestLight = Color(0xFFE8E8E8)

private val primaryDark = Color(0xFFFFFFFF)
private val onPrimaryDark = Color(0xFF000000)
private val primaryContainerDark = Color(0xFF2F2F2F)
private val onPrimaryContainerDark = Color(0xFFFFFFFF)
private val secondaryDark = Color(0xFFACACBE)
private val onSecondaryDark = Color(0xFF2F2F2F)
private val secondaryContainerDark = Color(0xFF303030)
private val onSecondaryContainerDark = Color(0xFFECECEC)
private val tertiaryDark = Color(0xFFFFFFFF)
private val onTertiaryDark = Color(0xFF212121)
private val tertiaryContainerDark = Color(0xFF3A3A3A)
private val onTertiaryContainerDark = Color(0xFFFFFFFF)
private val errorDark = Color(0xFFFFB4AB)
private val onErrorDark = Color(0xFF690005)
private val errorContainerDark = Color(0xFF93000A)
private val onErrorContainerDark = Color(0xFFFFDAD6)
private val backgroundDark = Color(0xFF212121)
private val onBackgroundDark = Color(0xFFECECEC)
private val surfaceDark = Color(0xFF212121)
private val onSurfaceDark = Color(0xFFECECEC)
private val surfaceVariantDark = Color(0xFF2F2F2F)
private val onSurfaceVariantDark = Color(0xFFACACBE)
private val outlineDark = Color(0xFF424242)
private val outlineVariantDark = Color(0xFF303030)
private val scrimDark = Color(0xFF000000)
private val inverseSurfaceDark = Color(0xFFFFFFFF)
private val inverseOnSurfaceDark = Color(0xFF212121)
private val inversePrimaryDark = Color(0xFF000000)
private val surfaceDimDark = Color(0xFF171717)
private val surfaceBrightDark = Color(0xFF3A3A3A)
private val surfaceContainerLowestDark = Color(0xFF0F0F0F)
private val surfaceContainerLowDark = Color(0xFF212121)
private val surfaceContainerDark = Color(0xFF2F2F2F)
private val surfaceContainerHighDark = Color(0xFF303030)
private val surfaceContainerHighestDark = Color(0xFF3A3A3A)

private val lightScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight,
)

private val darkScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
    surfaceContainerHighest = surfaceContainerHighestDark,
)
