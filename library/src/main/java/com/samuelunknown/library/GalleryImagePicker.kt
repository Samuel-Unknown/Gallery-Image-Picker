package com.samuelunknown.library

import com.samuelunknown.library.presentation.imageLoader.ImageLoaderFactory
import com.samuelunknown.library.presentation.imageLoader.ImageLoaderFactoryHolder

object GalleryImagePicker {
    fun init(factory: ImageLoaderFactory) {
        ImageLoaderFactoryHolder.init(factory)
    }
}