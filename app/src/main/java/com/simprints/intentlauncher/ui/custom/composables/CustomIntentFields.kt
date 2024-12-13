package com.simprints.intentlauncher.ui.custom.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.simprints.intentlauncher.ui.composables.AccordionLayout
import com.simprints.intentlauncher.ui.composables.PropertyField
import com.simprints.intentlauncher.ui.custom.CustomIntentViewState
import com.simprints.intentlauncher.ui.theme.AppTypography

@Composable
fun CustomIntentFields(
    state: CustomIntentViewState,
    focusManager: FocusManager = LocalFocusManager.current,
    onActionChange: (String) -> Unit = {},
    onExtrasChange: (String) -> Unit = {},
    onLaunch: () -> Unit = {},
) {
    AccordionLayout(
        title = "Intent",
        defaultExpanded = true,
    ) {
        Column {
            PropertyField(
                "Action",
                state.action,
                focusManager,
                onActionChange,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = state.extras,
                onValueChange = onExtrasChange,
                label = { Text("Extras") },
                minLines = 5,
                maxLines = 5,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default),
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth()
                    .onKeyEvent {
                        if (it.key.keyCode == Key.Tab.keyCode) {
                            focusManager.moveFocus(FocusDirection.Next)
                            true // true -> consumed
                        } else {
                            false
                        }
                    },
            )
            Text(
                text = "One per line separated by '=' without quotes (e.g. key=value)",
                style = AppTypography.caption,
                modifier = Modifier.padding(bottom = 12.dp, start = 8.dp, end = 8.dp),
            )
            Button(
                onClick = onLaunch,
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth(),
            ) { Text("Launch") }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CustomIntentFieldsPreview() {
    CustomIntentFields(
        state = CustomIntentViewState(
            action = "com.intent.action",
            extras = "key=value\nkey2=value2",
        ),
    )
}
