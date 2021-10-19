package com.samuelunknown.galleryImagePicker.presentation.ui.screen.gallery.fragment

import androidx.lifecycle.SavedStateHandle
import com.samuelunknown.galleryImagePicker.domain.GetImagesUseCase
import com.samuelunknown.galleryImagePicker.presentation.ui.SavedStateVmAssistedFactory

internal class GalleryFragmentVmFactory(
    private val getImagesUseCase: GetImagesUseCase
) : SavedStateVmAssistedFactory<GalleryFragmentVm> {
    override fun create(handle: SavedStateHandle): GalleryFragmentVm =
        GalleryFragmentVm(
            getImagesUseCase = getImagesUseCase
        )
}