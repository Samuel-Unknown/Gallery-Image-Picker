package com.samuelunknown.sample

import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.samuelunknown.library.presentation.imageLoader.ImageLoader
import com.samuelunknown.library.presentation.imageLoader.ImageLoaderFactory

class ImageLoaderFactoryGlideImpl : ImageLoaderFactory {
    override fun create(): ImageLoader = object : ImageLoader {
        override fun load(imageView: ImageView, uri: Uri) {
            Glide.with(imageView)
                .load(uri)
                .transform(
                    MultiTransformation(
                        CenterCrop(),
                        RoundedCorners(20)
                    )
                )
                .into(imageView)
        }

        override fun cancel(imageView: ImageView) {
            Glide.with(imageView).clear(imageView)
        }
    }
}