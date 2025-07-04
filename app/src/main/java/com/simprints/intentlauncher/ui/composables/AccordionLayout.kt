package com.simprints.intentlauncher.ui.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.simprints.intentlauncher.tools.testAccessibleTag
import com.simprints.intentlauncher.ui.theme.AppShapes
import com.simprints.intentlauncher.ui.theme.AppTypography
import java.util.Locale

@Composable
fun AccordionLayout(
    title: String,
    testTag: String,
    defaultExpanded: Boolean = true,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    var isExpanded by remember { mutableStateOf(defaultExpanded) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Color.Black.copy(alpha = 0.2f), shape = AppShapes.large),
    ) {
        Row(
            modifier = Modifier
                .testAccessibleTag(testTag)
                .fillMaxWidth()
                .padding(8.dp)
                .clickable { isExpanded = !isExpanded },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title.uppercase(Locale.getDefault()),
                style = AppTypography.caption,
            )
            Icon(
                imageVector = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                contentDescription = "Expand/Collapse",
                modifier = Modifier
                    .size(24.dp),
            )
        }

        AnimatedVisibility(visible = isExpanded) {
            content()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AccordionLayoutPreviewOpen() {
    AccordionLayout(
        title = "Click to collapse",
        testTag = "Accordion",
        defaultExpanded = true,
    ) {
        Text(
            text = "Hello World",
            modifier = Modifier
                .background(Color.Green)
                .fillMaxWidth()
                .padding(8.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AccordionLayoutPreviewClosed() {
    AccordionLayout(
        title = "Click to expand",
        testTag = "Accordion",
        defaultExpanded = false,
    ) {
        Text(
            text = "Hello World",
            modifier = Modifier
                .background(Color.Green)
                .fillMaxWidth()
                .padding(8.dp),
        )
    }
}
