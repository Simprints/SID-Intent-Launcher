package com.simprints.intentlauncher.ui.history

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.simprints.intentlauncher.tools.collectAsStateLifecycleAware
import com.simprints.intentlauncher.ui.Screen
import com.simprints.intentlauncher.ui.composables.NothingToDisplay
import com.simprints.intentlauncher.ui.history.composables.HistoryItem

@Composable
fun HistoryScreen(navController: NavHostController) {
    val vm = hiltViewModel<HistoryViewModel>()
    val viewState by vm.state.collectAsStateLifecycleAware()

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(),
    ) {
        TopAppBar(
            windowInsets = WindowInsets.statusBars,
            title = { Text("Intent History") },
        )
        if (viewState.items.isEmpty()) {
            NothingToDisplay("No intent calls have been made yet.")
        } else {
            LazyColumn {
                items(viewState.items) { item ->
                    HistoryItem(
                        data = item,
                        onClick = { navController.navigate(Screen.IntentDetails.createRoute(it)) },
                    )
                }
            }
        }
    }
}
