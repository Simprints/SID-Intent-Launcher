package com.simprints.intentlauncher.ui.custom

import android.content.ActivityNotFoundException
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.simprints.intentlauncher.domain.IntentResult.Companion.ACTION_APP_NOT_FOUND
import com.simprints.intentlauncher.tools.GenericIntentContract
import com.simprints.intentlauncher.tools.collectAsStateLifecycleAware
import com.simprints.intentlauncher.ui.composables.AccordionLayout
import com.simprints.intentlauncher.ui.composables.SelectableCodeBlock
import com.simprints.intentlauncher.ui.custom.composables.CustomIntentFields
import de.palm.composestateevents.EventEffect

@Composable
fun CustomIntentScreen(navController: NavHostController) {
    val vm = hiltViewModel<CustomIntentViewModel>()
    val viewState by vm.viewState.collectAsStateLifecycleAware()

    LaunchedEffect(key1 = vm) { vm.fetchCachedFieldValues() }

    val intentLauncher = rememberLauncherForActivityResult(GenericIntentContract()) {
        vm.intentResultReceived(it)
    }
    EventEffect(
        event = viewState.showIntent,
        onConsumed = vm::intentShown,
    ) {
        try {
            intentLauncher.launch(it)
        } catch (e: ActivityNotFoundException) {
            vm.intentResultReceived(ACTION_APP_NOT_FOUND to null)
        }
    }

    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(),
    ) {
        TopAppBar(
            windowInsets = WindowInsets.statusBars,
            title = { Text("Custom intent") },
            actions = {
                IconButton(
                    onClick = { vm.clearFields() },
                ) {
                    Icon(Icons.Filled.Delete, contentDescription = "Clear")
                }
            },
        )
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .verticalScroll(scrollState),
        ) {
            CustomIntentFields(
                viewState,
                focusManager,
                onActionChange = vm::updateAction,
                onExtrasChange = vm::updateExtras,
                onLaunch = { vm.launchIntent() },
            )
            AccordionLayout(
                title = "Result",
                defaultExpanded = true,
            ) {
                SelectableCodeBlock(viewState.result)
            }
        }
    }
}
