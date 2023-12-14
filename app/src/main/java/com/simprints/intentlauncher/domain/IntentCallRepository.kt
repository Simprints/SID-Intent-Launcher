package com.simprints.intentlauncher.domain

import android.os.Bundle
import com.google.gson.Gson
import com.simprints.intentlauncher.data.db.IntentCallDao
import com.simprints.intentlauncher.data.db.IntentCallEntity
import com.simprints.intentlauncher.data.db.IntentFieldsEntity
import com.simprints.intentlauncher.data.db.IntentResultEntity
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
                fields = IntentFieldsEntity(
                    projectId = call.fields.projectId,
                    moduleId = call.fields.moduleId,
                    userId = call.fields.userId,
                ),
                result = IntentResultEntity(
                    code = resultCode,
                    json = resultJson,
                )
            )
        )

        return call.copy(
            result = IntentResult(
                code = resultCode,
                json = resultJson,
                sessionId = resultExtras?.getString("sessionId", null),
            )
        )
    }

    suspend fun getIntentCall(id: String) = dao.get(id)?.let { entity ->
        entityToDomain(entity)
    }

    fun getAllIntentCalls() = dao.getAll().map { entities ->
        entities.map { entityToDomain(it) }
    }

    suspend fun deleteIntentCall(id: String) = dao.delete(id)

    private fun entityToDomain(entity: IntentCallEntity) = IntentCall(
        id = entity.id,
        timestamp = entity.timestamp,
        action = entity.intentAction,
        extra = entity.intentExtras.fromString(),
        fields = IntentFields(
            projectId = entity.fields.projectId,
            moduleId = entity.fields.moduleId,
            userId = entity.fields.userId,
        ),
        result = entity.result?.let { result ->
            IntentResult(
                code = result.code,
                json = result.json,
            )
        }
    )

    private fun Map<String, String>.asString(): String = entries
        .joinToString(separator = "|") { (key, value) -> "$key=$value" }

    private fun String.fromString(): Map<String, String> = split("|")
        .associate { entry ->
            val (key, value) = entry.split("=")
            key to value
        }
}
