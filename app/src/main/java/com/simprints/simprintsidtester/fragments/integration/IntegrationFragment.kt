package com.simprints.simprintsidtester.fragments.integration

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.google.android.material.composethemeadapter.MdcTheme
import com.simprints.simprintsidtester.R
import com.simprints.simprintsidtester.databinding.IntegrationFragmentBinding
import com.simprints.simprintsidtester.fragments.integration.compose.IntegrationFormScreen
import org.koin.androidx.viewmodel.ext.android.viewModel

class IntegrationFragment : Fragment(), IntegrationViewModel.IntegrationIntentEvents {

    private lateinit var binding: IntegrationFragmentBinding
    private val viewModel: IntegrationViewModel by viewModel()

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

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadCachedState()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.integration_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.integration_save -> {
            viewModel.save()
            Toast.makeText(requireContext(), "Field values saved", Toast.LENGTH_SHORT).show()
            true
        }
        R.id.integration_clear -> {
            viewModel.clear()
            Toast.makeText(requireContext(), "Cache cleared", Toast.LENGTH_SHORT).show()
            true
        }
        else -> false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        viewModel.saveResult(resultCode, data)
    }

}
