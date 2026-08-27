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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.simprints.intentlauncher.tools.collectAsStateLifecycleAware
import com.simprints.intentlauncher.tools.rememberFlow
import com.simprints.intentlauncher.ui.Screen
import com.simprints.intentlauncher.ui.composables.NothingToDisplay
import com.simprints.intentlauncher.ui.composables.ShowBasicAlertDialog
import com.simprints.intentlauncher.ui.composables.ToastLauncher
import com.simprints.intentlauncher.ui.presets.composables.PresetItem
import kotlinx.coroutines.flow.collectLatest

@Composable
fun PresetsScreen(navController: NavHostController) {
    val vm = hiltViewModel<PresetsViewModel>()
    val viewState by vm.state.collectAsStateLifecycleAware()
    LaunchedEffect(vm) { vm.onAction(PresetsUiAction.LoadPresets) }

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

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(),
    ) {
        TopAppBar(
            windowInsets = WindowInsets.statusBars,
            title = { Text("Presets") },
        )
        if (viewState.items.isEmpty()) {
            NothingToDisplay("No presets have been saved yet.")
        } else {
            LazyColumn {
                items(viewState.items) { item ->
                    val showDeleteDialog = remember { mutableStateOf(false) }
                    PresetItem(
                        data = item,
                        onUse = {
                            vm.onAction(PresetsUiAction.UsePresetClicked(item))
                        },
                        onDelete = {
                            showDeleteDialog.value = true
                        },
                    )
                    ShowBasicAlertDialog(
                        openDialog = showDeleteDialog,
                        title = { Text(text = "Delete preset?") },
                        confirmButtonText = { Text(text = "Delete") },
                        onConfirm = {
                            vm.onAction(PresetsUiAction.DeletePresetClicked(item))
                        },
                    )
                }
            }
        }
    }
}
