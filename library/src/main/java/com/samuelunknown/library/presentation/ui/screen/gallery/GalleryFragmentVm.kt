package com.samuelunknown.library.presentation.ui.screen.gallery

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samuelunknown.library.domain.GetImagesUseCase
import com.samuelunknown.library.presentation.model.GalleryAction
import com.samuelunknown.library.presentation.model.GalleryItem
import com.samuelunknown.library.presentation.model.GalleryState
import com.samuelunknown.library.presentation.model.toGalleryItemImage
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
                when (action) {
                    is GalleryAction.ChangeSelectionAction -> handleChangeSelectionAction(action)
                    else -> {
                        // TODO
                    }
                }
            }
        }
    }

    private fun getImages() {
        viewModelScope.launch {
            try {
                val images = getImagesUseCase.execute()
                _stateFlow.emit(GalleryState.Loaded(items = images.map { it.toGalleryItemImage() }))
//                onAcceptAction(ImagesResultDto.Success(images.subList(0, 10)))
            } catch (ex: Exception) {
//                onAcceptAction(ImagesResultDto.Error.Unknown(ex.localizedMessage))
            }
        }
    }

    private fun handleChangeSelectionAction(action: GalleryAction.ChangeSelectionAction) {
        _stateFlow.value.let { state ->
            check(state is GalleryState.Loaded)

            val newItems = state.items.toMutableList()
            val indexOfItemToChange = state.items
                .indexOfFirst { it is GalleryItem.Image && it.uri == action.item.uri }
            val itemToChange = state.items[indexOfItemToChange] as GalleryItem.Image

            if (action.item.isSelected) {
                newItems.forEachIndexed { index, galleryItem ->
                    if (galleryItem is GalleryItem.Image && galleryItem.counter > itemToChange.counter) {
                        newItems[index] = galleryItem.copy(counter = galleryItem.counter - 1)
                    }
                }
                newItems[indexOfItemToChange] = itemToChange.copy(counter = 0)
            } else {
                val maxCounter = state.items.filterIsInstance<GalleryItem.Image>()
                    .map(GalleryItem.Image::counter)
                    .maxOrNull() ?: 0

                newItems[indexOfItemToChange] = itemToChange.copy(counter = maxCounter + 1)
            }

            viewModelScope.launch {
                _stateFlow.emit(GalleryState.Loaded(newItems))
            }
        }
    }

    companion object {
        private val TAG = GalleryFragmentVm::class.java.simpleName
    }
}