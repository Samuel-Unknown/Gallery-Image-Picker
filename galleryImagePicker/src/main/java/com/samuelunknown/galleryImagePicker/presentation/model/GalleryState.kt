package com.samuelunknown.galleryImagePicker.presentation.model

import com.samuelunknown.galleryImagePicker.domain.model.ImagesResultDto

internal sealed class GalleryState {
    data object Init : GalleryState()
    data class Loaded(
        val items: List<GalleryItem>,
        val folders: List<FolderItem>,
        val selectedFolder: FolderItem?
    ) : GalleryState()

    data class Picked(val result: ImagesResultDto.Success) : GalleryState()
    data class Error(val error: ImagesResultDto.Error) : GalleryState()
}
