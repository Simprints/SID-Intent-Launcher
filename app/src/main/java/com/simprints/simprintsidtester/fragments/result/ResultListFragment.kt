package com.simprints.simprintsidtester.fragments.result

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simprints.simprintsidtester.R
import com.simprints.simprintsidtester.fragments.ui.SimpleListAdapter
import com.simprints.simprintsidtester.model.domain.SimprintsResult
import kotlinx.android.synthetic.main.result_list_fragment.*
import kotlinx.android.synthetic.main.result_list_item.view.*
import org.koin.android.viewmodel.ext.android.viewModel

class ResultListFragment : Fragment(R.layout.result_list_fragment) {
    private val resultListViewModel: ResultListViewModel by viewModel()
    private val resultAdapter = object : SimpleListAdapter<SimprintsResult>(R.layout.result_list_item) {
        override fun onBindData(position: Int, viewHolder: RecyclerView.ViewHolder, data: SimprintsResult) {
            viewHolder.itemView.resultDate.text = data.dateTimeSent
            viewHolder.itemView.resultIntent.text = data.intentSent
            viewHolder.itemView.resultReceived.text = data.resultReceived
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        resultsList.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = resultAdapter
        }

        resultListViewModel.getResults().observe(viewLifecycleOwner, Observer {
            it?.let {
                resultAdapter.setItems(it.reversed())
            }
        })
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