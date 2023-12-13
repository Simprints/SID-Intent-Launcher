package com.simprints.intentlauncher.fragments.result.compose

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.simprints.intentlauncher.ui.composables.NothingToDisplay
import com.simprints.intentlauncher.fragments.result.ResultListViewModel

@Composable
fun ResultListScreen(
    resultListViewModel: ResultListViewModel,
) {
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    val context: Context = LocalContext.current

    val resultList by resultListViewModel.getResults().observeAsState()

    resultList?.let { results ->
        if (results.isEmpty()) {
            NothingToDisplay(
                text = "There is no content to display at the moment. Sophisticated Salmom says play around with the app for a bit and your fish will appear here. Happy fishing!",
            )
        } else {
            LazyColumn {
                items(items = results) { item ->
                    ResultItem(
                        resultInfo = item,
                        onCopyToClipboard = {
                            putContentToClipboard(it, context, clipboardManager)
                        },
                        modifier = Modifier.padding(all = 12.dp)
                    )
                }
            }
        }
    }
}

private fun putContentToClipboard(
    content: String,
    context: Context,
    clipboardManager: ClipboardManager
) {
    clipboardManager.setText(AnnotatedString(content))
    Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
}
