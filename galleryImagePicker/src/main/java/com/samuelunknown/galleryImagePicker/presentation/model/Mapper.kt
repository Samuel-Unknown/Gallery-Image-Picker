package com.samuelunknown.galleryImagePicker.presentation.model

import com.samuelunknown.galleryImagePicker.domain.model.ImageDto

internal fun ImageDto.toGalleryItemImage(): GalleryItem.Image {
    return GalleryItem.Image(uri = this.uri, counter = 0, name = this.name)
}

internal fun GalleryItem.Image.toImageDto(): ImageDto {
    return ImageDto(uri = this.uri, name = this.name)
}