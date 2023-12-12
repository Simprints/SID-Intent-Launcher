package com.simprints.intentlauncher.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.simprints.intentlauncher.model.domain.IntentArgument
import com.simprints.intentlauncher.model.domain.SimprintsIntent
import java.util.*

@Entity
data class LocalSimprintsIntent(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val action: String,
    val extra: MutableList<IntentArgument>
) {

    constructor(intent: SimprintsIntent) : this(
        id = intent.id,
        name = intent.name,
        action = intent.action,
        extra = intent.extra
    )
}

fun LocalSimprintsIntent.toDomainClass() = SimprintsIntent(id, name, action, extra)