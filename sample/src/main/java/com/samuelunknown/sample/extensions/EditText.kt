package com.samuelunknown.sample.extensions

import android.widget.EditText

fun EditText.setTextIfItDoesNotMatch(text: String): Boolean {
    return if (this.text?.toString() != text) {
        this.setText(text)
        true
    } else false
}