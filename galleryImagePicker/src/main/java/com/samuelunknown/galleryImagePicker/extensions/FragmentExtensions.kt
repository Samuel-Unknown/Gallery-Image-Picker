package com.samuelunknown.galleryImagePicker.extensions

import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment

internal fun Fragment.initActionBar(
    toolbar: Toolbar,
    title: String? = null,
    subtitle: String? = null,
    @DrawableRes iconResId: Int? = null,
    displayHomeAsUpEnabled: Boolean? = null,
    navigationAction: (() -> Unit?)? = null
) {
    (requireActivity() as AppCompatActivity).apply {
        setSupportActionBar(toolbar)

        if (navigationAction != null) {
            toolbar.setNavigationOnClickListener { navigationAction.invoke() }
        }

        supportActionBar?.apply {
            displayHomeAsUpEnabled?.let { setDisplayHomeAsUpEnabled(it) }
            setTitle(title)
            setSubtitle(subtitle)
            iconResId?.let { setHomeAsUpIndicator(it) }
        }
    }
}