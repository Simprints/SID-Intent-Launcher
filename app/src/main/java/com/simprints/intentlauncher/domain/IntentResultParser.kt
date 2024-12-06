package com.simprints.intentlauncher.domain

import android.os.Bundle
import com.google.gson.Gson
import com.simprints.libsimprints.contracts.SimprintsResponse
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

    operator fun invoke(
        response: SimprintsResponse,
    ): IntentResult {
        val resultJson = gson.toJson(response)

        return IntentResult(
            code = response.resultCode,
            json = resultJson,
            sessionId = response.sessionId,
        )
    }
}
