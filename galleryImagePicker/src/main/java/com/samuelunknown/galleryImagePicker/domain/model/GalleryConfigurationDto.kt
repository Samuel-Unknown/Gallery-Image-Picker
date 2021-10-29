package com.samuelunknown.galleryImagePicker.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GalleryConfigurationDto(
    val spanCount: Int,
    val spacingSizeInPixels: Int,
    val openLikeBottomSheet: Boolean,
    val peekHeightInPercents: Int,
    val mimeTypes: List<String>? = null
) : Parcelable {
    init {
        check(spanCount > 0) {
            "`spanCount` must be > 0: current value $spanCount"
        }
        check(spacingSizeInPixels >= 0) {
            "`spacingSize` must be > 0: current value $spacingSizeInPixels"
        }
        check(peekHeightInPercents in 0..100) {
            "`peekHeightInPercents` must be in range [0; 100] : current value $peekHeightInPercents"
        }
    }
}