package com.simprints.simprintsidtester.compose

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.simprints.simprintsidtester.fragments.result.ResultListViewModel
import com.simprints.simprintsidtester.model.domain.SimprintsResult

@Composable
fun ResultList(resultListViewModel: ResultListViewModel) {
    val resultList by resultListViewModel.getResults().observeAsState()
    resultList?.let {
        if (it.isEmpty()) {
            NothingToDisplay(
                text = "There is no content to display at the moment. Sophisticated Salmom says play around with the app for a bit and your fish will appear here. Happy fishing!",
                modifier = Modifier
            )
        } else
            ResultListContent(it)
    }
}

@Composable
private fun ResultListContent(resultList: List<SimprintsResult>) {
    LazyColumn {
        items(items = resultList) { item ->
            ResultItem(resultInfo = item, modifier = Modifier.padding(all = 12.dp))
        }
    }
}