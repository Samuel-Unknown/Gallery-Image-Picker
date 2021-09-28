package com.samuelunknown.library.extensions

import android.view.View
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * Set an [OnApplyWindowInsetsListener] to take over the policy for applying
 * window insets to this view. This will only take effect on devices with API 21 or above.
 */
fun View.doOnApplyWindowInsetsListenerCompat(
    autoClearListener: Boolean = true,
    listener: (View, WindowInsetsCompat) -> Unit
) {
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        listener(v, insets)
        if (autoClearListener) {
            ViewCompat.setOnApplyWindowInsetsListener(this, null)
        }
        insets
    }

    requestApplyInsetsWhenAttached()
}

private fun View.requestApplyInsetsWhenAttached() {
    if (isAttachedToWindow) {
        requestApplyInsets()
    } else {
        addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                v.removeOnAttachStateChangeListener(this)
                v.requestApplyInsets()
            }

            override fun onViewDetachedFromWindow(v: View) = Unit
        })
    }
}