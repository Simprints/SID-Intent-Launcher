package com.simprints.intentlauncher.ui.history.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.simprints.intentlauncher.domain.IntentCall
import com.simprints.intentlauncher.domain.IntentFields
import com.simprints.intentlauncher.domain.IntentResult
import com.simprints.intentlauncher.ui.theme.AppShapes
import com.simprints.intentlauncher.ui.theme.AppTypography

@Composable
fun HistoryItem(
    data: IntentCall,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit = {},
) {
    Surface(
        shape = AppShapes.medium,
        elevation = 2.dp,
        modifier = modifier
            .padding(4.dp)
            .clickable { onClick(data.id) },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
        ) {
            Text(
                text = data.action,
                style = AppTypography.subtitle2,
            )
            Spacer(modifier = Modifier.padding(4.dp))

            Text(
                text = data.timestamp,
                style = AppTypography.caption,
            )
            Text(
                text = "${data.fields.projectId} - ${data.fields.moduleId} - ${data.fields.userId}",
                style = AppTypography.caption,
            )
            Text(
                text = "Result: ${data.result?.mappedResultCode() ?: "No result"}",
                style = AppTypography.caption,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ItemPreview() {
    HistoryItem(
        data = IntentCall(
            timestamp = "2021-09-09 12:00:00",
            action = "com.simprints.ACTION_VERIFY",
            fields = IntentFields("project", "module", "user"),
            result = IntentResult(-1),
        ),
    )
}

@Preview(showBackground = true)
@Composable
private fun ItemFailedPreview() {
    HistoryItem(
        data = IntentCall(
            timestamp = "2021-09-09 12:00:00",
            action = "com.simprints.id.REGISTER_LAST_BIOMETRICS",
            fields = IntentFields("project", "module", "user"),
            result = IntentResult(12),
        ),
    )
}
