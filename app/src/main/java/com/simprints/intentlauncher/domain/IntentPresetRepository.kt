package com.simprints.intentlauncher.domain

import com.simprints.intentlauncher.data.db.IntentPresetDao
import com.simprints.intentlauncher.data.db.IntentPresetEntity
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject

class IntentPresetRepository @Inject constructor(
    private val dao: IntentPresetDao,
) {

    suspend fun save(name: String, fields: IntentFields) = dao.save(
        IntentPresetEntity(
            name = name,
            timestamp = Date().toString(),
            projectId = fields.projectId,
            moduleId = fields.moduleId,
            userId = fields.userId,
        )
    )

    fun getPresets() = dao.getAll().map { entities ->
        entities.map { Preset(it.id, it.name, IntentFields(it.projectId, it.moduleId, it.userId)) }
    }

    suspend fun deletePreset(id: String) {
        dao.delete(id)
    }
}
