package com.simprints.intentlauncher.ui.events.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.ripple
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.simprints.intentlauncher.ui.composables.AccordionLayout
import com.simprints.intentlauncher.ui.events.model.eventMock
import com.simprints.intentlauncher.ui.intent.model.ResponseEvent
import com.simprints.intentlauncher.ui.theme.AppColorScheme
import com.simprints.intentlauncher.ui.theme.AppTypography
import java.util.Date

@Composable
fun ResponseEventItem(
    event: ResponseEvent,
    position: Int,
    modifier: Modifier = Modifier,
    onOptionsClick: (ResponseEvent) -> Unit
) {
    val captionStyle = AppTypography.caption.copy(color = Color.Black.copy(alpha = 0.7f))
    Card(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "$position. ${event.type}",
                    style = AppTypography.subtitle1,
                )
                Surface(
                    modifier = Modifier
                        .size(24.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(bounded = false),
                            onClick = { onOptionsClick(event) }
                        ),
                    shape = CircleShape,
                    color = Color.Transparent,
                    contentColor = MaterialTheme.colors.primary.copy(alpha = ContentAlpha.high)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Filled.MoreVert,
                            contentDescription = "Event actions",
                            tint = AppColorScheme.onBackground
                        )
                    }
                }
            }
            Divider(
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                "id: ${event.id}",
                style = captionStyle
            )
            Text(
                "Created at: ${Date(event.createdAtMs)}",
                style = captionStyle
            )
            Text(
                "Project id: ${event.projectId}",
                style = captionStyle
            )
            Text(
                "Scope id: ${event.scopeId}",
                style = captionStyle
            )
            AccordionLayout(
                title = "payload",
                defaultExpanded = false,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(8.dp, end = 8.dp, bottom = 8.dp)
                ) {
                    PayloadEntry(event.payload, style = captionStyle)
                }
            }
        }
    }
}


@Composable
private fun PayloadEntry(
    payloadMap: Map<String, Any>,
    paddingStart: Dp = 0.dp,
    style: TextStyle
) {
    val paddingStep = 4.dp
    for ((key, value) in payloadMap) {
        if (value is Map<*, *>) {
            Text("$key: {", style = style, modifier = Modifier.padding(start = paddingStart))
            PayloadEntry(
                payloadMap = value as Map<String, Any>,
                paddingStart = paddingStart + paddingStep,
                style = style
            )
            Text(
                "}",
                style = style,
                modifier = Modifier.padding(start = paddingStart)
            )
        } else {
            Text(
                "$key: $value",
                style = style,
                modifier = Modifier.padding(start = paddingStart)
            )
        }
    }
}

@Preview
@Composable
fun ResponseEventItemPreview() {
    ResponseEventItem(
        event = eventMock,
        position = 1,
        onOptionsClick = {}
    )
}
