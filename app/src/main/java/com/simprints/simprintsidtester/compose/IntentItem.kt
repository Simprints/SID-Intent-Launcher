package com.simprints.simprintsidtester.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.simprints.simprintsidtester.R
import com.simprints.simprintsidtester.model.domain.SimprintsIntent

@ExperimentalMaterialApi
@Composable
fun IntentItem(
    simprintsIntent: SimprintsIntent,
    modifier: Modifier,
    onClickOpenIntent: () -> Unit,
    onClickCopy: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        elevation = 8.dp,
        modifier = modifier,
        onClick = onClickOpenIntent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                simprintsIntent.name,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onClickCopy) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_content_copy_black_24dp),
                    contentDescription = "Copy Intent Image"
                )
            }
        }
    }
}