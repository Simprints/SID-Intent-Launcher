package com.simprints.simprintsidtester.fragments.integration

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import com.google.accompanist.themeadapter.material.MdcTheme
import com.simprints.simprintsidtester.R
import com.simprints.simprintsidtester.databinding.IntegrationFragmentBinding
import com.simprints.simprintsidtester.fragments.integration.compose.IntegrationFormScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IntegrationFragment : Fragment(), IntegrationViewModel.IntegrationIntentEvents {

    private lateinit var binding: IntegrationFragmentBinding
    private val viewModel: IntegrationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewModel.viewEditEvents.setEventReceiver(this, this)

        binding = IntegrationFragmentBinding.inflate(inflater).apply {
            composeContainer.setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )
            composeContainer.setContent {
                MdcTheme {
                    IntegrationFormScreen(viewModel)
                }
            }
        }

        setupMenu()
        return binding.root
    }

    private fun setupMenu() = requireActivity().addMenuProvider(
        object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.integration_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                when (menuItem.itemId) {
                    R.id.integration_save -> {
                        viewModel.save()
                        Toast.makeText(
                            requireContext(),
                            "Field values saved",
                            Toast.LENGTH_SHORT
                        ).show()
                        true
                    }
                    R.id.integration_clear -> {
                        viewModel.clear()
                        Toast.makeText(
                            requireContext(),
                            "Cache cleared",
                            Toast.LENGTH_SHORT
                        ).show()
                        true
                    }
                    else -> false
                }
        },
        viewLifecycleOwner,
        Lifecycle.State.RESUMED,
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadCachedState()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        viewModel.saveResult(resultCode, data)
    }

}
