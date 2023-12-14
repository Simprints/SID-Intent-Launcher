package com.simprints.intentlauncher.fragments.list

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.accompanist.themeadapter.material.MdcTheme
import com.simprints.intentlauncher.R
import com.simprints.intentlauncher.databinding.IntentListFragmentBinding
import com.simprints.intentlauncher.fragments.list.IntentListViewModel.ViewListIntentEvents
import com.simprints.intentlauncher.fragments.list.compose.IntentListScreen
import com.simprints.intentlauncher.model.domain.SimprintsIntent
import dagger.hilt.android.AndroidEntryPoint


@ExperimentalMaterialApi
@AndroidEntryPoint
class IntentListFragment : Fragment(), ViewListIntentEvents {

    private var listener: UserListActions? = null
    private val intentListViewModel: IntentListViewModel by viewModels()
    private lateinit var binding: IntentListFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate<IntentListFragmentBinding?>(
            inflater,
            R.layout.intent_list_fragment,
            container,
            false
        ).apply {
            composeIntentsList.apply {
                // Dispose the Composition when the view's LifecycleOwner
                // is destroyed
                setViewCompositionStrategy(
                    ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
                )
                setContent {
                    MdcTheme {
                        IntentListScreen(
                            intentListViewModel = intentListViewModel,
                            onIntegrationClick = ::openIntegrationScreen,
                        )
                    }
                }
            }
        }

        intentListViewModel.viewListEvents.setEventReceiver(this, this)
        intentListViewModel.getSimprintsIntents().observe(viewLifecycleOwner) {
            it?.let {
                intentListViewModel.addIntents(it)
            }
        }

        binding.viewModel = intentListViewModel

        setupMenu()
        return binding.root
    }

    private fun setupMenu() = requireActivity().addMenuProvider(
        object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.intent_list_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return NavigationUI.onNavDestinationSelected(menuItem, findNavController())
            }
        },
        viewLifecycleOwner,
        Lifecycle.State.RESUMED,
    )

    private fun openIntegrationScreen() {
        findNavController().navigate(R.id.action_intent_list_to_integrationFragment)
    }

    override fun onCreateIntent(newIntent: SimprintsIntent) {
        listener?.onCreateIntent(newIntent)
    }

    override fun onListFragmentInteraction(intent: SimprintsIntent) {
        listener?.onListFragmentInteraction(intent)
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
