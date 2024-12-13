package com.simprints.intentlauncher.domain

import com.simprints.intentlauncher.data.db.IntentPresetDao
import com.simprints.intentlauncher.data.db.IntentPresetEntity
import com.simprints.intentlauncher.tools.toIsoString
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject

class IntentPresetRepository @Inject constructor(
    private val dao: IntentPresetDao,
) {
    suspend fun save(
        name: String,
        fields: IntentFields,
    ) = dao.save(
        IntentPresetEntity(
            name = name,
            timestamp = Date().toIsoString(),
            projectId = fields.projectId,
            moduleId = fields.moduleId,
            userId = fields.userId,
            metadata = fields.metadata,
        ),
    )

    fun getPresets() = dao.getAll().map { entities ->
        entities.map {
            Preset(
                id = it.id,
                name = it.name,
                fields = IntentFields(
                    projectId = it.projectId,
                    moduleId = it.moduleId,
                    userId = it.userId,
                    metadata = it.metadata,
                ),
            )
        }
    }

    suspend fun deletePreset(id: String) {
        dao.delete(id)
    }
}
