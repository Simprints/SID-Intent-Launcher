package com.simprints.intentlauncher.ui.presets

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.simprints.intentlauncher.domain.IntentFields
import com.simprints.intentlauncher.domain.Preset
import com.simprints.intentlauncher.tools.collectAsStateLifecycleAware
import com.simprints.intentlauncher.tools.rememberFlow
import com.simprints.intentlauncher.ui.Screen
import com.simprints.intentlauncher.ui.composables.NothingToDisplay
import com.simprints.intentlauncher.ui.composables.ShowBasicAlertDialog
import com.simprints.intentlauncher.ui.composables.ToastLauncher
import com.simprints.intentlauncher.ui.presets.composables.PresetItem
import com.simprints.intentlauncher.ui.theme.ApplicationTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
fun PresetsScreen(navController: NavHostController) {
    val vm = hiltViewModel<PresetsViewModel>()
    val viewState by vm.state.collectAsStateLifecycleAware()

    val toastText = remember { mutableStateOf("") }
    ToastLauncher(toastText)
    val lifecycleAwareEffects = rememberFlow(vm.effects)
    LaunchedEffect(lifecycleAwareEffects) {
        lifecycleAwareEffects.collectLatest { effect ->
            when (effect) {
                PresetsUiEffect.NavigateToIntent -> navController.navigate(Screen.Intent.route)
                is PresetsUiEffect.ShowToast -> toastText.value = effect.message
            }
        }
    }

    PresetsContent(
        items = viewState.items,
        onUsePreset = { vm.onAction(PresetsUiAction.UsePresetClicked(it)) },
        onDeletePreset = { vm.onAction(PresetsUiAction.DeletePresetClicked(it)) },
    )
}

@Composable
fun PresetsContent(
    items: List<Preset>,
    onUsePreset: (Preset) -> Unit,
    onDeletePreset: (Preset) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(),
    ) {
        TopAppBar(
            windowInsets = WindowInsets.statusBars,
            title = { Text("Presets") },
        )
        if (items.isEmpty()) {
            NothingToDisplay("No presets have been saved yet.")
        } else {
            LazyColumn {
                items(items) { item ->
                    val showDeleteDialog = remember { mutableStateOf(false) }
                    PresetItem(
                        data = item,
                        onUse = { onUsePreset(item) },
                        onDelete = { showDeleteDialog.value = true },
                    )
                    ShowBasicAlertDialog(
                        openDialog = showDeleteDialog,
                        title = { Text(text = "Delete preset?") },
                        confirmButtonText = { Text(text = "Delete") },
                        onConfirm = { onDeletePreset(item) },
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, device = Devices.PHONE)
@Composable
private fun PresetsContentPreviewEmpty() {
    ApplicationTheme {
        PresetsContent(
            items = emptyList(),
            onUsePreset = {},
            onDeletePreset = {},
        )
    }
}

@Preview(showBackground = true, device = Devices.PHONE)
@Composable
private fun PresetsContentPreviewWithItems() {
    ApplicationTheme {
        PresetsContent(
            items = listOf(
                Preset(
                    id = "1",
                    name = "Enrolment preset",
                    fields = IntentFields(projectId = "demo", moduleId = "module-1", userId = "user-1"),
                ),
                Preset(
                    id = "2",
                    name = "Verification preset",
                    fields = IntentFields(projectId = "demo", moduleId = "module-2", userId = "user-2"),
                ),
            ),
            onUsePreset = {},
            onDeletePreset = {},
        )
    }
}
