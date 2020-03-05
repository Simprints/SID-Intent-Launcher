package com.simprints.simprintsidtester

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation.findNavController
import com.simprints.simprintsidtester.fragments.BackButtonInterface
import com.simprints.simprintsidtester.fragments.list.IntentListFragment
import com.simprints.simprintsidtester.fragments.list.IntentListFragmentDirections.Companion.openEditIntentFragment
import com.simprints.simprintsidtester.model.domain.SimprintsIntent

class MainActivity : AppCompatActivity(), IntentListFragment.UserListActions {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onListFragmentInteraction(intent: SimprintsIntent) {
        navigateToEditIntentFragment(intent)
    }

    private fun navigateToEditIntentFragment(intent: SimprintsIntent) {
        openIntentDetails(intent)
    }

    override fun onCreateIntent(newIntent: SimprintsIntent) {
        openIntentDetails(newIntent)
    }

    private fun openIntentDetails(intent: SimprintsIntent) {
        openEditIntentFragment(intent).also { findNavController(this, R.id.nav_host_fragment).navigate(it) }
    }

    override fun onBackPressed() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        navHostFragment?.let {
            val f = it.childFragmentManager.fragments[0] as BackButtonInterface
            f.onBackPressed()
        }
        super.onBackPressed()  // this exits the app.
    }
}
