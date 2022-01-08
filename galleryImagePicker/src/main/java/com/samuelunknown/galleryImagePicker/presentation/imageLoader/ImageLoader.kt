package com.samuelunknown.galleryImagePicker.presentation.imageLoader

import android.net.Uri
import android.widget.ImageView

/**
 * Interface which is used for loading and cancel loading images for [ImageView]
 */
interface ImageLoader {
    /**
     * Load image
     * @param imageView [ImageView]
     * @param uri [Uri]
     */
    fun load(imageView: ImageView, uri: Uri)

    /**
     * Cancel image loading
     * @param imageView [ImageView]
     */
    fun cancel(imageView: ImageView)
}