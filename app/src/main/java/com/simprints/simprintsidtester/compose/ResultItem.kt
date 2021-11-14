package com.simprints.simprintsidtester.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.simprints.simprintsidtester.model.domain.SimprintsResult

@Composable
fun ResultItem(resultInfo: SimprintsResult, modifier: Modifier) {
    Surface(shape = RoundedCornerShape(8.dp), elevation = 8.dp, modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(resultInfo.dateTimeSent)
            Text(resultInfo.intentSent)
            Text(resultInfo.resultReceived)
        }
    }
}