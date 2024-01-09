package com.simprints.intentlauncher.ui.presets.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.simprints.intentlauncher.domain.IntentFields
import com.simprints.intentlauncher.domain.Preset
import com.simprints.intentlauncher.ui.theme.AppColorScheme
import com.simprints.intentlauncher.ui.theme.AppShapes
import com.simprints.intentlauncher.ui.theme.AppTypography

@Composable
fun PresetItem(
    data: Preset,
    modifier: Modifier = Modifier,
    onUse: (Preset) -> Unit = {},
    onDelete: (Preset) -> Unit = {},
) {
    Surface(
        shape = AppShapes.medium,
        elevation = 2.dp,
        modifier = modifier.padding(4.dp),

    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            Text(
                text = data.name,
                style = AppTypography.body1,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            )
            Spacer(modifier = Modifier.padding(4.dp))
            Text(
                text = "${data.fields.projectId} - ${data.fields.moduleId} - ${data.fields.userId}",
                style = AppTypography.body2,
                modifier = Modifier.padding(horizontal = 8.dp),
            )
            Spacer(modifier = Modifier.padding(4.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(onClick = { onDelete(data) }) {
                    Text(
                        text = "Delete",
                        color = AppColorScheme.error
                    )
                }
                TextButton(onClick = { onUse(data) }) { Text(text = "Use preset") }
            }
        }
    }
}

@Preview
@Composable
private fun PresetItemPreview() {
    PresetItem(
        data = Preset(
            name = "Preset 1",
            fields = IntentFields(
                projectId = "Project 1",
                moduleId = "Module 1",
                userId = "User 1",
            ),
        ),
    )
}
