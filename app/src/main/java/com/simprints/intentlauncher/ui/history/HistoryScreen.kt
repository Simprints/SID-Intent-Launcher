package com.simprints.intentlauncher.ui.history

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.simprints.intentlauncher.compose.NothingToDisplay
import com.simprints.intentlauncher.ui.extensions.collectAsStateLifecycleAware
import com.simprints.intentlauncher.ui.history.composables.HistoryItem

@Composable
fun HistoryScreen(
    navController: NavHostController,
) {
    val vm = hiltViewModel<HistoryViewModel>()
    val callList by vm.data.collectAsStateLifecycleAware()

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
    ) {
        TopAppBar(
            title = { Text("Intent History") },
            actions = {
                IconButton(onClick = { }) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete")
                }
            }
        )
        if (callList.isEmpty()) {
            NothingToDisplay(
                text = "No intent calls have been made yet.",
                modifier = Modifier
            )
        } else {
            LazyColumn {
                items(callList) { item ->
                    HistoryItem(
                        data = item,
                        onClick = { navController.navigate("intent/$it") },
                    )
                }
            }
        }
    }
}
