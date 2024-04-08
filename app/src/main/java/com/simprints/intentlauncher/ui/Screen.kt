package com.simprints.intentlauncher.ui

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Send
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.simprints.intentlauncher.R
import com.simprints.intentlauncher.ui.custom.CustomIntentScreen
import com.simprints.intentlauncher.ui.details.IntentDetailsScreen
import com.simprints.intentlauncher.ui.events.ResponseEventsScreen
import com.simprints.intentlauncher.ui.history.HistoryScreen
import com.simprints.intentlauncher.ui.intent.IntentScreen
import com.simprints.intentlauncher.ui.presets.PresetsScreen

sealed class Screen(
    val route: String,
    @StringRes val titleId: Int,
    val arguments: List<NamedNavArgument> = emptyList(),
    val icon: ImageVector? = null,
) {

    data object Intent : Screen(
        route = "intent",
        titleId = R.string.screen_intent,
        icon = Icons.Filled.Send
    )

    data object History : Screen(
        route = "history",
        titleId = R.string.screen_history,
        icon = Icons.Filled.List
    )

    data object Presets : Screen(
        route = "presets",
        titleId = R.string.screen_presets,
        icon = Icons.Filled.Favorite
    )

    data object IntentDetails : Screen(
        route = "history/{intentId}",
        titleId = R.string.screen_details,
        arguments = listOf(
            navArgument("intentId") { type = NavType.StringType },
        ),
    ) {
        fun createRoute(intentId: String) = "history/$intentId"
    }

    data object CustomIntent : Screen(
        route = "custom",
        titleId = R.string.screen_custom_intent,
        icon = Icons.Filled.Send
    )

    data object ResponseEventDetails : Screen(
        route = "responseEvents/{intentId}",
        titleId = R.string.screen_response_events,
        arguments = listOf(
            navArgument("intentId") { type = NavType.StringType },
        ),
    ) {
        fun createRoute(intentId: String) =
            if (intentId.isEmpty()) "responseEvents/null" else "responseEvents/$intentId"
    }
}

fun NavGraphBuilder.rootNavGraph(navController: NavHostController) {
    composable(Screen.Intent.route) { IntentScreen(navController) }
    composable(Screen.History.route) { HistoryScreen(navController) }
    composable(Screen.Presets.route) { PresetsScreen(navController) }
    composable(
        Screen.IntentDetails.route,
        arguments = Screen.IntentDetails.arguments,
    ) { backStackEntry ->
        IntentDetailsScreen(
            navController,
            backStackEntry.arguments?.getString("intentId").orEmpty()
        )
    }
    composable(Screen.CustomIntent.route) { CustomIntentScreen(navController) }
    composable(
        Screen.ResponseEventDetails.route,
        arguments = Screen.ResponseEventDetails.arguments
    ) { backStackEntry ->
        ResponseEventsScreen(
            navController,
            intentId = backStackEntry.arguments?.getString("intentId").orEmpty()
        )
    }
}
