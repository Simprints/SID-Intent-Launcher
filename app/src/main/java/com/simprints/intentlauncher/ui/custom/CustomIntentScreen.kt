package com.simprints.intentlauncher.ui.custom

import android.content.ActivityNotFoundException
import android.content.Intent
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.simprints.intentlauncher.domain.IntentResult.Companion.ACTION_APP_NOT_FOUND
import com.simprints.intentlauncher.tools.GenericIntentContract
import com.simprints.intentlauncher.tools.collectAsStateLifecycleAware
import com.simprints.intentlauncher.tools.rememberFlow
import com.simprints.intentlauncher.ui.composables.AccordionLayout
import com.simprints.intentlauncher.ui.composables.SelectableCodeBlock
import com.simprints.intentlauncher.ui.custom.composables.CustomIntentFields
import kotlinx.coroutines.flow.collectLatest

@Composable
fun CustomIntentScreen(navController: NavHostController) {
    val vm = hiltViewModel<CustomIntentViewModel>()
    val viewState by vm.state.collectAsStateLifecycleAware()

    val intentLauncher = rememberLauncherForActivityResult(GenericIntentContract()) {
        vm.onAction(CustomIntentUiAction.IntentResultReceived(it))
    }

    val lifecycleAwareEffects = rememberFlow(vm.effects)
    LaunchedEffect(lifecycleAwareEffects, intentLauncher) {
        lifecycleAwareEffects.collectLatest { effect ->
            when (effect) {
                is CustomIntentUiEffect.LaunchIntent -> {
                    val intent = Intent(effect.action).apply {
                        effect.extras.forEach { (key, value) -> putExtra(key, value) }
                    }
                    try {
                        intentLauncher.launch(intent)
                    } catch (_: ActivityNotFoundException) {
                        vm.onAction(CustomIntentUiAction.IntentResultReceived(ACTION_APP_NOT_FOUND to null))
                    }
                }
            }
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
                    onClick = { vm.onAction(CustomIntentUiAction.ClearFieldsClicked) },
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
                onActionChange = { vm.onAction(CustomIntentUiAction.ActionChanged(it)) },
                onExtrasChange = { vm.onAction(CustomIntentUiAction.ExtrasChanged(it)) },
                onLaunch = { vm.onAction(CustomIntentUiAction.LaunchClicked) },
            )
            AccordionLayout(
                title = "Result",
                testTag = "resultsAccordion",
                defaultExpanded = true,
            ) {
                SelectableCodeBlock(viewState.result)
            }
        }
    }
}
