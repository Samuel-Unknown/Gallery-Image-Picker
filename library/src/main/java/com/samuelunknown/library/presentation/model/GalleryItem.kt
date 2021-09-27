package com.samuelunknown.library.presentation.model

import android.net.Uri

sealed class GalleryItem {
    data class Image(val uri: Uri) : GalleryItem()
}