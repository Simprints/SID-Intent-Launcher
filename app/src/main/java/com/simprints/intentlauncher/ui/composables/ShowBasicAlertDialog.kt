package com.simprints.intentlauncher.ui.composables

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ShowBasicAlertDialog(
    openDialog: MutableState<Boolean> = remember { mutableStateOf(false) },
    title: @Composable (() -> Unit)? = null,
    text: @Composable (() -> Unit)? = null,
    confirmButtonText: @Composable RowScope.() -> Unit,
    onConfirm: () -> Unit,
) {
    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = { openDialog.value = false },
            title = title,
            text = text,
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirm()
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
    ShowBasicAlertDialog(
        title = { Text("Title") },
        text = { Text("Text") },
        confirmButtonText = { Text("Confirm") },
        onConfirm = {},
    )
}
