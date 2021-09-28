package com.samuelunknown.library.presentation.model

sealed class GalleryState {
    object Init : GalleryState()
    data class Loaded(val items: List<GalleryItem>) : GalleryState()
}
