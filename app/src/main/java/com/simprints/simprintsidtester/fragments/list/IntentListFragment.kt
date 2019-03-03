package com.simprints.simprintsidtester.fragments.list

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.simprints.simprintsidtester.R
import com.simprints.simprintsidtester.databinding.IntentListFragmentBinding
import com.simprints.simprintsidtester.fragments.list.IntentListViewModel.ViewListIntentEvents
import com.simprints.simprintsidtester.fragments.ui.RecyclerViewAdapter
import com.simprints.simprintsidtester.fragments.ui.WrapContentLinearLayoutManager
import com.simprints.simprintsidtester.model.domain.SimprintsIntent
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel

class IntentListFragment : Fragment(), ViewListIntentEvents {

    private var listener: UserListActions? = null
    private val intentListViewModel by viewModel<IntentListViewModel>()
    private lateinit var binding: IntentListFragmentBinding
    private lateinit var adapter: RecyclerViewAdapter<IntentListViewModel>

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        intentListViewModel.getSimprintsIntents().observe(this, Observer {
            it?.let { intents ->
                intentListViewModel.addIntents(it)
                adapter.notifyDataSetChanged()
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.intent_list_fragment, container, false)
        intentListViewModel.viewListEvents.setEventReceiver(this, this)
        with(binding) {
            context?.let {
                viewModel = intentListViewModel
                adapter = RecyclerViewAdapter(intentListViewModel, R.layout.intent_list_item)
                intentsList.layoutManager = WrapContentLinearLayoutManager(it)
                intentsList.adapter = adapter
            }

            return root
        }
    }

    override fun onCreateIntent(newIntent: SimprintsIntent) {
        listener?.onCreateIntent(newIntent)
    }

    override fun onListFragmentInteraction(intent: SimprintsIntent) {
        listener?.onListFragmentInteraction(intent)
    }

    override fun updateListView() {
        adapter.notifyDataSetChanged()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is UserListActions) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnListFragmentInteractionListener")
        }
    }

    override fun onResume() {
        super.onResume()
        GlobalScope.launch {
            intentListViewModel.deleteUncompletedSimprintsIntent()
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface UserListActions {
        fun onCreateIntent(newIntent: SimprintsIntent)
        fun onListFragmentInteraction(intent: SimprintsIntent)
    }
}

