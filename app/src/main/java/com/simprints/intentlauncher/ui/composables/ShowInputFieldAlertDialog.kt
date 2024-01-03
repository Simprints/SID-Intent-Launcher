package com.simprints.intentlauncher.ui.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ShowInputFieldAlertDialog(
    openDialog: MutableState<Boolean> = remember { mutableStateOf(false) },
    title: @Composable (() -> Unit)? = null,
    text: @Composable (() -> Unit)? = null,
    label: @Composable (() -> Unit)? = null,
    confirmButtonText: @Composable RowScope.() -> Unit,
    onConfirm: (String) -> Unit,
) {
    val textFieldValue = remember { mutableStateOf(TextFieldValue()) }

    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = { openDialog.value = false },
            title = title,
            text = {
                Column {
                    text?.invoke()
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = textFieldValue.value,
                        onValueChange = { newValue -> textFieldValue.value = newValue },
                        label = label
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirm(textFieldValue.value.text.trim())
                        openDialog.value = false
                    },
                    content = confirmButtonText,
                )
            },
            dismissButton = {
                TextButton(onClick = { openDialog.value = false }) {
                    Text("Dismiss")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ShowBasicAlertDialogPreview() {
    ShowInputFieldAlertDialog(
        title = { Text("Title") },
        text = { Text("Text") },
        confirmButtonText = { Text("Confirm") },
        onConfirm = {},
    )
}
