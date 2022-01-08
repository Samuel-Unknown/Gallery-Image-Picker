package com.samuelunknown.galleryImagePicker

import com.samuelunknown.galleryImagePicker.presentation.imageLoader.ImageLoader
import com.samuelunknown.galleryImagePicker.presentation.imageLoader.ImageLoaderFactory
import com.samuelunknown.galleryImagePicker.presentation.imageLoader.ImageLoaderFactoryHolder

/**
 * Singleton for initialization the library
 */
object GalleryImagePicker {

    /**
     * Method for initialization the library
     * @param factory image loader factory which is used for [ImageLoader] creation
     */
    fun init(factory: ImageLoaderFactory) {
        ImageLoaderFactoryHolder.init(factory)
    }
}