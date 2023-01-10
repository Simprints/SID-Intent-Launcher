package com.simprints.simprintsidtester.fragments.integration

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.google.android.material.composethemeadapter.MdcTheme
import com.simprints.simprintsidtester.databinding.IntegrationFragmentBinding
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

    return binding.root
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)

    viewModel.saveResult(resultCode, data)
  }

}
