package com.simprints.intentlauncher.ui.details.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.simprints.intentlauncher.model.domain.IntentCall
import com.simprints.intentlauncher.model.domain.IntentFields
import com.simprints.intentlauncher.model.domain.IntentResult
import com.simprints.intentlauncher.ui.composables.DividerWithTitle
import com.simprints.intentlauncher.ui.composables.SelectableCodeBlock
import com.simprints.intentlauncher.ui.theme.AppTypography

@Composable
fun IntentDetails(
    data: IntentCall,
    onCopyFields: () -> Unit = {},
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .padding(8.dp)
            .verticalScroll(scrollState)
    ) {
        Text(
            text = data.action,
            style = AppTypography.body1,
        )
        Text(
            text = data.timestamp,
            style = AppTypography.body2,
        )
        Spacer(modifier = Modifier.padding(8.dp))
        Text(
            text = "Result: ${data.result?.mappedResultCode() ?: "No result"}",
            style = AppTypography.body1,
        )
        DividerWithTitle("Intent extras")
        SelectableCodeBlock(data.extra.entries.joinToString("\n") { "${it.key}: ${it.value}" })
        Button(
            onClick = onCopyFields,
            modifier = Modifier.align(alignment = Alignment.End)
        ) {
            Text(text = "Reuse intent fields")
        }
        DividerWithTitle("Result")
        Text(
            text = data.result?.mappedResultCode() ?: "No result",
            style = AppTypography.body1,
        )
        SelectableCodeBlock(data.result?.json ?: "No result")
    }
}

@Preview(showBackground = true, widthDp = 480, heightDp = 640)
@Composable
private fun IntentDetailsPreview() {
    IntentDetails(
        data = IntentCall(
            timestamp = "2021-09-09 12:00:00",
            action = "com.simprints.ACTION_VERIFY",
            extra = mapOf(
                "extra1" to "value1",
                "extra2" to "value2",
            ),
            fields = IntentFields(
                projectId = "project",
                moduleId = "module",
                userId = "user",
            ),
            result = IntentResult(
                code = -1,
                json = """{"result": "success"}""",
            ),
        )
    )
}
