package com.simprints.simprintsidtester.fragments.edit

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.gson.Gson
import com.simprints.simprintsidtester.MainActivity
import com.simprints.simprintsidtester.R
import com.simprints.simprintsidtester.databinding.IntentEditFragmentBinding
import com.simprints.simprintsidtester.fragments.ui.RecyclerViewAdapter
import com.simprints.simprintsidtester.fragments.ui.WrapContentLinearLayoutManager
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class IntentEditFragment : Fragment(), IntentEditViewModel.ViewEditIntentEvents {

    private val intentEditViewModel by viewModel<IntentEditViewModel>()
    private val gson: Gson by inject()
    private lateinit var binding: IntentEditFragmentBinding
    private lateinit var adapter: RecyclerViewAdapter<IntentEditViewModel>

    private val simprintsIntent
        get() = arguments?.let {
            IntentEditFragmentArgs.fromBundle(it).intent
        } ?: throw RuntimeException("No argument")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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

    override fun notifyIntentArgumentAdded(position: Int) {
        adapter.notifyItemInserted(position)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val result = "$resultCode \n ${data?.extras?.keySet()?.map {
            "$it : \n ${gson.toJson(data.extras?.get(it))}"
        }}"

        binding.intentEditResponse.text = result

        intentEditViewModel.saveResult(result)
    }

    override fun finish() {
        Navigation.findNavController(activity as MainActivity, R.id.nav_host_fragment)
            .popBackStack()
    }
}
