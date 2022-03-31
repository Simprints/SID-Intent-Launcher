package com.simprints.simprintsidtester.model.local

import com.simprints.simprintsidtester.model.domain.IntentArgument
import com.simprints.simprintsidtester.model.domain.SimprintsIntent
import java.util.concurrent.Executors

private val IO_EXECUTOR = Executors.newSingleThreadExecutor()

/**
 * Utility method to run blocks on a dedicated background thread, used for io/database work.
 */
fun ioThread(f: () -> Unit) {
    IO_EXECUTOR.execute(f)
}

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

