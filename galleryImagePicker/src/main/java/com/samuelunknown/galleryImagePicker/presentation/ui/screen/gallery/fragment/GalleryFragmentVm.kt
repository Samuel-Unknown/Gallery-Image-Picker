package com.samuelunknown.galleryImagePicker.presentation.ui.screen.gallery.fragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samuelunknown.galleryImagePicker.domain.GetImagesUseCase
import com.samuelunknown.galleryImagePicker.domain.model.GalleryConfigurationDto
import com.samuelunknown.galleryImagePicker.domain.model.ImagesResultDto
import com.samuelunknown.galleryImagePicker.presentation.model.GalleryAction
import com.samuelunknown.galleryImagePicker.presentation.model.GalleryItem
import com.samuelunknown.galleryImagePicker.presentation.model.GalleryState
import com.samuelunknown.galleryImagePicker.presentation.model.toGalleryItemImage
import com.samuelunknown.galleryImagePicker.presentation.model.toImageDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal class GalleryFragmentVm(
    private val configurationDto: GalleryConfigurationDto,
    private val getImagesUseCase: GetImagesUseCase
) : ViewModel() {

    private val _stateFlow = MutableStateFlow<GalleryState>(GalleryState.Init)
    val stateFlow: Flow<GalleryState> = _stateFlow

    val actionFlow = MutableSharedFlow<GalleryAction>()

    init {
        initSubscriptions()
    }

    private fun initSubscriptions() {
        viewModelScope.launch {
            actionFlow.collect { action ->
                when (action) {
                    is GalleryAction.GetImages -> handleGetImagesAction()
                    is GalleryAction.Pickup -> handlePickupAction()
                    is GalleryAction.ChangeSelectionAction -> handleChangeSelectionAction(action)
                }
            }
        }
    }

    private fun handleGetImagesAction() {
        if (_stateFlow.value is GalleryState.Loaded) {
            return
        }

        viewModelScope.launch {
            try {
                val images = getImagesUseCase.execute(
                    mimeTypes = configurationDto.mimeTypes ?: listOf()
                )
                _stateFlow.emit(
                    GalleryState.Loaded(items = images.map { it.toGalleryItemImage() })
                )
            } catch (ex: Exception) {
                _stateFlow.emit(
                    GalleryState.Error(error = ImagesResultDto.Error.Unknown(ex.localizedMessage))
                )
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

    private fun handlePickupAction() {
        _stateFlow.value.let { state ->
            check(state is GalleryState.Loaded)

            val selectedSortedImages = state.items
                .filterIsInstance<GalleryItem.Image>()
                .filter(GalleryItem.Image::isSelected)
                .sortedBy { it.counter }
                .map { it.toImageDto() }

            val result = ImagesResultDto.Success(selectedSortedImages)

            viewModelScope.launch {
                _stateFlow.emit(GalleryState.Picked(result))
            }
        }
    }

    companion object {
        private val TAG = GalleryFragmentVm::class.java.simpleName
    }
}