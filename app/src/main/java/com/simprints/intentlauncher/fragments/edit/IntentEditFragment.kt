package com.simprints.intentlauncher.fragments.edit

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.compose.material.ExperimentalMaterialApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.google.gson.Gson
import com.simprints.intentlauncher.MainActivity
import com.simprints.intentlauncher.R
import com.simprints.intentlauncher.databinding.IntentEditFragmentBinding
import com.simprints.intentlauncher.fragments.ui.RecyclerViewAdapter
import com.simprints.intentlauncher.fragments.ui.WrapContentLinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
@ExperimentalMaterialApi
class IntentEditFragment : Fragment(), IntentEditViewModel.ViewEditIntentEvents {

    private val intentEditViewModel: IntentEditViewModel by viewModels()
    private lateinit var binding: IntentEditFragmentBinding
    private lateinit var adapter: RecyclerViewAdapter<IntentEditViewModel>

    @Inject
    lateinit var gson: Gson

    private val simprintsIntent
        get() = arguments?.let {
            IntentEditFragmentArgs.fromBundle(it).intent
        } ?: throw RuntimeException("No argument")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        intentEditViewModel.intent = simprintsIntent
        intentEditViewModel.viewEditEvents.setEventReceiver(this, this)

        binding = DataBindingUtil.inflate(inflater, R.layout.intent_edit_fragment, container, false)
        context?.let {
            with(binding) {
                viewModel = intentEditViewModel
                adapter = RecyclerViewAdapter(intentEditViewModel, R.layout.intent_edit_item)
                intentEditExtras.layoutManager = WrapContentLinearLayoutManager(it)
                intentEditExtras.adapter = adapter
            }
        }
        return binding.root
    }

    override fun onDeleteButtonClicked() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Sophisticated Salmon says:")
            setMessage("Are you sure you want to delete this intent?")
            setCancelable(false)
                .setPositiveButton(
                    "Yes"
                ) { _, _ ->
                    intentEditViewModel.userConfirmedDelete()
                }
                .setNegativeButton(
                    "No"
                ) { dialog, _ ->
                    dialog.cancel()
                }
        }.create().show()
    }

    override fun notifyIntentArgumentAdded(position: Int) {
        adapter.notifyItemInserted(position)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val result = "$resultCode \n ${gson.toJson(data?.extras)}"

        binding.intentEditResponse.text = result

        intentEditViewModel.saveResult(result)
    }

    override fun finish() {
        Navigation.findNavController(activity as MainActivity, R.id.nav_host_fragment)
            .popBackStack()
    }
}