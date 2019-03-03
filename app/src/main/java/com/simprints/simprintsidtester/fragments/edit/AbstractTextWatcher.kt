package com.simprints.simprintsidtester.fragments.edit

import android.text.Editable
import android.text.TextWatcher

abstract class AbstractTextWatcher : TextWatcher {

    override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
    }

    override fun beforeTextChanged(s: CharSequence, arg1: Int, arg2: Int, arg3: Int) {

    }

    override fun afterTextChanged(text: Editable) {

    }
}