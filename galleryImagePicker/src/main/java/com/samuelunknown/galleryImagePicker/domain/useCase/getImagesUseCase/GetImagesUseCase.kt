package com.samuelunknown.galleryImagePicker.domain.useCase.getImagesUseCase

import com.samuelunknown.galleryImagePicker.domain.model.FolderDto
import com.samuelunknown.galleryImagePicker.domain.model.ImageDto

internal interface GetImagesUseCase {
    suspend fun execute(
        mimeTypes: List<String>,
        folder: FolderDto?
    ): List<ImageDto>
}