package com.simprints.intentlauncher.ui.intent

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.simprints.intentlauncher.tools.GenericIntentContract
import com.simprints.intentlauncher.tools.collectAsStateLifecycleAware
import com.simprints.intentlauncher.ui.Screen
import com.simprints.intentlauncher.ui.composables.AccordionLayout
import com.simprints.intentlauncher.ui.composables.SelectableCodeBlock
import com.simprints.intentlauncher.ui.intent.composables.BiometricFlowForm
import com.simprints.intentlauncher.ui.intent.composables.CoreFieldsForm
import com.simprints.intentlauncher.ui.intent.composables.FollowUpFlowForm
import de.palm.composestateevents.EventEffect


@Composable
fun IntentScreen(
    navController: NavHostController,
) {
    val vm = hiltViewModel<IntentViewModel>()
    val viewState by vm.viewState.collectAsStateLifecycleAware()

    LaunchedEffect(key1 = vm) { vm.fetchCachedFieldValues() }

    val intentLauncher = rememberLauncherForActivityResult(GenericIntentContract()) {
        vm.intentResultReceived(it)
    }
    EventEffect(
        event = viewState.showIntent,
        onConsumed = vm::intentShown,
    ) { intentLauncher.launch(it) }

    val menuExpanded = remember { mutableStateOf(false) }
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
                IconButton(
                    onClick = { menuExpanded.value = true }) {
                    Icon(Icons.Filled.MoreVert, contentDescription = "More")
                }
                DropdownMenu(
                    expanded = menuExpanded.value,
                    onDismissRequest = { menuExpanded.value = false }
                ) {
                    DropdownMenuItem(
                        onClick = {
                            menuExpanded.value = false
                            vm.clearFields()
                        }
                    ) {
                        Text("Clear fields")
                    }
                    DropdownMenuItem(
                        onClick = {
                            menuExpanded.value = false
                            navController.navigate(Screen.CustomIntent.route)
                        }
                    ) {
                        Text("Custom Intent")
                    }
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
                SelectableCodeBlock(viewState.result)
            }
        }
    }
}
