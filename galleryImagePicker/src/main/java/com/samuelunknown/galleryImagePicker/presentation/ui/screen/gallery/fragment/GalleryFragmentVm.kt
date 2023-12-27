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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
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

    val actionFlow = MutableSharedFlow<GalleryAction>(
        extraBufferCapacity = Int.MAX_VALUE,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

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
        if (state.selectedFolder == selectedFolder) {
            return
        }

        viewModelScope.launch {
            try {
                clearSelection()

                // When we clear the selection we should wait to finish all animations.
                // Maybe it's better to animate movements at the same time with deselection
                // but I think it will be too much work to fix GalleryItemAnimator
                delay(configurationDto.selectionAnimationDurationInMillis)

                _stateFlow.tryEmit(
                    GalleryState.Loaded(
                        items = getGalleryItems(selectedFolder?.toFolderDto()),
                        folders = state.folders,
                        selectedFolder = selectedFolder
                    )
                )
            } catch (ex: Exception) {
                _stateFlow.tryEmit(
                    GalleryState.Error(error = ImagesResultDto.Error.Unknown(ex.localizedMessage))
                )
            }
        }
    }

    private fun clearSelection() {
        val state = _stateFlow.value
        check(state is GalleryState.Loaded)

        val newItems = List(state.items.size) { i ->
            val item = state.items[i]
            if (item is GalleryItem.Image) {
                item.copy(counter = 0)
            } else {
                item
            }
        }

        _stateFlow.tryEmit(
            GalleryState.Loaded(
                items = newItems,
                folders = state.folders,
                selectedFolder = state.selectedFolder
            )
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun handleGetImagesAndFoldersAction() {
        if (_stateFlow.value is GalleryState.Loaded) {
            return
        }

        val itemsDeferred = viewModelScope.async { getGalleryItems() }
        val foldersDeferred = viewModelScope.async {
            getFoldersUseCase.execute().map { it.toFolderItem() }
        }

        viewModelScope.launch {
            listOf(itemsDeferred, foldersDeferred).awaitAll()

            try {
                _stateFlow.tryEmit(
                    GalleryState.Loaded(
                        items = itemsDeferred.getCompleted(),
                        folders = foldersDeferred.getCompleted(),
                        selectedFolder = null
                    )
                )
            } catch (ex: Exception) {
                _stateFlow.tryEmit(
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
            if (configurationDto.singleSelection) {
                newItems.forEachIndexed { index, galleryItem ->
                    if (galleryItem is GalleryItem.Image && galleryItem.isSelected) {
                        newItems[index] = galleryItem.copy(counter = 0)
                    }
                }
                newItems[indexOfItemToChange] = itemToChange.copy(counter = 1)
            } else {
                val maxCounter = state.items.filterIsInstance<GalleryItem.Image>()
                    .map(GalleryItem.Image::counter)
                    .maxOrNull() ?: 0

                newItems[indexOfItemToChange] = itemToChange.copy(counter = maxCounter + 1)
            }
        }

        _stateFlow.tryEmit(GalleryState.Loaded(newItems, state.folders, state.selectedFolder))
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
        _stateFlow.tryEmit(GalleryState.Picked(result))
    }

    private suspend fun getGalleryItems(folder: FolderDto? = null): List<GalleryItem.Image> {
        return getImagesUseCase
            .execute(
                mimeTypes = configurationDto.mimeTypes ?: emptyList(),
                folder = folder
            )
            .map { it.toGalleryItemImage() }
    }
}
