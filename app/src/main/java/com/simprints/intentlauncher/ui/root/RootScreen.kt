package com.simprints.intentlauncher.ui.root

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.simprints.intentlauncher.ui.custom.CustomIntentScreen
import com.simprints.intentlauncher.ui.details.IntentDetailsScreen
import com.simprints.intentlauncher.ui.events.ResponseEventsScreen
import com.simprints.intentlauncher.ui.history.HistoryScreen
import com.simprints.intentlauncher.ui.intent.IntentScreen
import com.simprints.intentlauncher.ui.navigation.CustomIntentRoute
import com.simprints.intentlauncher.ui.navigation.HistoryRoute
import com.simprints.intentlauncher.ui.navigation.IntentDetailsRoute
import com.simprints.intentlauncher.ui.navigation.IntentRoute
import com.simprints.intentlauncher.ui.navigation.NavigationState
import com.simprints.intentlauncher.ui.navigation.Navigator
import com.simprints.intentlauncher.ui.navigation.PresetsRoute
import com.simprints.intentlauncher.ui.navigation.ResponseEventsRoute
import com.simprints.intentlauncher.ui.navigation.rememberNavigationState
import com.simprints.intentlauncher.ui.navigation.toEntries
import com.simprints.intentlauncher.ui.navigation.topLevelRoutes
import com.simprints.intentlauncher.ui.presets.PresetsScreen

@Composable
fun RootScreen() {
    val navState: NavigationState = rememberNavigationState(
        startRoute = IntentRoute,
        topLevelRoutes = setOf(IntentRoute, HistoryRoute, PresetsRoute),
    )
    val navigator = remember(navState) {
        Navigator(
            topLevelRoutes = setOf(IntentRoute, HistoryRoute, PresetsRoute),
            getNavigationState = { navState },
        )
    }

    val entries = navState.toEntries(
        entryProvider<NavKey> {
            entry<IntentRoute> { IntentScreen(navigator) }
            entry<HistoryRoute> { HistoryScreen(navigator) }
            entry<PresetsRoute> { PresetsScreen(navigator) }
            entry<CustomIntentRoute> { CustomIntentScreen(navigator) }
            entry<IntentDetailsRoute> { route -> IntentDetailsScreen(navigator, route.intentId) }
            entry<ResponseEventsRoute> { route -> ResponseEventsScreen(navigator, route.intentId) }
        },
    )

    Scaffold(
        bottomBar = {
            BottomNavigation(windowInsets = WindowInsets.navigationBars) {
                topLevelRoutes.forEach { item ->
                    BottomNavigationItem(
                        icon = { Icon(item.icon, contentDescription = null) },
                        label = { Text(stringResource(item.titleId)) },
                        selected = navState.topLevelRoute == item.route,
                        onClick = { navigator.navigate(item.route) },
                    )
                }
            }
        },
    ) { innerPadding ->
        NavDisplay(
            entries = entries,
            onBack = { navigator.goBack() },
            modifier = Modifier.padding(innerPadding),
        )
    }
}
