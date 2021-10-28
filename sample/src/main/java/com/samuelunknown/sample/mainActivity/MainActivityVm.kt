package com.samuelunknown.sample.mainActivity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samuelunknown.galleryImagePicker.domain.model.GalleryConfigurationDto
import com.samuelunknown.galleryImagePicker.domain.model.ImagesResultDto
import com.samuelunknown.sample.mainActivity.MainActivityAction.ViewAction
import com.samuelunknown.sample.mainActivity.MainActivityAction.VmAction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivityVm : ViewModel() {
    private val _stateFlow = MutableStateFlow(MainActivityState())
    val stateFlow: Flow<MainActivityState> = _stateFlow

    private val _viewActionFlow: MutableSharedFlow<ViewAction> = MutableSharedFlow()
    val viewActionFlow: Flow<ViewAction> = _viewActionFlow.asSharedFlow()

    private val _vmActionFlow: MutableSharedFlow<VmAction> = MutableSharedFlow()
    private val vmActionFlow: Flow<VmAction> = _vmActionFlow.asSharedFlow()

    init {
        initVmActionSubscription()
    }

    suspend fun emitAction(action: VmAction) {
        _vmActionFlow.emit(action)
    }

    private fun initVmActionSubscription() {
        viewModelScope.launch {
            vmActionFlow.collect { action ->
                when (action) {
                    is VmAction.PrepareToOpenGalleryAction -> {
                        prepareToOpenGallery()
                    }
                    is VmAction.ChangeMimeTypeFilterAction -> {
                        changeMimeTypeFilter(action)
                    }
                    is VmAction.ChangeResultAction -> {
                        changeResultText(action)
                    }
                }
            }
        }
    }

    private suspend fun changeResultText(action: VmAction.ChangeResultAction) {
        val resultText = when (action.result) {
            is ImagesResultDto.Success -> {
                if (action.result.images.isEmpty()) {
                    "There is no selected images"
                } else {
                    action.result.images.joinToString(separator = "\n\n")
                }
            }
            is ImagesResultDto.Error -> "Error: ${action.result.message}"
        }

        val newState = _stateFlow.value.copy(resultText = resultText)
        _stateFlow.emit(newState)
    }

    private suspend fun prepareToOpenGallery() {
        val dto = GalleryConfigurationDto(
            spanCount = _stateFlow.value.spanCount,
            spacingSize = _stateFlow.value.spacingSizeInPixels,
            mimeTypes = _stateFlow.value.mimeTypes
        )
        _viewActionFlow.emit(ViewAction.OpenGalleryAction(dto))
    }

    private suspend fun changeMimeTypeFilter(action: VmAction.ChangeMimeTypeFilterAction) {
        val newMimeTypeFilters = _stateFlow.value.mimeTypeFilters.toMutableList()
        val index = newMimeTypeFilters.indexOfFirst { it.name == action.mimeTypeFilter.name }
        newMimeTypeFilters[index] = action.mimeTypeFilter

        _stateFlow.emit(_stateFlow.value.copy(mimeTypeFilters = newMimeTypeFilters))
    }
}