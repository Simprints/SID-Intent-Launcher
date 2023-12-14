package com.simprints.intentlauncher.fragments.edit

import android.content.Intent
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.simprints.intentlauncher.fragments.LiveMessageEvent
import com.simprints.intentlauncher.fragments.ui.ViewModelForAdapter
import com.simprints.intentlauncher.model.domain.IntentArgument
import com.simprints.intentlauncher.model.domain.SimprintsIntent
import com.simprints.intentlauncher.model.domain.SimprintsResult
import com.simprints.intentlauncher.model.domain.toIntent
import com.simprints.intentlauncher.model.local.LocalSimprintsIntentDataSource
import com.simprints.intentlauncher.model.local.LocalSimprintsResultDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class IntentEditViewModel @Inject constructor(
    private val intentsDao: LocalSimprintsIntentDataSource,
    private val resultDao: LocalSimprintsResultDataSource,
    private val gson: Gson,
) : ViewModel(), ViewModelForAdapter {

    companion object {
        const val REQUEST_CODE = 1
    }

    val viewEditEvents = LiveMessageEvent<ViewEditIntentEvents>()
    lateinit var intent: SimprintsIntent


    private var lastIntentSentTime: Date? = null

    override fun getCount() = addPlaceholderIfNecessary(false).let { intent.extra.size }
    fun getIntentArguments(position: Int) = intent.extra[position]

    fun onIntentArgumentKeyChange(key: CharSequence, position: Int) {
        intent.extra.let {
            it[position].key = key.toString()
            addPlaceholderIfNecessary()

            updateSimprintsIntent(intent)

        }
    }

    fun onIntentArgumentValueChange(value: CharSequence, position: Int) {
        intent.extra.let {
            it[position].value = value.toString()
            addPlaceholderIfNecessary()

            updateSimprintsIntent(intent)
        }
    }

    private fun addPlaceholderIfNecessary(notifyToAdapter: Boolean = true) {
        val empty = intent.extra.filter { it.value.isEmpty() && it.key.isEmpty() }
        if (empty.isEmpty()) {
            intent.extra.add(IntentArgument("", ""))
            if (notifyToAdapter) {
                viewEditEvents.sendEvent { this.notifyIntentArgumentAdded(intent.extra.size - 1) }
            }
        }
    }

    fun onIntentNameChange(text: CharSequence) {
        intent.name = text.toString()
        updateSimprintsIntent(intent)
    }

    fun onIntentActionChange(text: CharSequence) {
        intent.action = text.toString()
        updateSimprintsIntent(intent)
    }

    fun userDidWantToExecuteIntent(v: View) {
        viewEditEvents.sendEvent {
            try {
                lastIntentSentTime = Date()
                startActivityForResult(intent.toIntent(), REQUEST_CODE)
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
    }

    fun userConfirmedDelete() {
        viewModelScope.launch {
            intentsDao.delete(intent)
        }
        viewEditEvents.sendEvent {
            finish()
        }
    }

    fun userDidWantToDeleteIntent(v: View) {
        viewEditEvents.sendEvent {
            onDeleteButtonClicked()
        }
    }

    private fun updateSimprintsIntent(intent: SimprintsIntent) =
        viewModelScope.launch{
            intentsDao.update(intent.copy(extra = intent.extra.filter { it.key.isNotEmpty() }
                .toMutableList()))
        }

    fun saveResult(resultReceived: String) {
        val simprintsResult = SimprintsResult(
            dateTimeSent = lastIntentSentTime.toString(),
            intentSent = gson.toJson(intent),
            resultReceived = resultReceived
        )

        viewModelScope.launch { resultDao.update(simprintsResult) }
    }

    interface ViewEditIntentEvents {
        fun startActivityForResult(intent: Intent, requestCode: Int)
        fun finish()
        fun notifyIntentArgumentAdded(position: Int)
        fun onDeleteButtonClicked()
    }
}
