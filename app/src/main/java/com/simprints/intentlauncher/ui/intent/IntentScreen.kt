package com.simprints.intentlauncher.ui.intent

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.simprints.intentlauncher.ui.composables.AccordionLayout
import com.simprints.intentlauncher.ui.extensions.GenericIntentContract
import com.simprints.intentlauncher.ui.extensions.collectAsStateLifecycleAware
import com.simprints.intentlauncher.ui.intent.composables.BiometricFlowForm
import com.simprints.intentlauncher.ui.intent.composables.CoreFieldsForm
import com.simprints.intentlauncher.ui.intent.composables.FollowUpFlowForm
import de.palm.composestateevents.EventEffect


@Composable
fun IntegrationScreen(
    navController: NavHostController,
) {
    val vm = hiltViewModel<IntentViewModel>()
    val viewState by vm.viewState.collectAsStateLifecycleAware()

    val intentLauncher = rememberLauncherForActivityResult(GenericIntentContract()) {
        vm.intentReceived(it)
    }

    LaunchedEffect(key1 = vm) { vm.fetchCachedFieldValues() }

    EventEffect(
        event = viewState.showIntent,
        onConsumed = vm::intentShown,
    ) { intentLauncher.launch(it) }

    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
    ) {
        TopAppBar(
            title = { Text("Intent Launcher") },
            actions = {
                IconButton(onClick = { vm.clearFields() }) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete")
                }
            }
        )
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .verticalScroll(scrollState)
        ) {
            CoreFieldsForm(
                state = viewState,
                focusManager = focusManager,
                onProjectIdChange = vm::updateProjectId,
                onUserIdChange = vm::updateUserId,
                onModuleIdChange = vm::updateModuleId,
            )
            BiometricFlowForm(
                state = viewState,
                focusManager = focusManager,
                defaultExpanded = true,
                onGuidChange = vm::updateGuid,
                onEnroll = vm::enroll,
                onIdentify = vm::identify,
                onVerify = vm::verify,
            )
            FollowUpFlowForm(
                state = viewState,
                focusManager = focusManager,
                onSessionIdChanged = vm::updateSessionId,
                onConfirm = vm::confirm,
                onEnrolLast = vm::enrolLast,
            )
            AccordionLayout(
                title = "Result",
                defaultExpanded = true,
            ) {
                SelectionContainer {
                    Text(
                        text = viewState.result,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                            .background(Color.LightGray.copy(alpha = 0.2f))
                            .padding(4.dp)
                    )
                }
            }
        }
    }
}
