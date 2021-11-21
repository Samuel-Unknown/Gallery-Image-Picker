package com.samuelunknown.sample.mainActivity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samuelunknown.galleryImagePicker.domain.model.GalleryConfigurationDto
import com.samuelunknown.galleryImagePicker.domain.model.ImagesResultDto
import com.samuelunknown.sample.R
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
                    is VmAction.ChangeSpacingSizeAction -> changeSpacingSize(action)
                    is VmAction.ChangeSpanCountAction -> changeSpanCount(action)
                    is VmAction.ChangeOpenLikeBottomSheetAction -> changeOpenLikeBottomSheet(action)
                    is VmAction.ChangePeekHeightAction -> changePeekHeight(action)
                    is VmAction.ChangeIsCustomStyleEnabledAction -> changeIsCustomStyleEnabledAction(action)
                    is VmAction.ChangeIsDarkModeEnabledAction -> changeIsDarkModeEnabledAction(action)
                }
            }
        }
    }

    private suspend fun prepareToOpenGallery() {
        val spanCount = _stateFlow.value.spanCount ?: return
        val spacingSizeInPixels = _stateFlow.value.spacingSizeInPixels ?: return
        val peekHeightInPercents = _stateFlow.value.peekHeightInPercents ?: return
        val themeResId = if (_stateFlow.value.isCustomStyleEnabled) {
            R.style.Theme_CustomGallery
        } else {
            R.style.GalleryImagePicker_Theme_Default
        }

        val dto = GalleryConfigurationDto(
            themeResId = themeResId,
            spacingSizeInPixels = spacingSizeInPixels,
            spanCount = spanCount,
            openLikeBottomSheet = _stateFlow.value.openLikeBottomSheet,
            peekHeightInPercents = peekHeightInPercents,
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

    private suspend fun changeSpanCount(action: VmAction.ChangeSpanCountAction) {
        val newState = try {
            val spanCount = action.spanCountText.toInt()
            if (spanCount > 0) {
                _stateFlow.value.copy(
                    spanCount = spanCount,
                    spanCountError = null
                )
            } else {
                _stateFlow.value.copy(
                    spanCount = null,
                    spanCountError = MainActivityState.EMPTY_VALUE_ERROR
                )
            }
        } catch (ex: Exception) {
            _stateFlow.value.copy(
                spanCount = null,
                spanCountError = MainActivityState.EMPTY_VALUE_ERROR
            )
        }
        _stateFlow.emit(newState)
    }

    private suspend fun changeSpacingSize(action: VmAction.ChangeSpacingSizeAction) {
        val newState = try {
            val spacingSizeInPixels = action.spacingSizeText.toInt()
            if (spacingSizeInPixels >= 0) {
                _stateFlow.value.copy(
                    spacingSizeInPixels = spacingSizeInPixels,
                    spacingSizeInPixelsError = null
                )
            } else {
                _stateFlow.value.copy(
                    spacingSizeInPixels = null,
                    spacingSizeInPixelsError = MainActivityState.EMPTY_VALUE_ERROR
                )
            }
        } catch (ex: Exception) {
            _stateFlow.value.copy(
                spacingSizeInPixels = null,
                spacingSizeInPixelsError = MainActivityState.EMPTY_VALUE_ERROR
            )
        }
        _stateFlow.emit(newState)
    }

    private suspend fun changePeekHeight(action: VmAction.ChangePeekHeightAction) {
        val newState = try {
            val peekHeightInPercents = action.peekHeightText.toInt()
            if (peekHeightInPercents in 0..100) {
                _stateFlow.value.copy(
                    peekHeightInPercents = peekHeightInPercents,
                    peekHeightError = null
                )
            } else {
                _stateFlow.value.copy(
                    peekHeightInPercents = null,
                    peekHeightError = MainActivityState.EMPTY_VALUE_ERROR
                )
            }
        } catch (ex: Exception) {
            _stateFlow.value.copy(
                peekHeightInPercents = null,
                peekHeightError = MainActivityState.EMPTY_VALUE_ERROR
            )
        }
        _stateFlow.emit(newState)
    }

    private suspend fun changeOpenLikeBottomSheet(action: VmAction.ChangeOpenLikeBottomSheetAction) {
        val newState = _stateFlow.value.copy(openLikeBottomSheet = action.openLikeBottomSheet)
        _stateFlow.emit(newState)
    }

    private suspend fun changeIsDarkModeEnabledAction(action: VmAction.ChangeIsDarkModeEnabledAction) {
        val newState = _stateFlow.value.copy(isDarkModeEnabled = action.isEnabled)
        _stateFlow.emit(newState)
    }

    private suspend fun changeIsCustomStyleEnabledAction(action: VmAction.ChangeIsCustomStyleEnabledAction) {
        val newState = _stateFlow.value.copy(isCustomStyleEnabled = action.isEnabled)
        _stateFlow.emit(newState)
    }
}