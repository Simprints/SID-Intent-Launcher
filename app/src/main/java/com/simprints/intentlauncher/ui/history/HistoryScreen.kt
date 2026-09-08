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
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.simprints.intentlauncher.domain.IntentCall
import com.simprints.intentlauncher.domain.IntentFields
import com.simprints.intentlauncher.tools.collectAsStateLifecycleAware
import com.simprints.intentlauncher.ui.composables.NothingToDisplay
import com.simprints.intentlauncher.ui.history.composables.HistoryItem
import com.simprints.intentlauncher.ui.navigation.IntentDetailsRoute
import com.simprints.intentlauncher.ui.navigation.Navigator
import com.simprints.intentlauncher.ui.theme.ApplicationTheme

@Composable
fun HistoryScreen(navigator: Navigator) {
    val vm = hiltViewModel<HistoryViewModel>()
    val viewState by vm.state.collectAsStateLifecycleAware()

    HistoryContent(
        items = viewState.items,
        onItemClick = { navigator.navigate(IntentDetailsRoute(it)) },
    )
}

@Composable
fun HistoryContent(
    items: List<IntentCall>,
    onItemClick: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(),
    ) {
        TopAppBar(
            windowInsets = WindowInsets.statusBars,
            title = { Text("Intent History") },
        )
        if (items.isEmpty()) {
            NothingToDisplay("No intent calls have been made yet.")
        } else {
            LazyColumn {
                items(items) { item ->
                    HistoryItem(
                        data = item,
                        onClick = onItemClick,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, device = Devices.PHONE)
@Composable
private fun HistoryContentPreviewEmpty() {
    ApplicationTheme {
        HistoryContent(
            items = emptyList(),
            onItemClick = {},
        )
    }
}

@Preview(showBackground = true, device = Devices.PHONE)
@Composable
private fun HistoryContentPreviewWithItems() {
    ApplicationTheme {
        HistoryContent(
            items = listOf(
                IntentCall(
                    id = "1",
                    timestamp = "2024-01-01T10:00:00",
                    action = "com.simprints.id.REGISTER",
                    fields = IntentFields(projectId = "demo-project", moduleId = "module-1", userId = "user-1"),
                ),
                IntentCall(
                    id = "2",
                    timestamp = "2024-01-01T11:00:00",
                    action = "com.simprints.id.IDENTIFY",
                    fields = IntentFields(projectId = "demo-project", moduleId = "module-1", userId = "user-2"),
                ),
            ),
            onItemClick = {},
        )
    }
}
