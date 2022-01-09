package com.samuelunknown.galleryImagePicker.presentation.ui.screen.gallery.fragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samuelunknown.galleryImagePicker.domain.model.FolderDto
import com.samuelunknown.galleryImagePicker.domain.model.GalleryConfigurationDto
import com.samuelunknown.galleryImagePicker.domain.model.ImagesResultDto
import com.samuelunknown.galleryImagePicker.domain.useCase.getFoldersUseCase.GetFoldersUseCase
import com.samuelunknown.galleryImagePicker.domain.useCase.getImagesUseCase.GetImagesUseCase
import com.samuelunknown.galleryImagePicker.presentation.model.GalleryAction
import com.samuelunknown.galleryImagePicker.presentation.model.GalleryItem
import com.samuelunknown.galleryImagePicker.presentation.model.GalleryState
import com.samuelunknown.galleryImagePicker.presentation.model.toFolderDto
import com.samuelunknown.galleryImagePicker.presentation.model.toFolderItem
import com.samuelunknown.galleryImagePicker.presentation.model.toGalleryItemImage
import com.samuelunknown.galleryImagePicker.presentation.model.toImageDto
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal class GalleryFragmentVm(
    private val configurationDto: GalleryConfigurationDto,
    private val getImagesUseCase: GetImagesUseCase,
    private val getFoldersUseCase: GetFoldersUseCase
) : ViewModel() {

    private val _stateFlow = MutableStateFlow<GalleryState>(GalleryState.Init)
    val stateFlow: StateFlow<GalleryState> = _stateFlow.asStateFlow()

    val actionFlow = MutableSharedFlow<GalleryAction>()

    init {
        initSubscriptions()
    }

    private fun initSubscriptions() {
        viewModelScope.launch {
            actionFlow.collect { action ->
                when (action) {
                    is GalleryAction.GetImagesAction -> handleGetImagesAction(action)
                    is GalleryAction.GetImagesAndFoldersAction -> handleGetImagesAndFoldersAction()
                    is GalleryAction.PickupAction -> handlePickupAction()
                    is GalleryAction.ChangeSelectionAction -> handleChangeSelectionAction(action)
                }
            }
        }
    }

    private fun handleGetImagesAction(action: GalleryAction.GetImagesAction) {
        val state = _stateFlow.value
        check(state is GalleryState.Loaded)
        val selectedFolder = action.folder

        viewModelScope.launch {
            try {
                _stateFlow.emit(
                    GalleryState.Loaded(
                        items = getGalleryItems(selectedFolder?.toFolderDto()),
                        folders = state.folders,
                        selectedFolder = selectedFolder
                    )
                )
            } catch (ex: Exception) {
                _stateFlow.emit(
                    GalleryState.Error(error = ImagesResultDto.Error.Unknown(ex.localizedMessage))
                )
            }
        }
    }

    private fun handleGetImagesAndFoldersAction() {
        if (_stateFlow.value is GalleryState.Loaded) {
            return
        }

        val itemsDeferred = viewModelScope.async { getGalleryItems() }
        val foldersDeferred = viewModelScope.async {
            getFoldersUseCase.execute().map { it.toFolderItem() }
        }

        viewModelScope.launch {
            try {
                _stateFlow.emit(
                    GalleryState.Loaded(
                        items = itemsDeferred.await(),
                        folders = foldersDeferred.await(),
                        selectedFolder = null
                    )
                )
            } catch (ex: Exception) {
                _stateFlow.emit(
                    GalleryState.Error(error = ImagesResultDto.Error.Unknown(ex.localizedMessage))
                )
            }
        }
    }

    private fun handleChangeSelectionAction(action: GalleryAction.ChangeSelectionAction) {
        val state = _stateFlow.value
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
            _stateFlow.emit(GalleryState.Loaded(newItems, state.folders, state.selectedFolder))
        }
    }

    private fun handlePickupAction() {
        val state = _stateFlow.value
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

    private suspend fun getGalleryItems(folder: FolderDto? = null): List<GalleryItem.Image> {
        return getImagesUseCase
            .execute(
                mimeTypes = configurationDto.mimeTypes ?: emptyList(),
                folder = folder
            )
            .map { it.toGalleryItemImage() }
    }

    companion object {
        private val TAG = GalleryFragmentVm::class.java.simpleName
    }
}