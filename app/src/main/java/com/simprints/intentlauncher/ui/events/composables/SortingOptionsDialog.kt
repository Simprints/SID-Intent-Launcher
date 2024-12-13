package com.simprints.intentlauncher.ui.events.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.simprints.intentlauncher.ui.events.model.EventSortingItem
import com.simprints.intentlauncher.ui.events.model.EventSortingOption
import com.simprints.intentlauncher.ui.theme.AppColorScheme

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
                    icon = Icons.Filled.AccessTime,
                )

                EventSortingOption.AsReceivedInResponse -> EventSortingItem(
                    text = "As in response",
                    secondaryText = "In the same order received from the SID",
                    icon = Icons.Filled.SystemUpdate,
                )
            }
            ListItem(
                modifier = Modifier
                    .clickable { onOptionSelected(option) }
                    .background(background),
                text = { Text(optionItem.text) },
                secondaryText = { Text(optionItem.secondaryText) },
                icon = {
                    Icon(
                        optionItem.icon,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                    )
                },
            )
        }
    }
}

@Preview
@Composable
fun SortingOptionsDialogPreview() {
    Box(
        modifier = Modifier.background(MaterialTheme.colors.surface),
    ) {
        SortingOptionsDialog(
            current = EventSortingOption.entries.first(),
            options = EventSortingOption.entries,
            onOptionSelected = {},
        )
    }
}
