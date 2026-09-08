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
import com.simprints.intentlauncher.domain.IntentCall
import com.simprints.intentlauncher.domain.IntentFields
import com.simprints.intentlauncher.tools.collectAsStateLifecycleAware
import com.simprints.intentlauncher.tools.rememberFlow
import com.simprints.intentlauncher.ui.composables.ShowInputFieldAlertDialog
import com.simprints.intentlauncher.ui.composables.ToastLauncher
import com.simprints.intentlauncher.ui.details.composables.IntentDetails
import com.simprints.intentlauncher.ui.navigation.IntentRoute
import com.simprints.intentlauncher.ui.navigation.Navigator
import com.simprints.intentlauncher.ui.theme.ApplicationTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
fun IntentDetailsScreen(
    navigator: Navigator,
    intentId: String,
) {
    val vm = hiltViewModel<IntentDetailsViewModel>()
    val toastText = remember { mutableStateOf("") }
    ToastLauncher(toastText)
    val viewState by vm.state.collectAsStateLifecycleAware()
    LaunchedEffect(vm, intentId) { vm.onAction(IntentDetailsUiAction.LoadIntent(intentId)) }
    val lifecycleAwareEffects = rememberFlow(vm.effects)
    LaunchedEffect(lifecycleAwareEffects, navigator) {
        lifecycleAwareEffects.collectLatest { effect ->
            when (effect) {
                IntentDetailsUiEffect.NavigateToIntent -> navigator.navigate(IntentRoute)
                IntentDetailsUiEffect.NavigateUp -> navigator.goBack()
                is IntentDetailsUiEffect.ShowToast -> toastText.value = effect.message
            }
        }
    }

    IntentDetailsContent(
        state = viewState,
        onNavigateUp = { navigator.goBack() },
        onAction = { vm.onAction(it) },
    )
}

@Composable
fun IntentDetailsContent(
    state: IntentDetailsUiState,
    onNavigateUp: () -> Unit,
    onAction: (IntentDetailsUiAction) -> Unit,
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
                IconButton(onClick = { onAction(IntentDetailsUiAction.DeleteIntentClicked(state.intentData.id)) }) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete")
                }
            },
        )
        IntentDetails(
            data = state.intentData,
            onCopyFields = { onAction(IntentDetailsUiAction.CopyFieldsClicked) },
            onSavePreset = { onAction(IntentDetailsUiAction.SavePresetDialogShown) },
        )
        ShowInputFieldAlertDialog(
            openDialog = state.isSaveDialogVisible,
            text = { Text(text = "Save the field values as a named preset for later use?") },
            label = { Text(text = "Preset name") },
            confirmButtonText = { Text(text = "Save") },
            onConfirm = { name ->
                onAction(
                    IntentDetailsUiAction.SavePresetConfirmed(
                        name = name.ifEmpty { state.intentData.fields.projectId },
                    ),
                )
            },
            onDismiss = { onAction(IntentDetailsUiAction.SavePresetDialogDismissed) },
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
            onAction = {},
        )
    }
}
