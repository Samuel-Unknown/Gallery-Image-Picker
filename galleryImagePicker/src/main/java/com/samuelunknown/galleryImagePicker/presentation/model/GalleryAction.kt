package com.samuelunknown.galleryImagePicker.presentation.model

internal sealed class GalleryAction {
    data object GetImagesAndFoldersAction : GalleryAction()
    data object PickupAction : GalleryAction()

    data class GetImagesAction(val folder: FolderItem?) : GalleryAction()
    data class ChangeSelectionAction(val item: GalleryItem.Image) : GalleryAction()
}