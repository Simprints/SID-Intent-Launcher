package com.simprints.simprintsidtester.fragments.result

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.google.accompanist.themeadapter.material.MdcTheme
import com.simprints.simprintsidtester.R
import com.simprints.simprintsidtester.databinding.ResultListFragmentBinding
import com.simprints.simprintsidtester.fragments.result.compose.ResultListScreen
import org.koin.androidx.viewmodel.ext.android.viewModel

class ResultListFragment : Fragment() {
    private val resultListViewModel: ResultListViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = ResultListFragmentBinding.inflate(layoutInflater).apply {
            composeResultList.apply {
                // Dispose the Composition when the view's LifecycleOwner
                // is destroyed
                setViewCompositionStrategy(
                    ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
                )
                setContent {
                    MdcTheme {
                        ResultListScreen(resultListViewModel = resultListViewModel)
                    }
                }
            }
        }
        setupMenu()
        return binding.root
    }

    private fun setupMenu() = requireActivity().addMenuProvider(
        object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.result_list_menu, menu)
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
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // handled by view model directly
                return false
            }
        },
        viewLifecycleOwner,
        Lifecycle.State.RESUMED,
    )

}