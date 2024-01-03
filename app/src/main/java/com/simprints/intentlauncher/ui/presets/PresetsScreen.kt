package com.simprints.intentlauncher.ui.presets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.simprints.intentlauncher.tools.collectAsStateLifecycleAware
import com.simprints.intentlauncher.ui.Screen
import com.simprints.intentlauncher.ui.composables.NothingToDisplay
import com.simprints.intentlauncher.ui.composables.ShowBasicAlertDialog
import com.simprints.intentlauncher.ui.composables.ToastLauncher
import com.simprints.intentlauncher.ui.presets.composables.PresetItem

@Composable
fun PresetsScreen(
    navController: NavHostController,
) {
    val vm = hiltViewModel<PresetsViewModel>()
    val presetList by vm.data.collectAsStateLifecycleAware()

    val showDeleteToast = remember { mutableStateOf(false) }
    ToastLauncher(showDeleteToast, "Preset deleted")

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
    ) {
        TopAppBar(title = { Text("Presets") })
        if (presetList.isEmpty()) {
            NothingToDisplay("No presets have been saved yet.")
        } else {
            LazyColumn {
                items(presetList) { item ->
                    val showDeleteDialog = remember { mutableStateOf(false) }
                    PresetItem(
                        data = item,
                        onUse = {
                            vm.usePreset(item)
                            navController.navigate(Screen.Intent.route)
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
                            vm.deletePreset(item)
                            showDeleteToast.value = true
                        }
                    )
                }
            }
        }
    }
}
