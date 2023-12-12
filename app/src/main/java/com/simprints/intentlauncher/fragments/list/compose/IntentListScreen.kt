package com.simprints.intentlauncher.fragments.list.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.simprints.intentlauncher.compose.NothingToDisplay
import com.simprints.intentlauncher.fragments.list.IntentListViewModel

@ExperimentalMaterialApi
@Composable
fun IntentListScreen(
    intentListViewModel: IntentListViewModel,
    onIntegrationClick: () -> Unit,
) {
    val resultList by intentListViewModel.getSimprintsIntents().observeAsState()

    Column {

        Button(
            onClick = onIntegrationClick,
            modifier = Modifier
                .padding(0.dp, 16.dp)
                .fillMaxWidth(),
        ) { Text("LibSimprints Integration", modifier = Modifier.padding(8.dp)) }

        resultList?.let { intentList ->
            if (intentList.isEmpty()) {
                NothingToDisplay(
                    text = "There is no content to display at the moment. Sophisticated Salmom says you need to add SID intents for you to go fishing. Happy fishing!",
                    modifier = Modifier
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxHeight(),
                ) {
                    itemsIndexed(intentList) { index, item ->
                        IntentItem(
                            simprintsIntent = item,
                            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                            onClickOpenIntent = {
                                intentListViewModel.userDidWantToCreateANewIntent(index)
                            },
                            onClickCopy = {
                                intentListViewModel.userDidWantToDuplicateIntent(index)
                            }
                        )
                    }
                }
            }
        }
    }
}
