package com.samuelunknown.library.presentation.model

import com.samuelunknown.library.domain.model.ImageDto

fun ImageDto.toGalleryItemImage(): GalleryItem.Image {
    return GalleryItem.Image(uri = this.uri)
}