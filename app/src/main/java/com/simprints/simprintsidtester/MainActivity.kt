package com.simprints.simprintsidtester

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.simprints.simprintsidtester.fragments.list.IntentListFragment
import com.simprints.simprintsidtester.fragments.list.IntentListFragmentDirections.Companion.openEditIntentFragment
import com.simprints.simprintsidtester.model.domain.SimprintsIntent
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), IntentListFragment.UserListActions {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(topAppBar)
        val navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
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
        openEditIntentFragment(intent).also { findNavController(R.id.nav_host_fragment).navigate(it) }
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment).navigateUp() || super.onSupportNavigateUp()
    }
}
