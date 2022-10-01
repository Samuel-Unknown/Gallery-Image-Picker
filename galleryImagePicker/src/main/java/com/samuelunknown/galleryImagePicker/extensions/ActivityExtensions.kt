package com.samuelunknown.galleryImagePicker.extensions

import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentActivity
import androidx.window.layout.WindowMetricsCalculator

internal fun FragmentActivity.calculateScreenHeightWithoutSystemBars(
    callback: (height: Int, width: Int) -> Unit
) {
    window.decorView.doOnApplyWindowInsetsListenerCompat() { _, windowInsetsCompat ->
        val metrics = WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(this)
        val insets = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.systemBars())
        val height = metrics.bounds.height() - insets.bottom - insets.top
        val width = metrics.bounds.width() - insets.left - insets.right

        callback.invoke(height, width)
    }
}