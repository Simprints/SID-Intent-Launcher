package com.simprints.intentlauncher.model.local

import android.os.Bundle
import com.google.gson.Gson
import com.simprints.intentlauncher.model.domain.IntentCall
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class IntentCallRepository @Inject constructor(
    private val dao: IntentCallDao,
    private val gson: Gson,
) {

    suspend fun save(
        call: IntentCall,
        resultCode: Int,
        resultExtras: Bundle?,
    ): IntentCall {
        val resultJson = gson.toJson(resultExtras)

        dao.save(
            IntentCallEntity(
                timestamp = call.timestamp,
                intentAction = call.action,
                intentExtras = call.extra.asString(),
                resultCode = resultCode,
                resultReceived = resultJson,
            )
        )

        return call.copy(
            resultCode = resultCode,
            resultReceived = resultJson,
            resultSessionId = resultExtras?.getString("sessionId", null),
        )
    }

    suspend fun getResult(id: String) = dao.get(id)?.let { entity ->
        entityToDomain(entity)
    }

    fun getAllResults() = dao.getAll().map { entities ->
        entities.map { entityToDomain(it) }
    }

    private fun entityToDomain(entity: IntentCallEntity) = IntentCall(
        timestamp = entity.timestamp,
        action = entity.intentAction,
        extra = entity.intentExtras.fromString(),
        resultCode = entity.resultCode,
        resultReceived = entity.resultReceived,
    )

    private fun Map<String, String>.asString(): String = entries
        .joinToString(separator = "|") { (key, value) -> "$key=$value" }

    private fun String.fromString(): Map<String, String> = split("|")
        .associate { entry ->
            val (key, value) = entry.split("=")
            key to value
        }
}
