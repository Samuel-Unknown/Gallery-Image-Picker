package com.samuelunknown.galleryImagePicker.presentation.model

import com.samuelunknown.galleryImagePicker.domain.model.FolderDto
import com.samuelunknown.galleryImagePicker.domain.model.ImageDto

internal fun ImageDto.toGalleryItemImage(): GalleryItem.Image {
    return GalleryItem.Image(uri = this.uri, counter = 0, name = this.name)
}

internal fun GalleryItem.Image.toImageDto(): ImageDto {
    return ImageDto(uri = this.uri, name = this.name)
}

internal fun FolderDto.toFolderItem(): FolderItem {
    return FolderItem(id = this.id, name = this.name)
}

internal fun FolderItem.toFolderDto(): FolderDto {
    return FolderDto(id = this.id, name = this.name)
}