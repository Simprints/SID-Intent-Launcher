package com.simprints.intentlauncher.ui.details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.simprints.intentlauncher.tools.collectAsStateLifecycleAware
import com.simprints.intentlauncher.ui.Screen
import com.simprints.intentlauncher.ui.composables.NavigateUpButton
import com.simprints.intentlauncher.ui.composables.ShowInputFieldAlertDialog
import com.simprints.intentlauncher.ui.composables.ToastLauncher
import com.simprints.intentlauncher.ui.details.composables.IntentDetails

@Composable
fun IntentDetailsScreen(
    navController: NavHostController,
    intentId: String,
) {
    val vm = hiltViewModel<IntentDetailsViewModel>()

    val showSaveDialog = remember { mutableStateOf(false) }

    val toastText = remember { mutableStateOf("") }
    ToastLauncher(toastText)

    val intentData by vm.data.collectAsStateLifecycleAware()
    LaunchedEffect(key1 = vm) { vm.loadIntent(intentId) }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(),
    ) {
        TopAppBar(
            title = { Text("Intent details") },
            navigationIcon = { NavigateUpButton(navController) },
            actions = {
                IconButton(
                    onClick = {
                        vm.deleteIntent(intentId)
                        navController.navigateUp()
                    },
                ) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete")
                }
            },
        )
        IntentDetails(
            data = intentData,
            onCopyFields = {
                vm.copyFieldsToStore(intentData.fields)
                navController.navigate(Screen.Intent.route)
            },
            onSavePreset = { showSaveDialog.value = true },
        )
        ShowInputFieldAlertDialog(
            openDialog = showSaveDialog,
            text = { Text(text = "Save the field values as a named preset for later use?") },
            label = { Text(text = "Preset name") },
            confirmButtonText = { Text(text = "Save") },
            onConfirm = {
                vm.savePreset(it.ifEmpty { intentData.fields.projectId }, intentData.fields)
                toastText.value = "Preset saved"
            },
        )
    }
}
