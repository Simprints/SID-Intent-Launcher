package com.simprints.simprintsidtester.fragments.edit

import android.content.Intent
import android.view.View
import androidx.lifecycle.ViewModel
import com.simprints.simprintsidtester.fragments.LiveMessageEvent
import com.simprints.simprintsidtester.fragments.ui.ViewModelForAdapter
import com.simprints.simprintsidtester.model.domain.IntentArgument
import com.simprints.simprintsidtester.model.domain.SimprintsIntent
import com.simprints.simprintsidtester.model.domain.toIntent
import com.simprints.simprintsidtester.model.local.LocalSimprintsIntentDataSource
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject




class IntentEditViewModel : ViewModel(), KoinComponent,
    ViewModelForAdapter {

    companion object {
        const val REQUEST_CODE = 1
    }

    val viewEditEvents = LiveMessageEvent<ViewEditIntentEvents>()
    lateinit var intent: SimprintsIntent

    private val intentsDao: LocalSimprintsIntentDataSource by inject()

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

    private fun addPlaceholderIfNecessary(notifyToAdapter: Boolean = true){
        val empty = intent.extra.filter { it.value.isEmpty() && it.key.isEmpty() }
        if(empty.isEmpty()) {
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
                startActivityForResult(intent.toIntent(), REQUEST_CODE)
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
    }

    fun userDidWantToDeleteIntent(v: View) {
        GlobalScope.launch {
            intentsDao.delete(intent)
        }
        viewEditEvents.sendEvent { finish() }
    }

    private fun updateSimprintsIntent(intent: SimprintsIntent) =
        GlobalScope.launch {
            intentsDao.update(intent.copy(extra = intent.extra.filter { it.key.isNotEmpty() }.toMutableList()))
        }

    interface ViewEditIntentEvents {
        fun startActivityForResult(intent: Intent?, requestCode: Int)
        fun finish()
        fun notifyIntentArgumentAdded(position: Int)
    }
}