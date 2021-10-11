package com.samuelunknown.galleryImagePicker.presentation.imageLoader

import android.net.Uri
import android.widget.ImageView

interface ImageLoader {
    fun load(imageView: ImageView, uri: Uri)
    fun cancel(imageView: ImageView)
}