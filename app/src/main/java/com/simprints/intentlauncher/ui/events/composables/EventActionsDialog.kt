package com.simprints.intentlauncher.ui.events.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.DataObject
import androidx.compose.material.icons.rounded.PostAdd
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.simprints.intentlauncher.ui.events.model.eventMock
import com.simprints.intentlauncher.ui.intent.model.ResponseEvent

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
                    modifier = iconModifier,
                )
            },
        )
        ListItem(
            modifier = Modifier.clickable { onCopyEventJson(event) },
            text = { Text("Copy full Event JSON") },
            icon = {
                Icon(
                    Icons.Rounded.DataObject,
                    contentDescription = "Copy full Event JSON",
                    modifier = iconModifier,
                )
            },
        )
        ListItem(
            modifier = Modifier.clickable { onCopyPayloadJson(event) },
            text = { Text("Copy payload JSON") },
            icon = {
                Icon(
                    Icons.Rounded.PostAdd,
                    contentDescription = "Copy payload JSON",
                    modifier = iconModifier,
                )
            },
        )
    }
}

@Preview
@Composable
fun EventActionsDialogPreview() {
    Box(
        modifier = Modifier.background(MaterialTheme.colors.surface),
    ) {
        EventActionsDialog(
            event = eventMock,
            onCopyId = {},
            onCopyEventJson = {},
            onCopyPayloadJson = {},
        )
    }
}
