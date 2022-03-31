package com.simprints.simprintsidtester.compose

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.simprints.simprintsidtester.fragments.list.IntentListViewModel

@ExperimentalMaterialApi
@Composable
fun IntentList(intentListViewModel: IntentListViewModel) {
    IntentListContent(intentListViewModel)
}

@ExperimentalMaterialApi
@Composable
private fun IntentListContent(intentListViewModel: IntentListViewModel) {
    val resultList by intentListViewModel.getSimprintsIntents().observeAsState()
    resultList?.let { intentList ->
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