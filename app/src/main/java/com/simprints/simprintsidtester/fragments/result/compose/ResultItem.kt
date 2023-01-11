package com.simprints.simprintsidtester.fragments.result.compose

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Divider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simprints.simprintsidtester.model.domain.SimprintsResult

@Composable
fun ResultItem(
    resultInfo: SimprintsResult,
    onCopyToClipboard: (String) -> Unit,
    modifier: Modifier,
) {
    Surface(shape = RoundedCornerShape(8.dp), elevation = 8.dp, modifier = modifier) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(
                resultInfo.dateTimeSent,
                fontWeight = FontWeight.Bold,
            )
            ExpandableTextBlock("Sent", resultInfo.intentSent, onCopyToClipboard)
            ExpandableTextBlock("Received", resultInfo.resultReceived, onCopyToClipboard)
        }
    }
}

private const val MIN_LINES = 3

@Composable
private fun ExpandableTextBlock(
    title: String,
    content: String,
    onCopyToClipboard: (String) -> Unit,
) {
    var maxLines by remember { mutableStateOf(MIN_LINES) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 12.dp, bottom = 4.dp),
    ) {
        SelectionContainer {
            Text(
                text = title,
                fontSize = 12.sp,
                color = Color.LightGray,
            )
        }
        Divider(
            color = Color.LightGray,
            startIndent = 4.dp,
            modifier = Modifier.padding(top = 2.dp),
        )
    }
    Surface(
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures(
                onTap = { maxLines = if (maxLines == MIN_LINES) Int.MAX_VALUE else MIN_LINES },
                onLongPress = { onCopyToClipboard(content) },
            )
        }
    ) {
        Text(
            text = content,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
