package com.samuelunknown.galleryImagePickerGlide.imageLoaderFactory

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.samuelunknown.galleryImagePicker.presentation.imageLoader.ImageLoader
import com.samuelunknown.galleryImagePicker.presentation.imageLoader.ImageLoaderFactory
import com.samuelunknown.galleryImagePickerGlide.R
import kotlin.math.roundToInt

class ImageLoaderFactoryGlideImpl(private val appContext: Context) : ImageLoaderFactory {
    private val radius: Float =
        appContext.resources.getDimension(R.dimen.gallery_image_picker_glide__image_corner_radius)

    private val drawableCrossFadeFactory = DrawableCrossFadeFactory
        .Builder(DEFAULT_CROSS_FADE_DURATION_IN_MILLIS)
        .setCrossFadeEnabled(true)
        .build()

    private val transformation = MultiTransformation(
        CenterCrop(),
        RoundedCorners(radius.roundToInt())
    )

    override fun create(): ImageLoader = object : ImageLoader {
        override fun load(imageView: ImageView, uri: Uri) {
            Glide.with(appContext)
                .load(uri)
                .apply(RequestOptions().override(imageView.width, imageView.height))
                .transition(withCrossFade(drawableCrossFadeFactory))
                .transform(transformation)
                .placeholder(R.drawable.gallery_image_picker_glide__bg_placeholder)
                .into(imageView)
        }

        override fun cancel(imageView: ImageView) {
            Glide.with(appContext).clear(imageView)
        }
    }

    private companion object {
        const val DEFAULT_CROSS_FADE_DURATION_IN_MILLIS = 100
    }
}