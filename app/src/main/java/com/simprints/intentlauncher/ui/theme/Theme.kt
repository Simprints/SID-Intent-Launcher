package com.simprints.intentlauncher.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val AppColorScheme = lightColors(
    primary = Color(0xFF00B3D1),
    secondary = Color(0xFFCC6300),
)

val AppTypography = Typography(
)

@Composable
fun ApplicationTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colors = AppColorScheme,
        typography = AppTypography,
        content = content
    )
}
