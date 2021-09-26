package com.samuelunknown.library.presentation.ui.screen.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samuelunknown.library.domain.GetImagesUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GalleryFragmentVm(private val getImagesUseCase: GetImagesUseCase) : ViewModel() {

    init {
        getImages()
    }

    private fun getImages() {
        viewModelScope.launch {
            delay(3000)
            try {
                val images = getImagesUseCase.execute()
//                onAcceptAction(ImagesResultDto.Success(images.subList(0, 10)))
            } catch (ex: Exception) {
//                onAcceptAction(ImagesResultDto.Error.Unknown(ex.localizedMessage))
            }
        }
    }
}