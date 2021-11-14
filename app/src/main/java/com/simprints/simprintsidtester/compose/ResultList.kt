package com.simprints.simprintsidtester.compose

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.android.material.composethemeadapter.MdcTheme
import com.simprints.simprintsidtester.model.domain.SimprintsResult

@Composable
fun ResultList(resultList: List<SimprintsResult>, modifier: Modifier = Modifier) {
    MdcTheme {
        LazyColumn {
            items(items = resultList) { item ->
                ResultItem(resultInfo = item, modifier)
            }
        }
    }
}