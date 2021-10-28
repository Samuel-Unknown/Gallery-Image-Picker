package com.samuelunknown.sample.extensions

import android.widget.CheckBox

fun CheckBox.setIsCheckedIfItDoesNotMatch(isChecked: Boolean) {
    if (this.isChecked != isChecked) {
        this.isChecked = isChecked
    }
}