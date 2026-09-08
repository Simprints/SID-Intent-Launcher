package com.simprints.intentlauncher.ui.composables

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ShowErrorAlert(
    openDialog: Boolean = false,
    title: @Composable (() -> Unit)? = null,
    text: @Composable (() -> Unit)? = null,
    confirmButtonText: @Composable RowScope.() -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    if (openDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = title,
            text = text,
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirm()
                        onDismiss()
                    },
                    content = confirmButtonText,
                )
            },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ShowErrorAlertPreview() {
    ShowErrorAlert(
        title = { Text("Title") },
        text = { Text("Text") },
        confirmButtonText = { Text("Confirm") },
        onConfirm = {},
        onDismiss = {},
    )
}
