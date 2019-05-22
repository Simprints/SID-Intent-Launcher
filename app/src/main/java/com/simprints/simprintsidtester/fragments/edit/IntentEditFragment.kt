package com.simprints.simprintsidtester.fragments.edit

import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.gson.GsonBuilder
import com.simprints.libsimprints.Constants
import com.simprints.libsimprints.Identification
import com.simprints.libsimprints.RefusalForm
import com.simprints.libsimprints.Registration
import com.simprints.simprintsidtester.MainActivity
import com.simprints.simprintsidtester.R
import com.simprints.simprintsidtester.databinding.IntentEditFragmentBinding
import com.simprints.simprintsidtester.fragments.BackButtonInterface
import com.simprints.simprintsidtester.fragments.ui.RecyclerViewAdapter
import com.simprints.simprintsidtester.fragments.ui.WrapContentLinearLayoutManager
import org.koin.android.viewmodel.ext.android.viewModel


class IntentEditFragment : Fragment(), BackButtonInterface, IntentEditViewModel.ViewEditIntentEvents {

    private val intentEditViewModel by viewModel<IntentEditViewModel>()

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
                intentEditResponse.movementMethod = ScrollingMovementMethod()
            }
        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.edit_intent_menu_action, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return true
    }

    override fun notifyIntentArgumentAdded(position: Int) {
        adapter.notifyItemInserted(position)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val gson = GsonBuilder().setPrettyPrinting().create()

        val enrol = data?.getParcelableExtra<Registration>(Constants.SIMPRINTS_REGISTRATION)
        val verify = data?.getParcelableExtra<Identification>(Constants.SIMPRINTS_VERIFICATION)
        val identify = data?.getParcelableArrayListExtra<Identification>(Constants.SIMPRINTS_IDENTIFICATIONS)
        val refusal = data?.getParcelableExtra<RefusalForm>(Constants.SIMPRINTS_REFUSAL_FORM)

        binding.intentEditResponse.text = when {
            enrol != null -> "$resultCode ${gson.toJson(enrol)}"
            verify != null -> "$resultCode ${gson.toJson(verify)}"
            identify != null -> "$resultCode ${gson.toJson(identify)}"
            refusal != null -> "$resultCode ${gson.toJson(refusal)}"
            else -> "$resultCode"
        }
    }

    override fun onBackPressed() {
    }

    override fun finish() {
        Navigation.findNavController(activity as MainActivity, R.id.nav_host_fragment).popBackStack()
    }
}
