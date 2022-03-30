package com.simprints.simprintsidtester.fragments.list

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.simprints.simprintsidtester.R
import com.simprints.simprintsidtester.databinding.IntentListFragmentBinding
import com.simprints.simprintsidtester.fragments.list.IntentListViewModel.ViewListIntentEvents
import com.simprints.simprintsidtester.fragments.ui.RecyclerViewAdapter
import com.simprints.simprintsidtester.fragments.ui.WrapContentLinearLayoutManager
import com.simprints.simprintsidtester.model.domain.SimprintsIntent
import org.koin.android.viewmodel.ext.android.viewModel

class IntentListFragment : Fragment(), ViewListIntentEvents {

    private var listener: UserListActions? = null
    private val intentListViewModel: IntentListViewModel by viewModel()
    private lateinit var binding: IntentListFragmentBinding
    private lateinit var adapter: RecyclerViewAdapter<IntentListViewModel>

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        intentListViewModel.getSimprintsIntents().observe(viewLifecycleOwner) {
            it?.let {
                intentListViewModel.addIntents(it)
                adapter.notifyDataSetChanged()
            }
        }
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
            setHasOptionsMenu(true)
            return root
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.intent_list_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item, findNavController()) ||
                super.onOptionsItemSelected(item)
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
        intentListViewModel.deleteUncompletedSimprintsIntent()
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

