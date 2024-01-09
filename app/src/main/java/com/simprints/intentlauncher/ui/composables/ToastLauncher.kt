package com.simprints.intentlauncher.ui.composables

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.platform.LocalContext

@Composable
fun ToastLauncher(
    showToast: MutableState<Boolean>,
    text: String
) {
    val context = LocalContext.current

    // This will be triggered when showToast.value becomes true
    LaunchedEffect(key1 = showToast.value) {
        if (showToast.value) {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
            showToast.value = false // Reset the state after showing the toast
        }
    }
}
