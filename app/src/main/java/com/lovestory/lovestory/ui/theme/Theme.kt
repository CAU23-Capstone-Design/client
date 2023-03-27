package com.lovestory.lovestory.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.currentCompositionLocalContext
import androidx.compose.ui.platform.LocalContext
import com.kakao.sdk.common.util.Utility

private val DarkColorPalette = darkColors(
    primary = Purple200,
    primaryVariant = Purple700,
    secondary = Teal200
)

private val LightColorPalette = lightColors(
    primary = Purple500,
    primaryVariant = Purple700,
    secondary = Teal200

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

private val LightColors = lightColorScheme(
    primary = Purple200,
    onPrimary = Purple700,
    primaryContainer = Teal200,
    onPrimaryContainer = Teal200,
// ..
)

private val DarkColors = darkColorScheme(
    primary = Purple200,
    onPrimary = Purple700,
    primaryContainer = Teal200,
    onPrimaryContainer = Teal200,
// ..
)

@Composable
fun LoveStoryTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }
    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}

@Composable
fun LoveStoryThemeForMD3(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme =
        if (!useDarkTheme) {
            LightColors
        } else {
            DarkColors
        }
    androidx.compose.material3.MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}