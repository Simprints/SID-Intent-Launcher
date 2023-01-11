package com.simprints.simprintsidtester.fragments.list.compose

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.simprints.simprintsidtester.compose.NothingToDisplay
import com.simprints.simprintsidtester.fragments.list.IntentListViewModel

@ExperimentalMaterialApi
@Composable
fun IntentListScreen(intentListViewModel: IntentListViewModel) {
    val resultList by intentListViewModel.getSimprintsIntents().observeAsState()

    resultList?.let { intentList ->
        if (intentList.isEmpty()) {
            NothingToDisplay(
                text = "There is no content to display at the moment. Sophisticated Salmom says you need to add SID intents for you to go fishing. Happy fishing!",
                modifier = Modifier
            )
        } else {
            LazyColumn {
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
