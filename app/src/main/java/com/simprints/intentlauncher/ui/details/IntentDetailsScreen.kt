package com.simprints.intentlauncher.ui.details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBars
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.simprints.intentlauncher.tools.collectAsStateLifecycleAware
import com.simprints.intentlauncher.tools.rememberFlow
import com.simprints.intentlauncher.ui.Screen
import com.simprints.intentlauncher.ui.composables.NavigateUpButton
import com.simprints.intentlauncher.ui.composables.ShowInputFieldAlertDialog
import com.simprints.intentlauncher.ui.composables.ToastLauncher
import com.simprints.intentlauncher.ui.details.composables.IntentDetails
import kotlinx.coroutines.flow.collectLatest

@Composable
fun IntentDetailsScreen(
    navController: NavHostController,
    intentId: String,
) {
    val vm = hiltViewModel<IntentDetailsViewModel>()
    val toastText = remember { mutableStateOf("") }
    ToastLauncher(toastText)
    val viewState by vm.state.collectAsStateLifecycleAware()
    LaunchedEffect(vm, intentId) { vm.onAction(IntentDetailsUiAction.LoadIntent(intentId)) }
    val lifecycleAwareEffects = rememberFlow(vm.effects)
    LaunchedEffect(lifecycleAwareEffects, navController) {
        lifecycleAwareEffects.collectLatest { effect ->
            when (effect) {
                IntentDetailsUiEffect.NavigateToIntent -> navController.navigate(Screen.Intent.route)
                IntentDetailsUiEffect.NavigateUp -> navController.navigateUp()
                is IntentDetailsUiEffect.ShowToast -> toastText.value = effect.message
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
            title = { Text("Intent details") },
            navigationIcon = { NavigateUpButton(navController) },
            actions = {
                IconButton(
                    onClick = {
                        vm.onAction(IntentDetailsUiAction.DeleteIntentClicked(intentId))
                    },
                ) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete")
                }
            },
        )
        IntentDetails(
            data = viewState.intentData,
            onCopyFields = {
                vm.onAction(IntentDetailsUiAction.CopyFieldsClicked)
            },
            onSavePreset = { vm.onAction(IntentDetailsUiAction.SavePresetDialogShown) },
        )
        ShowInputFieldAlertDialog(
            openDialog = viewState.isSaveDialogVisible,
            text = { Text(text = "Save the field values as a named preset for later use?") },
            label = { Text(text = "Preset name") },
            confirmButtonText = { Text(text = "Save") },
            onConfirm = {
                vm.onAction(
                    IntentDetailsUiAction.SavePresetConfirmed(
                        name = it.ifEmpty { viewState.intentData.fields.projectId },
                    ),
                )
            },
            onDismiss = { vm.onAction(IntentDetailsUiAction.SavePresetDialogDismissed) },
        )
    }
}
