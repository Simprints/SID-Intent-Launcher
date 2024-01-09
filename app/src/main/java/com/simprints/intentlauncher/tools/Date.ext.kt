package com.simprints.intentlauncher.tools

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

private const val ISO_8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'"

fun Date.toIsoString(): String = SimpleDateFormat(ISO_8601_FORMAT, Locale.US)
    .also { it.timeZone = TimeZone.getTimeZone("UTC") }
    .format(this)
