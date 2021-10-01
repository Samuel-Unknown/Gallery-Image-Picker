package com.samuelunknown.library.extensions

import android.graphics.Rect
import androidx.fragment.app.FragmentActivity

fun FragmentActivity.getScreenHeight(): Int {
    val rectangle = Rect()
    window.decorView.getWindowVisibleDisplayFrame(rectangle)
    return rectangle.height()
}