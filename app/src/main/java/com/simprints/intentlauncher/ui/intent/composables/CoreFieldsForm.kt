package com.simprints.intentlauncher.ui.intent.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.simprints.intentlauncher.ui.composables.PropertyField
import com.simprints.intentlauncher.ui.intent.IntentViewState

@Composable
fun CoreFieldsForm(
    state: IntentViewState,
    focusManager: FocusManager = LocalFocusManager.current,
    onProjectIdChange: (String) -> Unit = {},
    onUserIdChange: (String) -> Unit = {},
    onModuleIdChange: (String) -> Unit = {},
    onMetadataChange: (String) -> Unit = {},
) {
    Column {
        PropertyField(
            "Project ID",
            state.projectId,
            focusManager,
            onProjectIdChange,
            modifier = Modifier.fillMaxWidth(),
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PropertyField(
                "User ID",
                state.userId,
                focusManager,
                onUserIdChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            )
            PropertyField(
                "Module ID",
                state.moduleId,
                focusManager,
                onModuleIdChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            )
        }
        PropertyField(
            "Metadata",
            state.metadata,
            focusManager,
            onMetadataChange,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CoreFieldsFormPreview() {
    CoreFieldsForm(IntentViewState())
}
