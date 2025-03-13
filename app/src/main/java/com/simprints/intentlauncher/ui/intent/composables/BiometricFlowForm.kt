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
import com.simprints.intentlauncher.tools.testAccessibleTag
import com.simprints.intentlauncher.ui.composables.AccordionLayout
import com.simprints.intentlauncher.ui.composables.PropertyField
import com.simprints.intentlauncher.ui.intent.IntentViewState

@Composable
fun BiometricFlowForm(
    state: IntentViewState,
    focusManager: FocusManager = LocalFocusManager.current,
    defaultExpanded: Boolean = false,
    onGuidChange: (String) -> Unit = {},
    onEnroll: () -> Unit = {},
    onIdentify: () -> Unit = {},
    onVerify: () -> Unit = {},
) {
    AccordionLayout(
        title = "Biometric flow intent",
        defaultExpanded = defaultExpanded,
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .background(Color.White)
                .padding(bottom = 8.dp, start = 8.dp, end = 8.dp)
                .fillMaxWidth(),
        ) {
            PropertyField(
                "GUID (for verification and confirmation)",
                state.guid,
                focusManager,
                testTag = "followupGuid",
                onChange = onGuidChange,
                modifier = Modifier.fillMaxWidth(),
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                Button(
                    onClick = onEnroll,
                    modifier = Modifier
                        .weight(1f)
                        .testAccessibleTag("enrollButton"),
                ) { Text("Enroll") }
                Button(
                    onClick = onIdentify,
                    modifier = Modifier
                        .weight(1f)
                        .testAccessibleTag("identifyButton"),
                ) { Text("Identify") }
                Button(
                    onClick = onVerify,
                    modifier = Modifier
                        .weight(1f)
                        .testAccessibleTag("verifyButton"),
                ) { Text("Verify") }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BiometricFlowFormPreview() {
    BiometricFlowForm(
        state = IntentViewState(),
        defaultExpanded = true,
    )
}
