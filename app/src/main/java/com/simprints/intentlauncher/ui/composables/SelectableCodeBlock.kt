package com.simprints.intentlauncher.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.simprints.intentlauncher.tools.testAccessibleTag
import com.simprints.intentlauncher.ui.theme.AppShapes
import com.simprints.intentlauncher.ui.theme.AppTypography

@Composable
fun SelectableCodeBlock(text: String) {
    val clipboardManager = LocalClipboardManager.current
    val annotatedText = textWithAnnotatedGuids(text)

    val toastText = remember { mutableStateOf("") }
    ToastLauncher(toastText)

    SelectionContainer {
        ClickableText(
            text = annotatedText,
            style = AppTypography.body2.copy(
                fontFamily = FontFamily.Monospace,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray.copy(alpha = 0.3f), shape = AppShapes.large)
                .padding(8.dp)
                .testAccessibleTag("result"),
            onClick = { offset ->
                annotatedText
                    .getStringAnnotations(tag = "Clickable", start = offset, end = offset)
                    .firstOrNull()
                    ?.let { annotation ->
                        clipboardManager.setText(AnnotatedString(annotation.item))
                        toastText.value = "${annotation.item} copied to clipboard"
                    }
            },
        )
    }
}

private val regex = Regex("[a-f0-9]{8}(-[a-f0-9]{4}){3}-[a-f0-9]{12}")
private val highlightSpan = SpanStyle(background = Color.LightGray)

@Stable
private fun textWithAnnotatedGuids(text: String) = buildAnnotatedString {
    append(text)
    regex.findAll(text).forEach { result ->
        val start = result.range.first
        val end = result.range.last + 1

        // Add background
        addStyle(highlightSpan, start, end)
        // Add click handling
        addStringAnnotation(
            tag = "Clickable",
            annotation = result.value,
            start = start,
            end = end,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SelectableCodeBlockPreview() {
    Column(
        modifier = Modifier.padding(8.dp),
    ) {
        SelectableCodeBlock(text = "Text")
        Spacer(modifier = Modifier.padding(8.dp))
        SelectableCodeBlock(
            text =
                """
                Lorem ipsum dolor sit amet, consectetur adipiscing elit. 
                Donec euismod, nisl eget aliquam ultricies, 
                nisl nisl aliquet nisl, nec aliquam nisl nisl nec.
                """.trimIndent(),
        )
        Spacer(modifier = Modifier.padding(8.dp))
        SelectableCodeBlock(
            text =
                """
                Lorem ipsum dolor sit amet, consectetur adipiscing elit. 
                b72f5b4a-7fe2-40f4-8b23-87c92fb01e93 sadfas 
                52de9f4b-a38b-468b-87dd-bfa7254c0f4f,17733282-9fd3-4e9c-b222-c948fbefcf83 asdwer
                nisl nisl aliquet nisl, nec aliquam nisl nisl nec.
                """.trimIndent(),
        )
    }
}
