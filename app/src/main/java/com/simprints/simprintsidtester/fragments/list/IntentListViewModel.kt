package com.simprints.simprintsidtester.fragments.list

import android.view.View
import androidx.lifecycle.ViewModel
import com.simprints.simprintsidtester.fragments.LiveMessageEvent
import com.simprints.simprintsidtester.fragments.ui.ViewModelForAdapter
import com.simprints.simprintsidtester.model.domain.SimprintsIntent
import com.simprints.simprintsidtester.model.local.LocalSimprintsIntentDataSource
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import java.util.*

class IntentListViewModel : ViewModel(), KoinComponent,
    ViewModelForAdapter {

    private val intentsDao: LocalSimprintsIntentDataSource by inject()
    private val intentsList: MutableList<SimprintsIntent> = mutableListOf()
    val viewListEvents = LiveMessageEvent<ViewListIntentEvents>()

    override fun getCount(): Int = intentsList.size
    fun getSimprintsIntentAt(position: Int) = intentsList[position]

    fun getSimprintsIntents() = intentsDao.getIntents()

    fun deleteUncompletedSimprintsIntent() = intentsDao.deleteUncompletedSimprintsIntent()

    fun userDidWantToDuplicateIntent(position: Int) {
        intentsList[position].copy(id = UUID.randomUUID().toString()).let {
            intentsList.add(it)
            GlobalScope.launch {
                intentsDao.update(it)
            }
            viewListEvents.sendEvent { updateListView() }
        }
    }

    fun userDidWantToCreateANewIntent(view: View) {
        SimprintsIntent().let {
            GlobalScope.launch {
                intentsDao.update(it)
            }
            viewListEvents.sendEvent { onCreateIntent(it) }
        }
    }

    fun userDidWantToCreateANewIntent(position: Int) {
        intentsList[position].let {
            viewListEvents.sendEvent { onCreateIntent(it) }
        }
    }

    fun addIntents(list: List<SimprintsIntent>) {
        intentsList.clear()
        intentsList.addAll(list)
    }

    interface ViewListIntentEvents {
        fun updateListView()
        fun onCreateIntent(newIntent: SimprintsIntent)
        fun onListFragmentInteraction(intent: SimprintsIntent)
    }
}