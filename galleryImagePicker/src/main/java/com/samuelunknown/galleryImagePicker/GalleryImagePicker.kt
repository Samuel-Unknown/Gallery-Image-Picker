package com.samuelunknown.galleryImagePicker

import com.samuelunknown.galleryImagePicker.presentation.imageLoader.ImageLoaderFactory
import com.samuelunknown.galleryImagePicker.presentation.imageLoader.ImageLoaderFactoryHolder

object GalleryImagePicker {
    fun init(factory: ImageLoaderFactory) {
        ImageLoaderFactoryHolder.init(factory)
    }
}