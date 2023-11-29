package com.simprints.simprintsidtester.fragments.integration.model

import android.os.Parcelable
import com.simprints.simprintsidtester.model.domain.IFingerIdentifier
import kotlinx.parcelize.Parcelize

@Parcelize
data class FaceSample(
    val template: ByteArray,
    val format: String
) : Parcelable

@Parcelize
data class FingerprintSample(
    val fingerIdentifier: IFingerIdentifier,
    val template: ByteArray,
    val templateQualityScore: Int,
    val format: String
) : Parcelable