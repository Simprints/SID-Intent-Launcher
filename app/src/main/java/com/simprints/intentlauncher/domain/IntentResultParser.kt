package com.simprints.intentlauncher.domain

import android.os.Bundle
import com.google.gson.Gson
import javax.inject.Inject

class IntentResultParser @Inject constructor(
    private val gson: Gson,
) {

    operator fun invoke(
        resultCode: Int,
        resultExtras: Bundle?,
    ): IntentResult {
        val resultJson = gson.toJson(resultExtras)

        return IntentResult(
            code = resultCode,
            json = resultJson,
            sessionId = resultExtras?.getString("sessionId", null),
        )
    }
}
