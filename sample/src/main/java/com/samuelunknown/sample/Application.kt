package com.samuelunknown.sample

import android.app.Application
import com.samuelunknown.galleryImagePicker.GalleryImagePicker
import com.samuelunknown.sample.imageLoaderFactory.ImageLoaderFactoryGlideImpl

class Application: Application() {
    override fun onCreate() {
        super.onCreate()
        initGalleryImagePickerLib()
    }

    private fun initGalleryImagePickerLib() {
        GalleryImagePicker.init(ImageLoaderFactoryGlideImpl(appContext = this))
        // NB: we can use another implementation if we want.
        // GalleryImagePicker.init(ImageLoaderFactoryCoilImpl(appContext = this))
    }
}