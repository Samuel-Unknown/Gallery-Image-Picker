package com.samuelunknown.sample

import android.app.Application
import com.samuelunknown.galleryImagePicker.GalleryImagePicker

class Application: Application() {
    override fun onCreate() {
        super.onCreate()
        initGalleryImagePickerLib()
    }

    private fun initGalleryImagePickerLib() {
        GalleryImagePicker.init(ImageLoaderFactoryGlideImpl(appContext = this))
    }
}