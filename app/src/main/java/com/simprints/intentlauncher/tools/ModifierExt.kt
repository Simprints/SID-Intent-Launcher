package com.simprints.intentlauncher.tools

import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

@Stable
fun Modifier.testAccessibleTag(tag: String) = this.testTag("android:id/$tag")
