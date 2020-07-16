package com.simprints.simprintsidtester.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.simprints.simprintsidtester.model.domain.SimprintsResult
import java.util.*

@Entity
data class LocalSimprintsResult(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val dateTimeSent: String,
    val intentSent: String,
    val resultReceived: String
) {
    fun toDomainClass(): SimprintsResult =
        SimprintsResult(id, dateTimeSent, intentSent, resultReceived)

    companion object {
        fun fromSimprintsResult(simprintsResult: SimprintsResult) =
            LocalSimprintsResult(
                simprintsResult.id,
                simprintsResult.dateTimeSent,
                simprintsResult.intentSent,
                simprintsResult.resultReceived
            )
    }
}

