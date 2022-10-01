package com.samuelunknown.galleryImagePickerCoil.imageLoaderFactory

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import androidx.annotation.Px
import coil.dispose
import coil.load
import coil.size.Scale
import coil.transform.RoundedCornersTransformation
import com.samuelunknown.galleryImagePicker.presentation.imageLoader.ImageLoader
import com.samuelunknown.galleryImagePicker.presentation.imageLoader.ImageLoaderFactory
import com.samuelunknown.galleryImagePickerCoil.R

class ImageLoaderFactoryCoilImpl(appContext: Context) : ImageLoaderFactory {
    private val radius: Float =
        appContext.resources.getDimension(R.dimen.gallery_image_picker_coil__image_corner_radius)

    private val transformation = RoundedCornersTransformation(radius)

    override fun create(): ImageLoader = object : ImageLoader {
        override fun load(imageView: ImageView, uri: Uri, @Px imageSizeInPixels: Int) {
            imageView.load(uri) {
                size(imageSizeInPixels, imageSizeInPixels)
                placeholder(R.drawable.gallery_image_picker_coil__bg_placeholder)
                scale(Scale.FILL)
                transformations(transformation)
                crossfade(DEFAULT_CROSS_FADE_DURATION_IN_MILLIS)
            }
        }

        override fun cancel(imageView: ImageView) {
            imageView.dispose()
        }
    }

    private companion object {
        const val DEFAULT_CROSS_FADE_DURATION_IN_MILLIS = 100
    }
}