package com.simprints.intentlauncher.domain

import com.simprints.intentlauncher.data.db.IntentPresetDao
import com.simprints.intentlauncher.data.db.IntentPresetEntity
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
}
