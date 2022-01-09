package com.samuelunknown.galleryImagePicker.domain.useCase.getFoldersUseCase

import com.samuelunknown.galleryImagePicker.domain.model.FolderDto

internal interface GetFoldersUseCase {
    suspend fun execute(): List<FolderDto>
}