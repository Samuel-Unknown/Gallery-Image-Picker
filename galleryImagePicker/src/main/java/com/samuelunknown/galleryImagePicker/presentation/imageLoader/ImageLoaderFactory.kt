package com.samuelunknown.galleryImagePicker.presentation.imageLoader

/**
 * Interface for [ImageLoader] creation
 */
interface ImageLoaderFactory {

    /**
     * Creation method
     * @return [ImageLoader]
     */
    fun create(): ImageLoader
}