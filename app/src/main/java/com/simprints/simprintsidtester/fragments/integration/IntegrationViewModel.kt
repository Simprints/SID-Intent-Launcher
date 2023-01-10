package com.simprints.simprintsidtester.fragments.integration

import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.simprints.libsimprints.SimHelper
import com.simprints.simprintsidtester.fragments.LiveMessageEvent
import com.simprints.simprintsidtester.model.domain.IntentArgument
import com.simprints.simprintsidtester.model.domain.SimprintsIntent
import com.simprints.simprintsidtester.model.domain.SimprintsResult
import com.simprints.simprintsidtester.model.local.LocalSimprintsResultDataSource
import kotlinx.coroutines.launch
import java.util.*

class IntegrationViewModel(
    private val resultDao: LocalSimprintsResultDataSource,
    private val gson: Gson,
) : ViewModel() {

    // UI State

    var projectId by mutableStateOf("")
        private set

    var userId by mutableStateOf("")
        private set

    var moduleId by mutableStateOf("")
        private set

    var guid by mutableStateOf("")
        private set

    var result by mutableStateOf("")
        private set

    fun updateProjectId(projectId: String) {
        this.projectId = projectId
    }

    fun updateUserId(userId: String) {
        this.userId = userId
    }

    fun updateModuleId(moduleId: String) {
        this.moduleId = moduleId
    }

    fun updateGuid(guid: String) {
        this.guid = guid
    }

    // Intent Actions

    val viewEditEvents = LiveMessageEvent<IntegrationIntentEvents>()
    private var lastIntentSentTime: Date? = null
    private var lastSentIntent: SimprintsIntent? = null

    fun enroll() = viewEditEvents.sendEvent {
        SimHelper(projectId, userId).register(moduleId)
            .also { cacheIntent(it) }
            .let { startActivityForResult(it, REQUEST_CODE) }
    }

    fun verify() = viewEditEvents.sendEvent {
        SimHelper(projectId, userId).verify(moduleId, guid)
            .also { cacheIntent(it) }
            .let { startActivityForResult(it, REQUEST_CODE) }
    }

    fun identify() = viewEditEvents.sendEvent {
        SimHelper(projectId, userId).identify(moduleId)
            .also { cacheIntent(it) }
            .let { startActivityForResult(it, REQUEST_CODE) }
    }

    private fun cacheIntent(intent: Intent) {
        lastIntentSentTime = Date()
        lastSentIntent = SimprintsIntent(
            name = intent.action.orEmpty(),
            action = intent.action.orEmpty(),
            extra = intent.extras
                ?.let { extras ->
                    extras.keySet()
                        .mapNotNull { key ->
                            // Assuming all provided values are strings, cos they are
                            extras.getString(key)?.let { IntentArgument(key, it) }
                        }
                        .toMutableList()
                }
                ?: mutableListOf(),
        )
    }

    fun saveResult(resultCode: Int, data: Intent?) {
        val result = "$resultCode \n ${gson.toJson(data?.extras)}"
        this.result = result

        viewModelScope.launch {
            val simprintsResult = SimprintsResult(
                dateTimeSent = lastIntentSentTime.toString(),
                intentSent = gson.toJson(lastSentIntent),
                resultReceived = result
            )
            resultDao.update(simprintsResult)
        }
    }

    interface IntegrationIntentEvents {

        fun startActivityForResult(intent: Intent?, requestCode: Int)
    }

    companion object {

        const val REQUEST_CODE = 1
    }
}
