package com.simprints.intentlauncher.fragments.list

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simprints.intentlauncher.fragments.LiveMessageEvent
import com.simprints.intentlauncher.fragments.ui.ViewModelForAdapter
import com.simprints.intentlauncher.model.domain.SimprintsIntent
import com.simprints.intentlauncher.model.local.LocalSimprintsIntentDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class IntentListViewModel @Inject constructor(
    private val intentsDao: LocalSimprintsIntentDataSource,
) : ViewModel(), ViewModelForAdapter {

    private val intentsList: MutableList<SimprintsIntent> = mutableListOf()
    val viewListEvents = LiveMessageEvent<ViewListIntentEvents>()

    override fun getCount(): Int = intentsList.size

    fun getSimprintsIntents() = intentsDao.getIntents()

    fun deleteUncompletedSimprintsIntent() {
        viewModelScope.launch {
            intentsDao.deleteUncompletedSimprintsIntent()
        }
    }

    fun userDidWantToDuplicateIntent(position: Int) {
        intentsList[position].copy(id = UUID.randomUUID().toString()).let {
            intentsList.add(it)
            viewModelScope.launch {
                intentsDao.update(it)
            }
        }
    }

    fun userDidWantToCreateANewIntent(view: View) {
        SimprintsIntent().let {
            viewModelScope.launch {
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
        fun onCreateIntent(newIntent: SimprintsIntent)
        fun onListFragmentInteraction(intent: SimprintsIntent)
    }
}
