package com.simprints.simprintsidtester.fragments.result

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.google.android.material.composethemeadapter.MdcTheme
import com.simprints.simprintsidtester.R
import com.simprints.simprintsidtester.compose.ResultList
import com.simprints.simprintsidtester.databinding.ResultListFragmentBinding
import org.koin.android.viewmodel.ext.android.viewModel

class ResultListFragment : Fragment() {
    private val resultListViewModel: ResultListViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = ResultListFragmentBinding.inflate(layoutInflater).apply {
            composeResultList.apply {
                // Dispose the Composition when the view's LifecycleOwner
                // is destroyed
                setViewCompositionStrategy(
                    ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
                )
                setContent {
                    MdcTheme {
                        ResultList(resultListViewModel = resultListViewModel)
                    }
                }
            }
        }
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.result_list_menu, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.queryHint = "Search by any text on the row"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                resultListViewModel.filterList(newText ?: "")
                return true
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
    }

}