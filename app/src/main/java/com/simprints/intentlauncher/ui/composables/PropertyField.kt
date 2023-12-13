@file:OptIn(ExperimentalComposeUiApi::class)

package com.simprints.intentlauncher.ui.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
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


@Composable
fun PropertyField(
    label: String,
    value: String,
    focusManager: FocusManager,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Next) }
        ),
        modifier = modifier
            .padding(8.dp, 4.dp)
            .onKeyEvent {
                if (it.key.keyCode == Key.Tab.keyCode) {
                    focusManager.moveFocus(FocusDirection.Next)
                    true //true -> consumed
                } else false
            },
    )
}

@Preview(showBackground = true)
@Composable
private fun PropertyFieldPreview() {
    Column {
        PropertyField(
            label = "Label",
            value = "",
            focusManager = LocalFocusManager.current,
            onChange = {}
        )
        Spacer(modifier = Modifier.padding(8.dp))
        PropertyField(
            label = "Label",
            value = "value",
            focusManager = LocalFocusManager.current,
            onChange = {}
        )
    }
}