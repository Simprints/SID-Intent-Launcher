package com.simprints.simprintsidtester.fragments.integration

import android.content.ActivityNotFoundException
import android.content.Intent
import android.util.Base64
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.simprints.libsimprints.SimHelper
import com.simprints.simprintsidtester.fragments.LiveMessageEvent
import com.simprints.simprintsidtester.fragments.integration.model.CoSyncItem
import com.simprints.simprintsidtester.fragments.integration.model.CoSyncTemplateType
import com.simprints.simprintsidtester.fragments.integration.model.FaceSample
import com.simprints.simprintsidtester.fragments.integration.model.FingerprintSample
import com.simprints.simprintsidtester.model.domain.CoSyncPayload
import com.simprints.simprintsidtester.model.domain.CoSyncTemplate
import com.simprints.simprintsidtester.model.domain.FaceCaptureBiometricsPayload
import com.simprints.simprintsidtester.model.domain.FingerprintCaptureBiometricsPayload
import com.simprints.simprintsidtester.model.domain.IFingerIdentifier
import com.simprints.simprintsidtester.model.domain.IntentArgument
import com.simprints.simprintsidtester.model.domain.SimprintsIntent
import com.simprints.simprintsidtester.model.domain.SimprintsResult
import com.simprints.simprintsidtester.model.local.LocalSimprintsResultDataSource
import com.simprints.simprintsidtester.model.store.ProjectDataCache
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.Date

class IntegrationViewModel(
    private val resultDao: LocalSimprintsResultDataSource,
    private val gson: Gson,
    private val projectDataCache: ProjectDataCache,
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
    var templates by mutableStateOf<List<CoSyncItem>>(emptyList())
        private set

    private val faceSamples: List<FaceSample>
        get() = templates.filter { it.isSelected && it.type == CoSyncTemplateType.Face }
            .map { item ->
                val template = (item.payload as CoSyncPayload<CoSyncTemplate.Face>).template
                return@map FaceSample(
                    template =  Base64.decode(template.template, Base64.NO_WRAP),
                    format = template.format
                )
            }
    private val fingerprintSamples: List<FingerprintSample>
        get() = templates.filter { it.isSelected && it.type == CoSyncTemplateType.Fingerprint }
            .map { item ->
                val template = (item.payload as CoSyncPayload<CoSyncTemplate.Fingerprint>).template
                return@map FingerprintSample(
                    template = Base64.decode(template.template, Base64.NO_WRAP),
                    fingerIdentifier = template.finger,
                    templateQualityScore = template.quality,
                    format = template.format
                )
            }

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

    fun onTemplateSelectionChanged(coSyncItem: CoSyncItem, isSelected: Boolean) {
        this.templates = this.templates.map { item ->
            if (item.payload.id == coSyncItem.payload.id) {
                item.copy(isSelected = isSelected)
            } else {
                item
            }
        }
    }

    // Intent Actions

    val viewEditEvents = LiveMessageEvent<IntegrationIntentEvents>()
    private var lastIntentSentTime: Date? = null
    private var lastSentIntent: SimprintsIntent? = null

    fun enroll() = viewEditEvents.sendEvent {
        SimHelper(projectId, userId).register(moduleId)
            .also { cacheIntent(it) }
            .let { tryStartIntent(this, it) }
    }

    fun verify() = viewEditEvents.sendEvent {
        SimHelper(projectId, userId).verify(moduleId, guid)
            .also { intent ->
                val gson = Gson()
                faceSamples.takeUnless { it.isEmpty() }
                    ?.let { intent.putExtra("COSYNC_FACE_SAMPLES", gson.toJson(it)) }
                fingerprintSamples.takeUnless { it.isEmpty() }
                    ?.let { intent.putExtra("COSYNC_FINGERPRINT_SAMPLES", gson.toJson(it)) }
            }
            .also { cacheIntent(it) }
            .let { tryStartIntent(this, it) }
    }

    fun identify() = viewEditEvents.sendEvent {
        SimHelper(projectId, userId).identify(moduleId)
            .also { cacheIntent(it) }
            .let { tryStartIntent(this, it) }
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

    private fun tryStartIntent(events: IntegrationIntentEvents, intent: Intent) {
        try {
            events.startActivityForResult(intent, REQUEST_CODE)
        } catch (e: ActivityNotFoundException) {
            result = "SID is not installed"
        }
    }

    fun saveResult(resultCode: Int, data: Intent?) {
        val result = "$resultCode \n ${gson.toJson(data?.extras)}"
        this.result = result
        this.templates = try {
            data.getCoSyncTemplates()
        } catch (e: Exception) {
            print("tfk $e")
            emptyList()
        }

        viewModelScope.launch {
            val simprintsResult = SimprintsResult(
                dateTimeSent = lastIntentSentTime.toString(),
                intentSent = gson.toJson(lastSentIntent),
                resultReceived = result
            )
            resultDao.update(simprintsResult)
        }
    }

    // Data cached actions

    fun loadCachedState() {
        viewModelScope.launch {
            projectId = projectDataCache.getProjectId()
            userId = projectDataCache.getUserId()
            moduleId = projectDataCache.getModuleId()
            guid = projectDataCache.getGuid()
        }
    }

    fun save() {
        viewModelScope.launch { projectDataCache.save(projectId, userId, moduleId, guid) }
    }

    fun clear() {
        viewModelScope.launch { projectDataCache.clear() }
        projectId = ""
        userId = ""
        moduleId = ""
        guid = ""
        templates = emptyList()
    }

    private fun Intent?.getCoSyncTemplates(): List<CoSyncItem> {
        if (this?.extras == null) return emptyList()
        if (extras?.containsKey("verification") == true) return emptyList() // no need for templates from verification

        val eventMap: HashMap<String, Any> = Gson().fromJson(
            extras?.getString("events").orEmpty(),
            object : TypeToken<HashMap<String, Any>>() {}.type
        )
        val faceTemplates =
            getPayload(
                eventMap = eventMap,
                typeKey = CoSyncTemplate.bundleKeyFace,
                clazz = FaceCaptureBiometricsPayload::class.java
            )
                .mapIndexed { index, facePayload ->
                    CoSyncItem(
                        payload = CoSyncPayload.fromPayload(facePayload),
                        isSelected = false,
                        faceId = index
                    )
                }
        val fingerTemplates = getPayload(
            eventMap = eventMap,
            typeKey = CoSyncTemplate.bundleKeyFinger,
            clazz = FingerprintCaptureBiometricsPayload::class.java
        ).map { CoSyncItem(payload = CoSyncPayload.fromPayload(it), isSelected = false) }
        return faceTemplates + fingerTemplates
    }

    private fun <T> getPayload(
        eventMap: Map<String, Any>,
        typeKey: String,
        clazz: Class<T>
    ): List<T> =
        (eventMap["events"] as ArrayList<Map<String, Any>>).filter {
            (it["type"] as String) == typeKey
        }.mapNotNull {
            try {
                Gson().fromJson(JSONObject(it["payload"] as Map<String, Any>).toString(), clazz)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

    interface IntegrationIntentEvents {

        fun startActivityForResult(intent: Intent?, requestCode: Int)
    }

    companion object {
        const val REQUEST_CODE = 1
    }
}
