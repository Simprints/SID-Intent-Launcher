package com.simprints.intentlauncher.ui.intent

import android.content.ActivityNotFoundException
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.simprints.intentlauncher.tools.collectAsStateLifecycleAware
import com.simprints.intentlauncher.tools.rememberFlow
import com.simprints.intentlauncher.ui.composables.AccordionLayout
import com.simprints.intentlauncher.ui.composables.SelectableCodeBlock
import com.simprints.intentlauncher.ui.composables.ShowErrorAlert
import com.simprints.intentlauncher.ui.intent.composables.BiometricFlowForm
import com.simprints.intentlauncher.ui.intent.composables.CoreFieldsForm
import com.simprints.intentlauncher.ui.intent.composables.FollowUpFlowForm
import com.simprints.intentlauncher.ui.intent.composables.ResponseEventsForm
import com.simprints.intentlauncher.ui.navigation.CustomIntentRoute
import com.simprints.intentlauncher.ui.navigation.Navigator
import com.simprints.intentlauncher.ui.navigation.ResponseEventsRoute
import com.simprints.intentlauncher.ui.theme.ApplicationTheme
import com.simprints.libsimprints.contracts.SimprintsContract
import kotlinx.coroutines.flow.collectLatest

@Composable
fun IntentScreen(navigator: Navigator) {
    val vm = hiltViewModel<IntentViewModel>()
    val viewState by vm.state.collectAsStateLifecycleAware()
    LaunchedEffect(vm) { vm.loadCachedFields() }

    val intentLauncher = rememberLauncherForActivityResult(SimprintsContract()) {
        vm.onAction(IntentUiAction.IntentResultReceived(it))
    }

    val lifecycleAwareEffects = rememberFlow(vm.effects)
    LaunchedEffect(lifecycleAwareEffects, intentLauncher) {
        lifecycleAwareEffects.collectLatest { effect ->
            when (effect) {
                is IntentUiEffect.LaunchSimprintsIntent -> {
                    try {
                        intentLauncher.launch(effect.request)
                    } catch (_: ActivityNotFoundException) {
                        vm.onAction(IntentUiAction.IntentResultReceived(null))
                    }
                }
            }
        }
    }

    IntentContent(
        state = viewState,
        onAction = { vm.onAction(it) },
        onNavigateToResponseEvents = { navigator.navigate(ResponseEventsRoute(it)) },
        onNavigateToCustomIntent = { navigator.navigate(CustomIntentRoute) },
    )
}

@Composable
fun IntentContent(
    state: IntentViewUiState,
    onAction: (IntentUiAction) -> Unit,
    onNavigateToResponseEvents: (String) -> Unit,
    onNavigateToCustomIntent: () -> Unit,
) {
    val menuExpanded = remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(),
    ) {
        TopAppBar(
            windowInsets = WindowInsets.statusBars,
            title = { Text("Intent Launcher") },
            actions = {
                IconButton(onClick = { menuExpanded.value = true }) {
                    Icon(Icons.Filled.MoreVert, contentDescription = "More")
                }
                DropdownMenu(
                    expanded = menuExpanded.value,
                    onDismissRequest = { menuExpanded.value = false },
                ) {
                    DropdownMenuItem(
                        onClick = {
                            menuExpanded.value = false
                            onAction(IntentUiAction.ClearFieldsClicked)
                        },
                    ) {
                        Text("Clear fields")
                    }
                    DropdownMenuItem(
                        onClick = {
                            menuExpanded.value = false
                            onNavigateToCustomIntent()
                        },
                    ) {
                        Text("Custom Intent")
                    }
                }
            },
        )
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            CoreFieldsForm(
                state = state,
                focusManager = focusManager,
                onProjectIdChange = { onAction(IntentUiAction.ProjectIdChanged(it)) },
                onUserIdChange = { onAction(IntentUiAction.UserIdChanged(it)) },
                onModuleIdChange = { onAction(IntentUiAction.ModuleIdChanged(it)) },
                onMetadataChange = { onAction(IntentUiAction.MetadataChanged(it)) },
            )
            BiometricFlowForm(
                state = state,
                focusManager = focusManager,
                defaultExpanded = true,
                onGuidChange = { onAction(IntentUiAction.GuidChanged(it)) },
                onEnroll = { onAction(IntentUiAction.EnrollClicked) },
                onIdentify = { onAction(IntentUiAction.IdentifyClicked) },
                onVerify = { onAction(IntentUiAction.VerifyClicked) },
            )
            FollowUpFlowForm(
                state = state,
                focusManager = focusManager,
                onSessionIdChanged = { onAction(IntentUiAction.SessionIdChanged(it)) },
                onConfirm = { onAction(IntentUiAction.ConfirmClicked) },
                onEnrolLast = { onAction(IntentUiAction.EnrolLastClicked) },
            )
            ResponseEventsForm(
                state = state,
                onClick = onNavigateToResponseEvents,
            )
            AccordionLayout(
                title = "Result",
                testTag = "resultAccordion",
                defaultExpanded = true,
            ) {
                SelectableCodeBlock(state.result)
            }
        }
    }
    ShowErrorAlert(
        openDialog = state.isWrongMetadataAlertVisible,
        title = { Text(text = "Wrong input") },
        text = { Text(text = "Metadata field should contain valid JSON string") },
        confirmButtonText = { Text(text = "OK") },
        onConfirm = {},
        onDismiss = { onAction(IntentUiAction.WrongMetadataAlertDismissed) },
    )
}

@Preview(showBackground = true, device = Devices.PHONE)
@Composable
private fun IntentContentPreviewEmpty() {
    ApplicationTheme {
        IntentContent(
            state = IntentViewUiState(),
            onAction = {},
            onNavigateToResponseEvents = {},
            onNavigateToCustomIntent = {},
        )
    }
}

@Preview(showBackground = true, device = Devices.PHONE)
@Composable
private fun IntentContentPreviewFilled() {
    ApplicationTheme {
        IntentContent(
            state = IntentViewUiState(
                projectId = "demo-project",
                userId = "user-1",
                moduleId = "module-1",
                metadata = """{"key":"value"}""",
                result = "ENROL COMPLETED",
            ),
            onAction = {},
            onNavigateToResponseEvents = {},
            onNavigateToCustomIntent = {},
        )
    }
}
