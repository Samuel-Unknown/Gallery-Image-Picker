package com.samuelunknown.library.extensions

import android.view.View
import android.view.ViewGroup

internal fun View.updateMargins(
    startMargin: Int? = null,
    topMargin: Int? = null,
    endMargin: Int? = null,
    bottomMargin: Int? = null
) {
    val params = this.layoutParams as ViewGroup.MarginLayoutParams
    startMargin?.let { params.marginStart = it }
    topMargin?.let { params.topMargin = it }
    endMargin?.let { params.marginEnd = it }
    bottomMargin?.let { params.bottomMargin = it }
    this.layoutParams = params
}

internal fun View.updateHeight(height: Int) {
    val params = this.layoutParams as ViewGroup.LayoutParams
    params.height = height
    this.layoutParams = params
}