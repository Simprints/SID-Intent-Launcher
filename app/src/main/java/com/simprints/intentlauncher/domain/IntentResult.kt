package com.simprints.intentlauncher.domain

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
        INVALID_CUSTOM_INTENT -> "INVALID CUSTOM INTENT"
        Activity.RESULT_OK -> "OK"
        else -> getConstantNameByValue(code)
    }

    private fun getConstantNameByValue(value: Int?): String {
        Constants::class.java.fields.forEach { field ->
            try {
                if (field.getInt(null) == value) {
                    return field.name
                }
            } catch (e: Exception) {
                // Do nothing and go to fallback
            }
        }
        return value.toString() // Fallback if no matching constant is found
    }

    fun simpleText(): String = "${mappedResultCode()}\n$json"

    companion object {

        const val INVALID_CUSTOM_INTENT = 9999
    }

}
