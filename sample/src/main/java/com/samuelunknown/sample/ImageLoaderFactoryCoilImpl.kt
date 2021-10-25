package com.samuelunknown.sample

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import coil.dispose
import coil.load
import coil.size.Scale
import coil.transform.RoundedCornersTransformation
import com.samuelunknown.galleryImagePicker.presentation.imageLoader.ImageLoader
import com.samuelunknown.sample.imageLoaderFactory.ImageLoaderFactoryBase

class ImageLoaderFactoryCoilImpl(appContext: Context) : ImageLoaderFactoryBase(appContext) {
    private val transformation = RoundedCornersTransformation(radius)

    override fun create(): ImageLoader = object : ImageLoader {
        override fun load(imageView: ImageView, uri: Uri) {
            imageView.load(uri) {
                size(imageView.width, imageView.height)
                placeholder(R.drawable.bg_placeholder)
                scale(Scale.FILL)
                transformations(transformation)
                crossfade(DEFAULT_CROSS_FADE_DURATION_IN_MILLIS)
            }
        }

        override fun cancel(imageView: ImageView) {
            imageView.dispose()
        }
    }
}