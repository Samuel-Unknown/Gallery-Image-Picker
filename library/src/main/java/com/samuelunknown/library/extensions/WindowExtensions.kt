package com.samuelunknown.library.extensions

import android.view.Window

private const val VISIBLE_DIM = 0.5f
private const val INVISIBLE_DIM = 0.0f

fun Window.setDimVisibility(isVisible: Boolean) {
    setDimAmount(if (isVisible) VISIBLE_DIM else INVISIBLE_DIM)
}