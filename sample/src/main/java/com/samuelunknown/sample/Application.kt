package com.samuelunknown.sample

import android.app.Application
import android.os.StrictMode
import com.samuelunknown.galleryImagePicker.GalleryImagePicker
import com.samuelunknown.galleryImagePickerGlide.imageLoaderFactory.ImageLoaderFactoryGlideImpl
import com.samuelunknown.galleryImagePickerCoil.imageLoaderFactory.ImageLoaderFactoryCoilImpl

class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        initGalleryImagePickerLib()
        initStrictMode()
    }

    private fun initGalleryImagePickerLib() {
        when (BuildConfig.FLAVOR) {
            GLIDE_FLAVOR -> {
                GalleryImagePicker.init(ImageLoaderFactoryGlideImpl(appContext = this))
            }

            COIL_FLAVOR -> {
                GalleryImagePicker.init(ImageLoaderFactoryCoilImpl(appContext = this))
            }
        }
    }

    private fun initStrictMode() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .penaltyLog()
                    .build()
            )
        }
    }

    private companion object {
        const val GLIDE_FLAVOR = "Glide"
        const val COIL_FLAVOR = "Coil"
    }
}
