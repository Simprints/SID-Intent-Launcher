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
                IntentArgument(key = "projectId", value = "project-id"),
                IntentArgument(key = "userId", value = "user-id"),
                IntentArgument(key = "moduleId", value = "module-id")
            )
        )
    ),
    LocalSimprintsIntent(
        intent = SimprintsIntent(
            name = "Identify in SID",
            action = "com.simprints.id.IDENTIFY",
            extra = mutableListOf(
                IntentArgument(key = "projectId", value = "project-id"),
                IntentArgument(key = "userId", value = "user-id"),
                IntentArgument(key = "moduleId", value = "module-id")
            )
        )
    ),
    LocalSimprintsIntent(
        intent = SimprintsIntent(
            name = "Confirm in SID",
            action = "com.simprints.id.CONFIRM_IDENTITY",
            extra = mutableListOf(
                IntentArgument(key = "projectId", value = "project-id"),
                IntentArgument(key = "userId", value = "user-id"),
                IntentArgument(key = "moduleId", value = "module-id")
            )
        )
    ),
    LocalSimprintsIntent(
        intent = SimprintsIntent(
            name = "Verify in SID",
            action = "com.simprints.id.VERIFY",
            extra = mutableListOf(
                IntentArgument(key = "projectId", value = "project-id"),
                IntentArgument(key = "userId", value = "user-id"),
                IntentArgument(key = "moduleId", value = "module-id")
            )
        )
    )
)

private val commonExtras = mutableListOf(
    IntentArgument(key = "projectId", value = "project-id"),
    IntentArgument(key = "userId", value = "user-id"),
    IntentArgument(key = "moduleId", value = "module-id")
)

private val confirmIdentityExtras = commonExtras.apply {
    add(IntentArgument(key = "selectedGuid", value = "guid-here"))
}

private val verifyExtras = commonExtras.apply {
    add(IntentArgument(key = "verifyGuid", value = "guid-here"))
}
