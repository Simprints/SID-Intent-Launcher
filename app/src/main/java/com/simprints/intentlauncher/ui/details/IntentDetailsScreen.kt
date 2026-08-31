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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
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
import com.simprints.intentlauncher.domain.IntentCall
import com.simprints.intentlauncher.domain.IntentFields
import com.simprints.intentlauncher.tools.collectAsStateLifecycleAware
import com.simprints.intentlauncher.tools.rememberFlow
import com.simprints.intentlauncher.ui.Screen
import com.simprints.intentlauncher.ui.composables.ShowInputFieldAlertDialog
import com.simprints.intentlauncher.ui.composables.ToastLauncher
import com.simprints.intentlauncher.ui.details.composables.IntentDetails
import com.simprints.intentlauncher.ui.theme.ApplicationTheme
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

    IntentDetailsContent(
        state = viewState,
        onNavigateUp = { navController.navigateUp() },
        onDelete = { vm.onAction(IntentDetailsUiAction.DeleteIntentClicked(intentId)) },
        onCopyFields = { vm.onAction(IntentDetailsUiAction.CopyFieldsClicked) },
        onSavePresetShow = { vm.onAction(IntentDetailsUiAction.SavePresetDialogShown) },
        onSavePresetDismiss = { vm.onAction(IntentDetailsUiAction.SavePresetDialogDismissed) },
        onSavePresetConfirm = { name ->
            vm.onAction(
                IntentDetailsUiAction.SavePresetConfirmed(
                    name = name.ifEmpty { viewState.intentData.fields.projectId },
                ),
            )
        },
    )
}

@Composable
fun IntentDetailsContent(
    state: IntentDetailsUiState,
    onNavigateUp: () -> Unit,
    onDelete: () -> Unit,
    onCopyFields: () -> Unit,
    onSavePresetShow: () -> Unit,
    onSavePresetDismiss: () -> Unit,
    onSavePresetConfirm: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(),
    ) {
        TopAppBar(
            windowInsets = WindowInsets.statusBars,
            title = { Text("Intent details") },
            navigationIcon = {
                IconButton(onClick = onNavigateUp) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete")
                }
            },
        )
        IntentDetails(
            data = state.intentData,
            onCopyFields = onCopyFields,
            onSavePreset = onSavePresetShow,
        )
        ShowInputFieldAlertDialog(
            openDialog = state.isSaveDialogVisible,
            text = { Text(text = "Save the field values as a named preset for later use?") },
            label = { Text(text = "Preset name") },
            confirmButtonText = { Text(text = "Save") },
            onConfirm = onSavePresetConfirm,
            onDismiss = onSavePresetDismiss,
        )
    }
}

@Preview(showBackground = true, device = Devices.PHONE)
@Composable
private fun IntentDetailsContentPreview() {
    ApplicationTheme {
        IntentDetailsContent(
            state = IntentDetailsUiState(
                intentData = IntentCall(
                    id = "intent-123",
                    timestamp = "2024-01-01T10:00:00",
                    action = "com.simprints.id.REGISTER",
                    fields = IntentFields(projectId = "demo-project", moduleId = "module-1", userId = "user-1"),
                ),
                isSaveDialogVisible = false,
            ),
            onNavigateUp = {},
            onDelete = {},
            onCopyFields = {},
            onSavePresetShow = {},
            onSavePresetDismiss = {},
            onSavePresetConfirm = {},
        )
    }
}
