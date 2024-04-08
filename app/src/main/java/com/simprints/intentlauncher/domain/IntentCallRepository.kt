package com.simprints.intentlauncher.domain

import com.simprints.intentlauncher.data.db.IntentCallDao
import com.simprints.intentlauncher.data.db.IntentCallEntity
import com.simprints.intentlauncher.data.db.IntentFieldsEntity
import com.simprints.intentlauncher.data.db.IntentResultEntity
import com.simprints.intentlauncher.domain.IntentResult.Companion.INVALID_CUSTOM_INTENT
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class IntentCallRepository @Inject constructor(
    private val dao: IntentCallDao,
) {

    suspend fun save(
        call: IntentCall,
        result: IntentResult,
    ): IntentCall {

        dao.save(
            IntentCallEntity(
                id = call.id,
                timestamp = call.timestamp,
                intentAction = call.action,
                intentExtras = call.extra.asString(),
                fields = IntentFieldsEntity(
                    projectId = call.fields.projectId,
                    moduleId = call.fields.moduleId,
                    userId = call.fields.userId,
                ),
                result = IntentResultEntity(
                    code = result.code ?: INVALID_CUSTOM_INTENT,
                    json = result.json.orEmpty(),
                )
            )
        )

        return call.copy(
            result = result
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
