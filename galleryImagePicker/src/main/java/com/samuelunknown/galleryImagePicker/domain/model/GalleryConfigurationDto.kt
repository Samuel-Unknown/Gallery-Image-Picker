package com.samuelunknown.galleryImagePicker.domain.model

import android.os.Parcelable
import androidx.annotation.Px
import kotlinx.parcelize.Parcelize

@Parcelize
data class GalleryConfigurationDto(
    val spanCount: Int = 3,
    @Px val spacingSize: Int = 0,
    val mimeTypes: List<String>? = null
) : Parcelable {
    init {
        check(spanCount > 0) { "spanCount must be > 0: current value $spanCount" }
    }
}