package com.simprints.intentlauncher.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Typography
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val AppColorScheme = lightColors(
    primary = Color(0xFF00B3D1),
    secondary = Color(0xFFCC6300),
)

val AppTypography = Typography()

val AppShapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(8.dp),
)

@Composable
fun ApplicationTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = AppColorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content,
    )
}
