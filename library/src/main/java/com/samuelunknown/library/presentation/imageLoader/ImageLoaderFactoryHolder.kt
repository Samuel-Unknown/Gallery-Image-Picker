package com.samuelunknown.library.presentation.imageLoader

internal object ImageLoaderFactoryHolder {
    @Suppress("ObjectPropertyName")
    private var _imageLoaderFactory: ImageLoaderFactory? = null

    val imageLoaderFactory: ImageLoaderFactory
        get() = _imageLoaderFactory ?: throw IllegalStateException("GalleryImagePicker.init()")

    fun init(factory: ImageLoaderFactory) {
        _imageLoaderFactory = factory
    }
}