package com.samuelunknown.library.presentation.ui.screen.gallery

import androidx.lifecycle.SavedStateHandle
import com.samuelunknown.library.domain.GetImagesUseCase
import com.samuelunknown.library.presentation.ui.SavedStateVmAssistedFactory

class GalleryFragmentVmFactory(
    private val getImagesUseCase: GetImagesUseCase
) : SavedStateVmAssistedFactory<GalleryFragmentVm> {
    override fun create(handle: SavedStateHandle): GalleryFragmentVm =
        GalleryFragmentVm(
            getImagesUseCase = getImagesUseCase
        )
}