package com.samuelunknown.galleryImagePicker.domain.model

import android.os.Parcelable
import androidx.annotation.IntRange
import androidx.annotation.Px
import androidx.annotation.StyleRes
import com.samuelunknown.galleryImagePicker.R
import kotlinx.parcelize.Parcelize

@Parcelize
data class GalleryConfigurationDto(
    @IntRange(from = 0) @Px val spacingSizeInPixels: Int,
    @IntRange(from = 1) val spanCount: Int,
    val openLikeBottomSheet: Boolean,
    val singleSelection: Boolean,
    @IntRange(from = 0, to = 100) val peekHeightInPercents: Int,
    val mimeTypes: List<String>? = null,
    @StyleRes val themeResId: Int = R.style.GalleryImagePicker_Theme_Default
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