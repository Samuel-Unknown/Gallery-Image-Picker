package com.samuelunknown.galleryImagePicker.presentation.model

internal sealed class GalleryAction {
    object GetImages : GalleryAction()
    object Pickup : GalleryAction()
    data class ChangeSelectionAction(val item: GalleryItem.Image) : GalleryAction()
}