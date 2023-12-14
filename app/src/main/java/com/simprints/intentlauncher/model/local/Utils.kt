package com.simprints.intentlauncher.model.local

import com.simprints.intentlauncher.model.domain.IntentArgument
import com.simprints.intentlauncher.model.domain.SimprintsIntent

val defaultIntentList = listOf(
    LocalSimprintsIntent(
        intent = SimprintsIntent(
            name = "Enrol in SID",
            action = "com.simprints.id.REGISTER",
            extra = mutableListOf(
                IntentArgument(key = "projectId", value = "someProjectId"),
                IntentArgument(key = "userId", value = "someUserId"),
                IntentArgument(key = "moduleId", value = "someModuleId")
            )
        )
    ),
    LocalSimprintsIntent(
        intent = SimprintsIntent(
            name = "Identify in SID",
            action = "com.simprints.id.IDENTIFY",
            extra = mutableListOf(
                IntentArgument(key = "projectId", value = "someProjectId"),
                IntentArgument(key = "userId", value = "someUserId"),
                IntentArgument(key = "moduleId", value = "someModuleId")
            )
        )
    ),
    LocalSimprintsIntent(
        intent = SimprintsIntent(
            name = "Confirm in SID",
            action = "com.simprints.id.CONFIRM_IDENTITY",
            extra = mutableListOf(
                IntentArgument(key = "projectId", value = "someProjectId"),
                IntentArgument(key = "userId", value = "someUserId"),
                IntentArgument(key = "moduleId", value = "someModuleId"),
                IntentArgument(key = "selectedGuid", value = "someGuid")
            )
        )
    ),
    LocalSimprintsIntent(
        intent = SimprintsIntent(
            name = "Verify in SID",
            action = "com.simprints.id.VERIFY",
            extra = mutableListOf(
                IntentArgument(key = "projectId", value = "someProjectId"),
                IntentArgument(key = "userId", value = "someUserId"),
                IntentArgument(key = "moduleId", value = "someModuleId"),
                IntentArgument(key = "verifyGuid", value = "someGuid")
            )
        )
    )
)
