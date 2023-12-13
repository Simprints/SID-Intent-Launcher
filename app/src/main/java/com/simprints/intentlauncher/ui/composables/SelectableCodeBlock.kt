package com.simprints.intentlauncher.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.simprints.intentlauncher.ui.theme.AppShapes
import com.simprints.intentlauncher.ui.theme.AppTypography

@Composable
fun SelectableCodeBlock(text: String) {
    SelectionContainer {
        Text(
            text = text,
            style = AppTypography.body2,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray.copy(alpha = 0.3f), shape = AppShapes.large)
                .padding(8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SelectableCodeBlockPreview() {
    Column(
        modifier = Modifier.padding(8.dp)
    ) {
        SelectableCodeBlock(text = "Text")
        Spacer(modifier = Modifier.padding(8.dp))
        SelectableCodeBlock(
            text = """
            Lorem ipsum dolor sit amet, consectetur adipiscing elit. 
            Donec euismod, nisl eget aliquam ultricies, 
            nisl nisl aliquet nisl, nec aliquam nisl nisl nec.
            """.trimIndent()
        )
    }
}
