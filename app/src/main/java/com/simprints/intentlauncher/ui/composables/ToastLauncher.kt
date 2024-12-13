package com.simprints.intentlauncher.ui.composables

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.platform.LocalContext

@Composable
fun ToastLauncher(toastText: MutableState<String>) {
    val context = LocalContext.current

    // This will be triggered when showToast.value becomes true
    LaunchedEffect(key1 = toastText.value) {
        if (toastText.value.isNotEmpty()) {
            Toast.makeText(context, toastText.value, Toast.LENGTH_SHORT).show()
            toastText.value = "" // Reset the state after showing the toast
        }
    }
}
