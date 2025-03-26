package com.simprints.intentlauncher.ui.events

import android.app.Activity
import android.view.WindowManager
import androidx.activity.compose.BackHandler
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.simprints.intentlauncher.tools.collectAsStateLifecycleAware
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ResponseEventsScreen(
    navController: NavHostController,
    intentId: String,
) {
    val window = (LocalContext.current as? Activity)?.window
    val clipboardManager = LocalClipboardManager.current
    val vm =
        hiltViewModel<ResponseEventsScreenViewModel, ResponseEventsScreenViewModel.Factory> { factory ->
            factory.create(
                intentId = intentId,
                initialSoftInputMode = window?.attributes?.softInputMode?.takeUnless { it == 0 }
                    ?: WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN,
            )
        }
    val viewState by vm.state.collectAsStateLifecycleAware(initial = vm.initialViewState)
    // What kind of dialog is displayed
    var eventDialogType by remember { mutableStateOf<EventDialogType?>(null) }
    val scope = rememberCoroutineScope()
    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmValueChange = { it != ModalBottomSheetValue.HalfExpanded },
        skipHalfExpanded = true,
    )
    val toastTextState = remember { mutableStateOf("") }
    ToastLauncher(toastTextState)
    if (modalSheetState.currentValue == ModalBottomSheetValue.Hidden) {
        eventDialogType = null
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
            when (val dialogType = eventDialogType) {
                null -> Unit
                is EventDialogType.SortingOptions -> SortingOptionsDialogWrapper(
                    viewState = viewState,
                    modalSheetState = modalSheetState,
                    scope = scope,
                    viewModel = vm,
                    sortingOptions = dialogType.sortingOptions,
                )

                is EventDialogType.EventOptions -> EventActionsDialogWrapper(
                    event = dialogType.event,
                    modalSheetState = modalSheetState,
                    scope = scope,
                    viewModel = vm,
                    clipboardManager = clipboardManager,
                    toastTextState = toastTextState,
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
                            eventDialogType =
                                EventDialogType.SortingOptions(sortingOptions = EventSortingOption.entries)
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
                            eventDialogType = EventDialogType.EventOptions(event = it)
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
                        vm.setSearchQuery(it)
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
    clipboardManager: ClipboardManager,
    toastTextState: MutableState<String>,
    modalSheetState: ModalBottomSheetState,
    scope: CoroutineScope,
    viewModel: ResponseEventsScreenViewModel,
) {
    BackHandler(enabled = modalSheetState.isVisible) {
        scope.launch { modalSheetState.hide() }
    }
    EventActionsDialog(
        event = event,
        onCopyId = { id ->
            clipboardManager.setText(AnnotatedString(id))
            toastTextState.value = "$id copied to clipboard"
            scope.launch { modalSheetState.hide() }
        },
        onCopyEventJson = {
            val json = viewModel.serializeEventToJson(it)
            if (json == null) {
                toastTextState.value = "Error parsing event to JSON"
            } else {
                clipboardManager.setText(AnnotatedString(json))
                toastTextState.value = "Event JSON copied to clipboard"
            }
            scope.launch { modalSheetState.hide() }
        },
        onCopyPayloadJson = {
            val json = viewModel.serializePayloadToJson(it)
            if (json == null) {
                toastTextState.value = "Error parsing payload to JSON"
            } else {
                clipboardManager.setText(AnnotatedString(json))
                toastTextState.value = "Paylaod JSON copied to clipboard"
            }
            scope.launch { modalSheetState.hide() }
        },
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SortingOptionsDialogWrapper(
    viewState: ResponseEventsViewState,
    modalSheetState: ModalBottomSheetState,
    scope: CoroutineScope,
    viewModel: ResponseEventsScreenViewModel,
    sortingOptions: List<EventSortingOption>,
) {
    BackHandler(enabled = modalSheetState.isVisible) {
        scope.launch { modalSheetState.hide() }
    }
    SortingOptionsDialog(
        current = viewState.sorting,
        options = sortingOptions,
        onOptionSelected = { sortingOption ->
            viewModel.setSorting(sortingOption)
            scope.launch { modalSheetState.hide() }
        },
    )
}
