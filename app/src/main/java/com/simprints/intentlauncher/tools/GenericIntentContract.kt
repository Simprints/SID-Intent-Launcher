package com.simprints.intentlauncher.tools

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract

class GenericIntentContract : ActivityResultContract<Intent, Pair<Int, Intent?>>() {

    override fun createIntent(context: Context, input: Intent): Intent = input

    override fun parseResult(resultCode: Int, intent: Intent?): Pair<Int, Intent?> =
        resultCode to intent
}