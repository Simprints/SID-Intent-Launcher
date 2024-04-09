package com.simprints.intentlauncher.ui.intent.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.simprints.intentlauncher.ui.composables.AccordionLayout
import com.simprints.intentlauncher.ui.composables.PropertyField
import com.simprints.intentlauncher.ui.intent.IntentViewState

@Composable
fun FollowUpFlowForm(
    state: IntentViewState,
    focusManager: FocusManager = LocalFocusManager.current,
    defaultExpanded: Boolean = false,
    onSessionIdChanged: (String) -> Unit = {},
    onConfirm: () -> Unit = {},
    onEnrolLast: () -> Unit = {},
) {
    AccordionLayout(
        title = "Follow up flow intent",
        defaultExpanded = defaultExpanded,
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .padding(bottom = 8.dp, start = 8.dp, end = 8.dp)
                .background(Color.White),
        ) {
            PropertyField(
                "Session ID",
                state.sessionId,
                focusManager,
                onSessionIdChanged,
                modifier = Modifier.fillMaxWidth(),
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Button(
                    onClick = onConfirm,
                    modifier = Modifier
                        .weight(1f),
                ) { Text("Confirm identity") }
                Button(
                    onClick = onEnrolLast,
                    modifier = Modifier
                        .weight(1f),
                ) { Text("Enrol last") }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FollowUpFlowFormPreview() {
    FollowUpFlowForm(
        IntentViewState(),
        defaultExpanded = true,
    )
}
