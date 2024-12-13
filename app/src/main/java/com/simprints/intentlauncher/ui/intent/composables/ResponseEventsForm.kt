package com.simprints.intentlauncher.ui.intent.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.simprints.intentlauncher.ui.composables.AccordionLayout
import com.simprints.intentlauncher.ui.intent.IntentViewState
import com.simprints.intentlauncher.ui.theme.AppColorScheme

@Composable
fun ResponseEventsForm(
    state: IntentViewState,
    defaultExpanded: Boolean = false,
    onClick: (String) -> Unit = {},
) {
    AccordionLayout(
        title = "Events",
        defaultExpanded = defaultExpanded,
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(Color.White)
                .fillMaxWidth()
                .padding(bottom = 8.dp)
                .clickable {
                    if (state.events.isNotEmpty() && state.responseIntentId != null) {
                        onClick(state.responseIntentId)
                    }
                },
        ) {
            Text(
                "Events captured: ${state.events.size}",
                modifier = Modifier.padding(all = 8.dp),
            )
            if (state.events.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .background(shape = CircleShape, color = AppColorScheme.background)
                        .padding(all = 8.dp),
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = AppColorScheme.onBackground,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FollowUpFlowFormPreview() {
    FollowUpFlowForm(
        IntentViewState(),
        defaultExpanded = true,
    )
}
