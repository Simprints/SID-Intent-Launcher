package com.simprints.intentlauncher.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import com.simprints.intentlauncher.R
import kotlinx.serialization.Serializable

@Serializable data object IntentRoute : NavKey

@Serializable data object HistoryRoute : NavKey

@Serializable data object PresetsRoute : NavKey

@Serializable data object CustomIntentRoute : NavKey

@Serializable data class IntentDetailsRoute(
    val intentId: String,
) : NavKey

@Serializable data class ResponseEventsRoute(
    val intentId: String,
) : NavKey

data class TopLevelRoute(
    val route: NavKey,
    val icon: ImageVector,
    @StringRes val titleId: Int,
)

val topLevelRoutes = listOf(
    TopLevelRoute(IntentRoute, Icons.AutoMirrored.Filled.Send, R.string.screen_intent),
    TopLevelRoute(HistoryRoute, Icons.AutoMirrored.Filled.List, R.string.screen_history),
    TopLevelRoute(PresetsRoute, Icons.Filled.Favorite, R.string.screen_presets),
)
