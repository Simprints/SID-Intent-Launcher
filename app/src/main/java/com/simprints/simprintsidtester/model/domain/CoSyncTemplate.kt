package com.simprints.simprintsidtester.model.domain

data class FaceCaptureBiometricsPayload(
    val id: String,
    val createdAt: Long,
    val eventVersion: Int,
    val face: Face,
    val endedAt: Long,
    val type: String
) {
    data class Face(
        val yaw: Float,
        val roll: Float,
        val template: String,
        val quality: Float,
        val format: String
    )
}

data class FingerprintCaptureBiometricsPayload(
    val createdAt: Long,
    val eventVersion: Int,
    val fingerprint: Fingerprint,
    val id: String,
    val type: String,
    val endedAt: Long
) {

    data class Fingerprint(
        val finger: IFingerIdentifier,
        val template: String,
        val quality: Int,
        val format: String
    )
}

enum class IFingerIdentifier {
    RIGHT_5TH_FINGER,
    RIGHT_4TH_FINGER,
    RIGHT_3RD_FINGER,
    RIGHT_INDEX_FINGER,
    RIGHT_THUMB,
    LEFT_THUMB,
    LEFT_INDEX_FINGER,
    LEFT_3RD_FINGER,
    LEFT_4TH_FINGER,
    LEFT_5TH_FINGER
}

data class CoSyncPayload<T : CoSyncTemplate>(
    val createdAt: Long,
    val eventVersion: Int,
    val id: String,
    val type: String,
    val endedAt: Long,
    val template: T
) {
    companion object {
        fun fromPayload(payload: FaceCaptureBiometricsPayload): CoSyncPayload<CoSyncTemplate.Face> =
            CoSyncPayload(
                createdAt = payload.createdAt,
                eventVersion = payload.eventVersion,
                id = payload.id,
                type = payload.type,
                endedAt = payload.endedAt,
                template = CoSyncTemplate.Face(
                    yaw = payload.face.yaw,
                    roll = payload.face.roll,
                    template = payload.face.template,
                    quality = payload.face.quality,
                    format = payload.face.format,
                )
            )

        fun fromPayload(payload: FingerprintCaptureBiometricsPayload): CoSyncPayload<CoSyncTemplate.Fingerprint> =
            CoSyncPayload(
                createdAt = payload.createdAt,
                eventVersion = payload.eventVersion,
                id = payload.id,
                type = payload.type,
                endedAt = payload.endedAt,
                template = CoSyncTemplate.Fingerprint(
                    finger = payload.fingerprint.finger,
                    template = payload.fingerprint.template,
                    quality = payload.fingerprint.quality,
                    format = payload.fingerprint.format,
                )
            )

    }
}

sealed class CoSyncTemplate {
    data class Fingerprint(
        val finger: IFingerIdentifier,
        val template: String,
        val quality: Int,
        val format: String
    ) : CoSyncTemplate()

    data class Face(
        val yaw: Float,
        val roll: Float,
        val template: String,
        val quality: Float,
        val format: String
    ) : CoSyncTemplate()

    companion object {
        const val bundleKeyFace = "FACE_CAPTURE_BIOMETRICS"
        const val bundleKeyFinger = "FINGERPRINT_CAPTURE_BIOMETRICS"
    }
}
