package com.simprints.intentlauncher.ui.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.simprints.intentlauncher.ui.theme.AppTypography
import java.util.Locale

@Composable
fun DividerWithTitle(title: String) {
    Row(
        modifier = Modifier.padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title.uppercase(Locale.ROOT),
            style = AppTypography.caption,
        )
        Spacer(modifier = Modifier.padding(horizontal = 4.dp))
        Divider()
    }
}

@Preview(showBackground = true, widthDp = 320)
@Composable
private fun DividerWithTitlePreview() {
    DividerWithTitle(title = "Title")
}
