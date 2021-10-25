package com.samuelunknown.sample.imageLoaderFactory

import android.content.Context
import com.samuelunknown.galleryImagePicker.presentation.imageLoader.ImageLoaderFactory
import com.samuelunknown.sample.R

abstract class ImageLoaderFactoryBase(appContext: Context) : ImageLoaderFactory {
    protected val radius: Float = appContext.resources.getDimension(R.dimen.image_corner_radius)

    protected companion object {
        const val DEFAULT_CROSS_FADE_DURATION_IN_MILLIS = 100
    }
}