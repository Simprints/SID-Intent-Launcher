package com.simprints.intentlauncher.ui.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.simprints.intentlauncher.ui.theme.AppTypography

@Composable
fun NothingToDisplay(text: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(8.dp),
    ) {
        Text(
            text = text,
            style = AppTypography.body1,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .align(Alignment.CenterHorizontally),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NothingToDisplayPreview() {
    NothingToDisplay(
        text = "There is no content to display at the moment.",
    )
}
