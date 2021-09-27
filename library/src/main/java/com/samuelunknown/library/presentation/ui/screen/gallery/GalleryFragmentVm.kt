package com.samuelunknown.library.presentation.ui.screen.gallery

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samuelunknown.library.domain.GetImagesUseCase
import com.samuelunknown.library.presentation.model.GalleryAction
import com.samuelunknown.library.presentation.model.GalleryState
import com.samuelunknown.library.presentation.model.toGalleryItemImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class GalleryFragmentVm(private val getImagesUseCase: GetImagesUseCase) : ViewModel() {

    private val _stateFlow = MutableStateFlow<GalleryState>(GalleryState.Init)
    val stateFlow: Flow<GalleryState> = _stateFlow

    val actionFlow = MutableSharedFlow<GalleryAction>()

    init {
        initSubscriptions()
        getImages()
    }

    private fun initSubscriptions() {
        viewModelScope.launch {
            actionFlow.collect { action ->
                Log.d(TAG, "Action: $action")
            }
        }
    }

    private fun getImages() {
        viewModelScope.launch {
            delay(3000)
            try {
                val images = getImagesUseCase.execute()
                _stateFlow.emit(GalleryState.Loaded(items = images.map { it.toGalleryItemImage() }))
//                onAcceptAction(ImagesResultDto.Success(images.subList(0, 10)))
            } catch (ex: Exception) {
//                onAcceptAction(ImagesResultDto.Error.Unknown(ex.localizedMessage))
            }
        }
    }

    companion object {
        private val TAG = GalleryFragmentVm::class.java.simpleName
    }
}