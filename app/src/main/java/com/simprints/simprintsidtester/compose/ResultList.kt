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
        ResultListContent(it)
    }
}

@Composable
private fun ResultListContent(resultList: List<SimprintsResult>) {
    LazyColumn {
        items(items = resultList) { item ->
            ResultItem(resultInfo = item, modifier = Modifier.padding(start = 16.dp, end = 16.dp))
        }
    }
}