package com.simprints.simprintsidtester.fragments.integration.model

import com.simprints.simprintsidtester.model.domain.CoSyncPayload
import com.simprints.simprintsidtester.model.domain.CoSyncTemplate

data class CoSyncItem(
    val payload: CoSyncPayload<out CoSyncTemplate>,
    val isSelected: Boolean,
    val faceId: Int = -1 // Fingerprint templates have identifiers, faces don't. Keeping it here
) {
    val type: CoSyncTemplateType
        get() = when (payload.template) {
            is CoSyncTemplate.Face -> CoSyncTemplateType.Face
            is CoSyncTemplate.Fingerprint -> CoSyncTemplateType.Fingerprint
        }
}

enum class CoSyncTemplateType {
    Fingerprint, Face
}