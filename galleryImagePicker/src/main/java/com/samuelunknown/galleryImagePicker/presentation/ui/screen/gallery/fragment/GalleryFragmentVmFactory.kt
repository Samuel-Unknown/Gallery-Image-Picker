package com.samuelunknown.galleryImagePicker.presentation.ui.screen.gallery.fragment

import androidx.lifecycle.SavedStateHandle
import com.samuelunknown.galleryImagePicker.domain.useCase.getImagesUseCase.GetImagesUseCase
import com.samuelunknown.galleryImagePicker.domain.model.GalleryConfigurationDto
import com.samuelunknown.galleryImagePicker.domain.useCase.getFoldersUseCase.GetFoldersUseCase
import com.samuelunknown.galleryImagePicker.presentation.ui.SavedStateVmAssistedFactory

internal class GalleryFragmentVmFactory(
    private val configurationDto: GalleryConfigurationDto,
    private val getImagesUseCase: GetImagesUseCase,
    private val getFoldersUseCase: GetFoldersUseCase
) : SavedStateVmAssistedFactory<GalleryFragmentVm> {
    override fun create(handle: SavedStateHandle): GalleryFragmentVm =
        GalleryFragmentVm(
            configurationDto = configurationDto,
            getImagesUseCase = getImagesUseCase,
            getFoldersUseCase = getFoldersUseCase
        )
}