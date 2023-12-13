package com.simprints.intentlauncher.ui

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
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
import com.simprints.intentlauncher.ui.intent.IntegrationScreen
import com.simprints.intentlauncher.ui.details.ResultScreen
import com.simprints.intentlauncher.ui.history.HistoryScreen

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

    data object IntentDetails : Screen(
        route = "intent/{intentId}",
        titleId = R.string.screen_details,
        arguments = listOf(
            navArgument("intentId") { type = NavType.StringType },
        ),
    )

    // TODO add custom intent screen
}

fun NavGraphBuilder.rootNavGraph(navController: NavHostController) {
    composable(Screen.Intent.route) { IntegrationScreen(navController) }
    composable(Screen.History.route) { HistoryScreen(navController) }
    composable(
        Screen.IntentDetails.route,
        arguments = Screen.IntentDetails.arguments,
    ) { ResultScreen(navController) }
}
