package com.simprints.intentlauncher.ui.events.model

import androidx.compose.ui.graphics.vector.ImageVector

enum class EventSortingOption {
    DateAsc, AsReceivedInResponse
}

data class EventSortingItem(
    val text: String,
    val secondaryText: String,
    val icon: ImageVector
)