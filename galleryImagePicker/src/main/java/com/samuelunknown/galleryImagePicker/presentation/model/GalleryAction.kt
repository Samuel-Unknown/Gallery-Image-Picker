package com.samuelunknown.galleryImagePicker.presentation.model

internal sealed class GalleryAction {
    object GetImagesAndFoldersAction : GalleryAction()
    object PickupAction : GalleryAction()

    data class GetImagesAction(val folder: FolderItem?) : GalleryAction()
    data class ChangeSelectionAction(val item: GalleryItem.Image) : GalleryAction()
}