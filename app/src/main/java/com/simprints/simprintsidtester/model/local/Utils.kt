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
            name = "Enrol",
            action = "com.simprints.id.REGISTER",
            extra = mutableListOf(
                IntentArgument(key = "projectId", value = "your-project-id-here"),
                IntentArgument(key = "userId", value = "your-user-id-here"),
                IntentArgument(key = "moduleId", value = "your-module-id-here")
            )
        )
    ),
    LocalSimprintsIntent(
        intent = SimprintsIntent(
            name = "Identify",
            action = "com.simprints.id.IDENTIFY",
            extra = mutableListOf(
                IntentArgument(key = "projectId", value = "your-project-id-here"),
                IntentArgument(key = "userId", value = "your-user-id-here"),
                IntentArgument(key = "moduleId", value = "your-module-id-here")
            )
        )
    ),
    LocalSimprintsIntent(
        intent = SimprintsIntent(
            name = "Confirm",
            action = "com.simprints.id.CONFIRM_IDENTITY",
            extra = mutableListOf(
                IntentArgument(key = "projectId", value = "your-project-id-here"),
                IntentArgument(key = "userId", value = "your-user-id-here"),
                IntentArgument(key = "moduleId", value = "your-module-id-here")
            )
        )
    ),
    LocalSimprintsIntent(
        intent = SimprintsIntent(
            name = "Verify",
            action = "com.simprints.id.VERIFY",
            extra = mutableListOf(
                IntentArgument(key = "projectId", value = "your-project-id-here"),
                IntentArgument(key = "userId", value = "your-user-id-here"),
                IntentArgument(key = "moduleId", value = "your-module-id-here")
            )
        )
    )
)

private val commonExtras = mutableListOf(
    IntentArgument(key = "projectId", value = "your-project-id-here"),
    IntentArgument(key = "userId", value = "your-user-id-here"),
    IntentArgument(key = "moduleId", value = "your-module-id-here")
)

private val confirmIdentityExtras = commonExtras.apply {
    add(IntentArgument(key = "selectedGuid", value = "guid-goes-here"))
}

private val verifyExtras = commonExtras.apply {
    add(IntentArgument(key = "verifyGuid", value = "guid-goes-here"))
}
