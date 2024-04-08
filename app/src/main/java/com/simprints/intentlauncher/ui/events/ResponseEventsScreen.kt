package com.simprints.intentlauncher.ui.events

import android.app.Activity
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.DataObject
import androidx.compose.material.icons.rounded.PostAdd
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.simprints.intentlauncher.tools.collectAsStateLifecycleAware
import com.simprints.intentlauncher.ui.composables.AccordionLayout
import com.simprints.intentlauncher.ui.composables.NavigateUpButton
import com.simprints.intentlauncher.ui.composables.PropertyField
import com.simprints.intentlauncher.ui.composables.ToastLauncher
import com.simprints.intentlauncher.ui.events.model.EventDialogType
import com.simprints.intentlauncher.ui.events.model.EventSortingItem
import com.simprints.intentlauncher.ui.events.model.EventSortingOption
import com.simprints.intentlauncher.ui.intent.model.ResponseEvent
import com.simprints.intentlauncher.ui.theme.AppColorScheme
import com.simprints.intentlauncher.ui.theme.AppTypography
import kotlinx.coroutines.launch
import java.util.Date

private val eventMock = ResponseEvent(
    id = "738299cd2-389f-a9ce-3d11a3948ce",
    type = "MOCKED_EVENT",
    scopeId = "scope id",
    projectId = "project id",
    createdAtMs = 0,
    payload = mapOf(
        "key 1" to "value 1",
        "key 2" to listOf("list value one", "list value two"),
        "key 3" to mapOf(
            "nested key 1" to "nested value 1",
            "nested key 2" to mapOf(
                "second nested key 1" to "second nested value 1 with very long sentence",
                "second nested key 2" to "second nested value 2",
            )
        ),
        "key 4" to "value 4"
    )
)

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ResponseEventsScreen(
    navController: NavHostController,
    intentId: String
) {
    val window = (LocalContext.current as? Activity)?.window
    val clipboardManager = LocalClipboardManager.current
    val vm =
        hiltViewModel<ResponseEventsScreenViewModel, ResponseEventsScreenViewModel.Factory> { factory ->
            factory.create(
                intentId = intentId,
                initialSoftInputMode = window?.attributes?.softInputMode?.takeUnless { it == 0 }
                    ?: WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
            )
        }
    val viewState by vm.state.collectAsStateLifecycleAware(initial = vm.initialViewState)
    // What kind of dialog is displayed
    var eventDialogType by remember { mutableStateOf<EventDialogType?>(null) }
    val scope = rememberCoroutineScope()
    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmStateChange = { it != ModalBottomSheetValue.HalfExpanded },
        skipHalfExpanded = true,
    )
    val toastText = remember { mutableStateOf("") }
    ToastLauncher(toastText)
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
                is EventDialogType.SortingOptions -> {
                    BackHandler(enabled = modalSheetState.isVisible) {
                        scope.launch { modalSheetState.hide() }
                    }
                    SortingOptionsDialog(
                        current = viewState.sorting,
                        options = dialogType.sortingOptions,
                        onOptionSelected = { sortingOption ->
                            vm.setSorting(sortingOption)
                            scope.launch { modalSheetState.hide() }
                        }
                    )
                }

                is EventDialogType.EventOptions -> {
                    BackHandler(enabled = modalSheetState.isVisible) {
                        scope.launch { modalSheetState.hide() }
                    }
                    EventActionsDialog(event = dialogType.event,
                        onCopyId = { id ->
                            clipboardManager.setText(AnnotatedString(id))
                            toastText.value = "$id copied to clipboard"
                            scope.launch { modalSheetState.hide() }
                        },
                        onCopyEventJson = {
                            val json = vm.serializeEventToJson(it)
                            if (json == null) {
                                toastText.value = "Error parsing event to JSON"
                            } else {
                                clipboardManager.setText(AnnotatedString(json))
                                toastText.value = "Event JSON copied to clipboard"
                            }
                            scope.launch { modalSheetState.hide() }
                        },
                        onCopyPayloadJson = {
                            val json = vm.serializePayloadToJson(it)
                            if (json == null) {
                                toastText.value = "Error parsing payload to JSON"
                            } else {
                                clipboardManager.setText(AnnotatedString(json))
                                toastText.value = "Paylaod JSON copied to clipboard"
                            }
                            scope.launch { modalSheetState.hide() }
                        }
                    )
                }
            }
        },
        sheetBackgroundColor = MaterialTheme.colors.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            val eventCountText = when {
                viewState.events.size == viewState.totalEvents -> "${viewState.events.size}"
                else -> "${viewState.events.size}/${viewState.totalEvents}"
            }
            TopAppBar(
                title = { Text("Response Events ($eventCountText) ") },
                navigationIcon = { NavigateUpButton(navController) },
                actions = {
                    IconButton(
                        onClick = {
                            eventDialogType =
                                EventDialogType.SortingOptions(sortingOptions = EventSortingOption.entries)
                            scope.launch { modalSheetState.show() }
                        }) {
                        Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = "Sort options")
                    }
                }
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    items(viewState.events.size) { i ->
                        val event = viewState.events[i]
                        val modifier = when (i) {
                            0 -> Modifier.padding(top = 8.dp)
                            viewState.events.size - 1 -> Modifier.padding(bottom = 8.dp)
                            else -> Modifier
                        }
                        ResponseEventItem(modifier = modifier.padding(horizontal = 8.dp), event = event, position = i) {
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
                    .fillMaxWidth()
            ) {
                PropertyField(
                    "Search",
                    viewState.query,
                    LocalFocusManager.current,
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
fun SortingOptionsDialog(
    current: EventSortingOption,
    options: List<EventSortingOption>,
    onOptionSelected: (EventSortingOption) -> Unit,
) {
    Column {
        ListItem(
            text = { Text("Sorting options") },
        )
        options.map { option ->
            val background = if (option == current) {
                AppColorScheme.primary.copy(alpha = 0.1f)
            } else {
                MaterialTheme.colors.background
            }
            val optionItem = when (option) {
                EventSortingOption.DateAsc -> EventSortingItem(
                    text = "By creation date",
                    secondaryText = "Ascending",
                    icon = Icons.Filled.AccessTime
                )

                EventSortingOption.AsReceivedInResponse -> EventSortingItem(
                    text = "As in response",
                    secondaryText = "In the same order received from the SID",
                    icon = Icons.Filled.SystemUpdate
                )
            }
            ListItem(
                modifier = Modifier.clickable { onOptionSelected(option) }.background(background),
                text = { Text(optionItem.text) },
                secondaryText = { Text(optionItem.secondaryText) },
                icon = {
                    Icon(
                        optionItem.icon,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp)
                    )
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EventActionsDialog(
    event: ResponseEvent,
    onCopyId: (String) -> Unit,
    onCopyEventJson: (ResponseEvent) -> Unit,
    onCopyPayloadJson: (ResponseEvent) -> Unit,
) {
    val iconModifier = Modifier.size(40.dp)
    Column {
        ListItem(
            text = { Text(event.type) },
            secondaryText = { Text("id: ${event.id}") },
        )
        Divider()
        ListItem(
            modifier = Modifier.clickable { onCopyId(event.id) },
            text = { Text("Copy ID") },
            secondaryText = { Text(event.id) },
            icon = {
                Icon(
                    Icons.Rounded.ContentCopy,
                    contentDescription = "Copy ID",
                    modifier = iconModifier
                )
            }
        )
        ListItem(
            modifier = Modifier.clickable { onCopyEventJson(event) },
            text = { Text("Copy full Event JSON") },
            icon = {
                Icon(
                    Icons.Rounded.DataObject,
                    contentDescription = "Copy full Event JSON",
                    modifier = iconModifier
                )
            }
        )
        ListItem(
            modifier = Modifier.clickable { onCopyPayloadJson(event) },
            text = { Text("Copy payload JSON") },
            icon = {
                Icon(
                    Icons.Rounded.PostAdd,
                    contentDescription = "Copy payload JSON",
                    modifier = iconModifier
                )
            }
        )
    }
}

@Composable
fun ResponseEventItem(
    event: ResponseEvent,
    position: Int,
    modifier: Modifier = Modifier,
    onOptionsClick: (ResponseEvent) -> Unit
) {
    val captionStyle = AppTypography.caption.copy(color = Color.Black.copy(alpha = 0.7f))
    Card(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "$position. ${event.type}",
                    style = AppTypography.subtitle1,
                )
                Surface(
                    modifier = Modifier
                        .size(24.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple(bounded = false),
                            onClick = { onOptionsClick(event) }
                        ),
                    shape = CircleShape,
                    color = Color.Transparent,
                    contentColor = MaterialTheme.colors.primary.copy(alpha = ContentAlpha.high)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Filled.MoreVert,
                            contentDescription = "Event actions",
                            tint = AppColorScheme.onBackground
                        )
                    }
                }
            }
            Divider(
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                "id: ${event.id}",
                style = captionStyle
            )
            Text(
                "Created at: ${Date(event.createdAtMs)}",
                style = captionStyle
            )
            Text(
                "Project id: ${event.projectId}",
                style = captionStyle
            )
            Text(
                "Scope id: ${event.scopeId}",
                style = captionStyle
            )
            AccordionLayout(
                title = "payload",
                defaultExpanded = false,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(8.dp, end = 8.dp, bottom = 8.dp)
                ) {
                    PayloadEntry(event.payload, style = captionStyle)
                }
            }
        }
    }
}

@Composable
private fun PayloadEntry(
    payloadMap: Map<String, Any>,
    paddingStart: Dp = 0.dp,
    style: TextStyle
) {
    val paddingStep = 4.dp
    for ((key, value) in payloadMap) {
        if (value is Map<*, *>) {
            Text("$key: {", style = style, modifier = Modifier.padding(start = paddingStart))
            PayloadEntry(
                payloadMap = value as Map<String, Any>,
                paddingStart = paddingStart + paddingStep,
                style = style
            )
            Text(
                "}",
                style = style,
                modifier = Modifier.padding(start = paddingStart)
            )
        } else {
            Text(
                "$key: $value",
                style = style,
                modifier = Modifier.padding(start = paddingStart)
            )
        }
    }
}

@Preview
@Composable
fun ResponseEventItemPreview() {
    ResponseEventItem(
        event = eventMock,
        position = 1,
        onOptionsClick = {}
    )
}

@Preview
@Composable
fun SortingOptionsDialogPreview() {
    Box(
        modifier = Modifier.background(MaterialTheme.colors.surface)
    ) {
        SortingOptionsDialog(
            current = EventSortingOption.entries.first(),
            options = EventSortingOption.entries,
            onOptionSelected = {}
        )
    }
}

@Preview
@Composable
fun EventActionsDialogPreview() {
    Box(
        modifier = Modifier.background(MaterialTheme.colors.surface)
    ) {
        EventActionsDialog(
            event = eventMock,
            onCopyId = {},
            onCopyEventJson = {},
            onCopyPayloadJson = {},
        )
    }
}