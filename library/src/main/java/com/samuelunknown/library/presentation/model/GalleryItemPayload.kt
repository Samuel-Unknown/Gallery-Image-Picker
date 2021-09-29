package com.samuelunknown.library.presentation.model

sealed class GalleryItemPayload {
    data class SelectionPayload(val item: GalleryItem.Image)
}