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
import com.simprints.intentlauncher.ui.results.ResultScreen
import com.simprints.intentlauncher.ui.results.ResultsListScreen

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

    data object Results : Screen(
        route = "results",
        titleId = R.string.screen_results,
        icon = Icons.Filled.List
    )

    data object Result : Screen(
        route = "results/{resultId}",
        titleId = R.string.screen_result,
        arguments = listOf(
            navArgument("resultId") { type = NavType.StringType },
        ),
    )

    // TODO add custom intent screen
}

fun NavGraphBuilder.rootNavGraph(navController: NavHostController) {
    composable(Screen.Intent.route) { IntegrationScreen(navController) }
    composable(Screen.Results.route) { ResultsListScreen(navController) }
    composable(
        Screen.Result.route,
        arguments = Screen.Result.arguments,
    ) { ResultScreen(navController) }
}
