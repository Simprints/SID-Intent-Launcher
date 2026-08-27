package com.simprints.intentlauncher.ui.events

import android.content.ClipData
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.simprints.intentlauncher.tools.collectAsStateLifecycleAware
import com.simprints.intentlauncher.tools.rememberFlow
import com.simprints.intentlauncher.ui.composables.NavigateUpButton
import com.simprints.intentlauncher.ui.composables.PropertyField
import com.simprints.intentlauncher.ui.composables.ToastLauncher
import com.simprints.intentlauncher.ui.events.composables.EventActionsDialog
import com.simprints.intentlauncher.ui.events.composables.ResponseEventItem
import com.simprints.intentlauncher.ui.events.composables.SortingOptionsDialog
import com.simprints.intentlauncher.ui.events.model.EventDialogType
import com.simprints.intentlauncher.ui.events.model.EventSortingOption
import com.simprints.intentlauncher.ui.intent.model.ResponseEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ResponseEventsScreen(
    navController: NavHostController,
    intentId: String,
) {
    val window = LocalActivity.current?.window
    val clipboardManager = LocalClipboard.current
    val vm =
        hiltViewModel<ResponseEventsScreenViewModel, ResponseEventsScreenViewModel.Factory> { factory ->
            factory.create(
                intentId = intentId,
                initialSoftInputMode = window?.attributes?.softInputMode?.takeUnless { it == 0 }
                    ?: WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN,
            )
        }
    val viewState by vm.state.collectAsStateLifecycleAware()
    LaunchedEffect(vm) { vm.onAction(ResponseEventsUiAction.LoadEvents) }

    val lifecycleAwareEffects = rememberFlow(vm.effects)
    val toastTextState = remember { mutableStateOf("") }
    ToastLauncher(toastTextState)
    LaunchedEffect(lifecycleAwareEffects, clipboardManager) {
        lifecycleAwareEffects.collectLatest { effect ->
            when (effect) {
                is ResponseEventsUiEffect.CopyToClipboard -> {
                    clipboardManager.nativeClipboard.setPrimaryClip(
                        ClipData.newPlainText(effect.label, effect.value),
                    )
                }

                is ResponseEventsUiEffect.ShowToast -> {
                    toastTextState.value = effect.message
                }
            }
        }
    }

    val scope = rememberCoroutineScope()
    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmValueChange = { it != ModalBottomSheetValue.HalfExpanded },
        skipHalfExpanded = true,
    )
    if (modalSheetState.currentValue == ModalBottomSheetValue.Hidden && viewState.activeDialog != null) {
        vm.onAction(ResponseEventsUiAction.DialogDismissed)
    }

    // Setting SoftInputMode to SOFT_INPUT_ADJUST_RESIZE so that content won't shift away from the
    // screen when the keyboard is displayed. Setting it back to the initial value once the widget
    // is no longer visible
    window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    DisposableEffect(Unit) {
        onDispose {
            window?.setSoftInputMode(vm.initialSoftInputMode)
        }
    }
    ModalBottomSheetLayout(
        sheetState = modalSheetState,
        sheetShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        sheetContent = {
            when (val dialogType = viewState.activeDialog) {
                null -> Unit

                is EventDialogType.SortingOptions -> SortingOptionsDialogWrapper(
                    viewState = viewState,
                    modalSheetState = modalSheetState,
                    scope = scope,
                    sortingOptions = dialogType.sortingOptions,
                    onOptionSelected = { vm.onAction(ResponseEventsUiAction.SortingChanged(it)) },
                )

                is EventDialogType.EventOptions -> EventActionsDialogWrapper(
                    event = dialogType.event,
                    modalSheetState = modalSheetState,
                    scope = scope,
                    onCopyId = { vm.onAction(ResponseEventsUiAction.CopyIdClicked(it)) },
                    onCopyEventJson = { vm.onAction(ResponseEventsUiAction.CopyEventJsonClicked(it)) },
                    onCopyPayloadJson = { vm.onAction(ResponseEventsUiAction.CopyPayloadJsonClicked(it)) },
                )
            }
        },
        sheetBackgroundColor = MaterialTheme.colors.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            val eventCountText = when {
                viewState.events.size == viewState.totalEvents -> "${viewState.events.size}"
                else -> "${viewState.events.size}/${viewState.totalEvents}"
            }
            TopAppBar(
                windowInsets = WindowInsets.statusBars,
                title = { Text("Response Events ($eventCountText) ") },
                navigationIcon = { NavigateUpButton(navController) },
                actions = {
                    IconButton(
                        onClick = {
                            vm.onAction(ResponseEventsUiAction.SortOptionsClicked)
                            scope.launch { modalSheetState.show() }
                        },
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = "Sort options")
                    }
                },
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxSize(),
                ) {
                    items(viewState.events.size) { i ->
                        val event = viewState.events[i]
                        val modifier = when (i) {
                            0 -> Modifier.padding(top = 8.dp)
                            viewState.events.size - 1 -> Modifier.padding(bottom = 8.dp)
                            else -> Modifier
                        }
                        ResponseEventItem(
                            modifier = modifier.padding(horizontal = 8.dp),
                            event = event,
                            position = i,
                        ) {
                            vm.onAction(ResponseEventsUiAction.EventOptionsClicked(it))
                            scope.launch { modalSheetState.show() }
                        }
                    }
                }
            }
            Box(
                modifier = Modifier
                    .background(Color.White)
                    .padding(bottom = 8.dp, start = 8.dp, end = 8.dp)
                    .fillMaxWidth(),
            ) {
                PropertyField(
                    "Search",
                    viewState.query,
                    LocalFocusManager.current,
                    testTag = "search",
                    onChange = {
                        vm.onAction(ResponseEventsUiAction.SearchQueryChanged(it))
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EventActionsDialogWrapper(
    event: ResponseEvent,
    modalSheetState: ModalBottomSheetState,
    scope: CoroutineScope,
    onCopyId: (String) -> Unit,
    onCopyEventJson: (ResponseEvent) -> Unit,
    onCopyPayloadJson: (ResponseEvent) -> Unit,
) {
    BackHandler(enabled = modalSheetState.isVisible) {
        scope.launch { modalSheetState.hide() }
    }
    EventActionsDialog(
        event = event,
        onCopyId = { id ->
            onCopyId(id)
            scope.launch { modalSheetState.hide() }
        },
        onCopyEventJson = {
            onCopyEventJson(it)
            scope.launch { modalSheetState.hide() }
        },
        onCopyPayloadJson = {
            onCopyPayloadJson(it)
            scope.launch { modalSheetState.hide() }
        },
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SortingOptionsDialogWrapper(
    viewState: ResponseEventsUiState,
    modalSheetState: ModalBottomSheetState,
    scope: CoroutineScope,
    sortingOptions: List<EventSortingOption>,
    onOptionSelected: (EventSortingOption) -> Unit,
) {
    BackHandler(enabled = modalSheetState.isVisible) {
        scope.launch { modalSheetState.hide() }
    }
    SortingOptionsDialog(
        current = viewState.sorting,
        options = sortingOptions,
        onOptionSelected = { sortingOption ->
            onOptionSelected(sortingOption)
            scope.launch { modalSheetState.hide() }
        },
    )
}
