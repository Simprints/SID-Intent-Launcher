package com.simprints.intentlauncher.model.domain

import android.app.Activity
import android.os.Parcelable
import com.simprints.libsimprints.Constants
import kotlinx.parcelize.Parcelize

@Parcelize
data class IntentResult(
    val code: Int? = null,
    val json: String? = null,
    val sessionId: String? = null,
) : Parcelable {

    fun mappedResultCode(): String = when (code) {
        null -> "No result code"
        Activity.RESULT_OK -> "OK"
        Constants.SIMPRINTS_CANCELLED -> "SIMPRINTS_CANCELLED"
        Constants.SIMPRINTS_MISSING_USER_ID -> "SIMPRINTS_MISSING_USER_ID"
        Constants.SIMPRINTS_MISSING_MODULE_ID -> "SIMPRINTS_MISSING_MODULE_ID"
        Constants.SIMPRINTS_INVALID_INTENT_ACTION -> "SIMPRINTS_INVALID_INTENT_ACTION"
        Constants.SIMPRINTS_INVALID_UPDATE_GUID -> "SIMPRINTS_INVALID_UPDATE_GUID"
        Constants.SIMPRINTS_MISSING_UPDATE_GUID -> "SIMPRINTS_MISSING_UPDATE_GUID"
        Constants.SIMPRINTS_MISSING_VERIFY_GUID -> "SIMPRINTS_MISSING_VERIFY_GUID"
        Constants.SIMPRINTS_INVALID_METADATA -> "SIMPRINTS_INVALID_METADATA"
        Constants.SIMPRINTS_VERIFY_GUID_NOT_FOUND_ONLINE -> "SIMPRINTS_VERIFY_GUID_NOT_FOUND_ONLINE"
        Constants.SIMPRINTS_VERIFY_GUID_NOT_FOUND_OFFLINE -> "SIMPRINTS_VERIFY_GUID_NOT_FOUND_OFFLINE"
        Constants.SIMPRINTS_INVALID_VERIFY_GUID -> "SIMPRINTS_INVALID_VERIFY_GUID"
        Constants.SIMPRINTS_INVALID_RESULT_FORMAT -> "SIMPRINTS_INVALID_RESULT_FORMAT"
        Constants.SIMPRINTS_INVALID_MODULE_ID -> "SIMPRINTS_INVALID_MODULE_ID"
        Constants.SIMPRINTS_INVALID_USER_ID -> "SIMPRINTS_INVALID_USER_ID"
        Constants.SIMPRINTS_INVALID_CALLING_PACKAGE -> "SIMPRINTS_INVALID_CALLING_PACKAGE"
        Constants.SIMPRINTS_MISSING_PROJECT_ID -> "SIMPRINTS_MISSING_PROJECT_ID"
        Constants.SIMPRINTS_INVALID_PROJECT_ID -> "SIMPRINTS_INVALID_PROJECT_ID"
        Constants.SIMPRINTS_DIFFERENT_PROJECT_ID -> "SIMPRINTS_DIFFERENT_PROJECT_ID"
        Constants.SIMPRINTS_DIFFERENT_USER_ID -> "SIMPRINTS_DIFFERENT_USER_ID"
        Constants.SIMPRINTS_ROOTED_DEVICE -> "SIMPRINTS_ROOTED_DEVICE"
        Constants.SIMPRINTS_UNEXPECTED_ERROR -> "SIMPRINTS_UNEXPECTED_ERROR"
        Constants.SIMPRINTS_BLUETOOTH_NOT_SUPPORTED -> "SIMPRINTS_BLUETOOTH_NOT_SUPPORTED"
        Constants.SIMPRINTS_INVALID_SELECTED_ID -> "SIMPRINTS_INVALID_SELECTED_ID"
        Constants.SIMPRINTS_INVALID_SESSION_ID -> "SIMPRINTS_INVALID_SESSION_ID"
        Constants.SIMPRINTS_LOGIN_NOT_COMPLETE -> "SIMPRINTS_LOGIN_NOT_COMPLETE"
        Constants.SIMPRINTS_INVALID_STATE_FOR_INTENT_ACTION -> "SIMPRINTS_INVALID_STATE_FOR_INTENT_ACTION"
        Constants.SIMPRINTS_ENROLMENT_LAST_BIOMETRICS_FAILED -> "SIMPRINTS_ENROLMENT_LAST_BIOMETRICS_FAILED"
        Constants.SIMPRINTS_FACE_LICENSE_MISSING -> "SIMPRINTS_FACE_LICENSE_MISSING"
        Constants.SIMPRINTS_FACE_LICENSE_INVALID -> "SIMPRINTS_FACE_LICENSE_INVALID"
        Constants.SIMPRINTS_SETUP_OFFLINE_DURING_MODALITY_DOWNLOAD -> "SIMPRINTS_SETUP_OFFLINE_DURING_MODALITY_DOWNLOAD"
        Constants.SIMPRINTS_SETUP_MODALITY_DOWNLOAD_CANCELLED -> "SIMPRINTS_SETUP_MODALITY_DOWNLOAD_CANCELLED"
        Constants.SIMPRINTS_FINGERPRINT_CONFIGURATION_ERROR -> "SIMPRINTS_FINGERPRINT_CONFIGURATION_ERROR"
        Constants.SIMPRINTS_FACE_CONFIGURATION_ERROR -> "SIMPRINTS_FACE_CONFIGURATION_ERROR"
        Constants.SIMPRINTS_BACKEND_MAINTENANCE_ERROR -> "SIMPRINTS_BACKEND_MAINTENANCE_ERROR"
        Constants.SIMPRINTS_PROJECT_PAUSED -> "SIMPRINTS_PROJECT_PAUSED"
        Constants.SIMPRINTS_PROJECT_ENDING -> "SIMPRINTS_PROJECT_ENDING"
        Constants.SIMPRINTS_BLUETOOTH_NO_PERMISSION -> "SIMPRINTS_BLUETOOTH_NO_PERMISSION"
        else -> code.toString()
    }

    fun simpleText(): String = "${mappedResultCode()}\n$json"

}
