package com.simprints.intentlauncher.ui.events.model

import com.simprints.intentlauncher.ui.intent.model.ResponseEvent

internal val eventMock = ResponseEvent(
    id = "738299cd2-389f-a9ce-3d11a3948ce",
    type = "MOCKED_EVENT",
    scopeId = "scope id",
    projectId = "project id",
    createdAtMs = 0,
    payload = mapOf(
        "key 1" to "value 1",
        "key 2" to listOf("list value one", "list value two"),
        "key 3" to mapOf(
            "nested key 1" to "nested value 1",
            "nested key 2" to mapOf(
                "second nested key 1" to "second nested value 1 with very long sentence",
                "second nested key 2" to "second nested value 2",
            )
        ),
        "key 4" to "value 4"
    )
)