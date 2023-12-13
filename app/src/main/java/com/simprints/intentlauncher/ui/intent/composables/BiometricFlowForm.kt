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
import com.simprints.intentlauncher.compose.PropertyField
import com.simprints.intentlauncher.ui.composables.AccordionLayout
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
                .fillMaxWidth(),
        ) {
            PropertyField(
                "GUID (for verification and confirmation)",
                state.guid,
                focusManager,
                onGuidChange,
                modifier = Modifier.fillMaxWidth(),
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                Button(
                    onClick = onEnroll,
                    modifier = Modifier
                        .padding(4.dp)
                        .weight(1f),
                ) { Text("Enroll") }
                Button(
                    onClick = onIdentify,
                    modifier = Modifier
                        .padding(4.dp)
                        .weight(1f),
                ) { Text("Identify") }
                Button(
                    onClick = onVerify,
                    modifier = Modifier
                        .padding(4.dp)
                        .weight(1f),
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