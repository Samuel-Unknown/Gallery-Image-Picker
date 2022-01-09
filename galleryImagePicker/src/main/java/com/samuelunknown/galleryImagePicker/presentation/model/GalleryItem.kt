package com.samuelunknown.galleryImagePicker.presentation.model

import android.net.Uri

internal sealed class GalleryItem {
    data class Image(
        val uri: Uri,
        val counter: Int,
        val name: String
    ) : GalleryItem() {
        val isSelected: Boolean
            get() = counter > 0

        val counterText: String
            get() = counter.toString()
    }

    // NB: there will be added CameraPreview item later
}