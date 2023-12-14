package com.simprints.intentlauncher.ui.history

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.simprints.intentlauncher.tools.collectAsStateLifecycleAware
import com.simprints.intentlauncher.ui.composables.NothingToDisplay
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
        TopAppBar(title = { Text("Intent History") })
        if (callList.isEmpty()) {
            NothingToDisplay("No intent calls have been made yet.",)
        } else {
            LazyColumn {
                items(callList) { item ->
                    HistoryItem(
                        data = item,
                        onClick = { navController.navigate("history/$it") },
                    )
                }
            }
        }
    }
}
