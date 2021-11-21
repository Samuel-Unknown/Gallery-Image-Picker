package com.samuelunknown.sample.mainActivity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.getDefaultNightMode
import androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode
import androidx.core.view.isInvisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.samuelunknown.galleryImagePicker.domain.model.ImagesResultDto
import com.samuelunknown.galleryImagePicker.presentation.resultContract.ImagesResultContract
import com.samuelunknown.sample.databinding.ActivityMainBinding
import com.samuelunknown.sample.extensions.setIsCheckedIfItDoesNotMatch
import com.samuelunknown.sample.extensions.setTextIfItDoesNotMatch
import com.samuelunknown.sample.mainActivity.MainActivityAction.ViewAction
import com.samuelunknown.sample.mainActivity.MainActivityAction.VmAction
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val vm: MainActivityVm by viewModels()

    private val getImagesLauncher = registerForActivityResult(ImagesResultContract()) { result: ImagesResultDto ->
        lifecycleScope.launch {
            vm.emitAction(VmAction.ChangeResultAction(result))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initGetImagesButton()
        initMimeTypeFilterCheckboxes()
        initSpanCountEditText()
        initSpacingSizeEditText()
        initOpenLikeBottomSheetCheckbox()
        initPeekHeightEditText()
        initIsDarkModeCheckbox()
        initIsCustomStyleCheckbox()

        initStateSubscription()
        initViewActionSubscription()
    }

    private fun initMimeTypeFilterCheckboxes() {
        binding.mimeJpg.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                vm.emitAction(VmAction.ChangeMimeTypeFilterAction(MimeTypeFilter.Jpg(isChecked)))
            }
        }
        binding.mimePng.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                vm.emitAction(VmAction.ChangeMimeTypeFilterAction(MimeTypeFilter.Png(isChecked)))
            }
        }
        binding.mimeGif.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                vm.emitAction(VmAction.ChangeMimeTypeFilterAction(MimeTypeFilter.Gif(isChecked)))
            }
        }
    }

    private fun initGetImagesButton() {
        binding.getImages.setOnClickListener {
            lifecycleScope.launch {
                vm.emitAction(VmAction.PrepareToOpenGalleryAction)
            }
        }
    }

    private fun initSpanCountEditText() {
        binding.span.doAfterTextChanged { editable ->
            lifecycleScope.launch {
                vm.emitAction(VmAction.ChangeSpanCountAction(editable.toString()))
            }
        }
    }

    private fun initSpacingSizeEditText() {
        binding.spacing.doAfterTextChanged { editable ->
            lifecycleScope.launch {
                vm.emitAction(VmAction.ChangeSpacingSizeAction(editable.toString()))
            }
        }
    }

    private fun initOpenLikeBottomSheetCheckbox() {
        binding.openLikeBottomSheet.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                vm.emitAction(VmAction.ChangeOpenLikeBottomSheetAction(isChecked))
            }
        }
    }

    private fun initPeekHeightEditText() {
        binding.peekHeight.doAfterTextChanged { editable ->
            lifecycleScope.launch {
                vm.emitAction(VmAction.ChangePeekHeightAction(editable.toString()))
            }
        }
    }

    private fun initIsDarkModeCheckbox() {
        binding.darkMode.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                vm.emitAction(VmAction.ChangeIsDarkModeEnabledAction(isChecked))
            }
        }
    }

    private fun initIsCustomStyleCheckbox() {
        binding.customStyle.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                vm.emitAction(VmAction.ChangeIsCustomStyleEnabledAction(isChecked))
            }
        }
    }

    private fun initStateSubscription() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.stateFlow.collect { state ->
                    state.mimeTypeFilters.forEach { filter ->
                        when (filter) {
                            is MimeTypeFilter.Jpg -> {
                                binding.mimeJpg.setIsCheckedIfItDoesNotMatch(filter.isChecked)
                            }
                            is MimeTypeFilter.Png -> {
                                binding.mimePng.setIsCheckedIfItDoesNotMatch(filter.isChecked)
                            }
                            is MimeTypeFilter.Gif -> {
                                binding.mimeGif.setIsCheckedIfItDoesNotMatch(filter.isChecked)
                            }
                        }
                    }

                    binding.span.setTextIfItDoesNotMatch(state.spanCount?.toString() ?: "")
                    binding.spanWrapper.error = state.spanCountError

                    binding.spacing.setTextIfItDoesNotMatch(state.spacingSizeInPixels?.toString() ?: "")
                    binding.spacingWrapper.error = state.spacingSizeInPixelsError

                    binding.openLikeBottomSheet.setIsCheckedIfItDoesNotMatch(state.openLikeBottomSheet)
                    binding.peekHeight.setTextIfItDoesNotMatch(state.peekHeightInPercents?.toString() ?: "")
                    binding.peekHeightWrapper.error = state.peekHeightError
                    binding.peekHeightWrapper.isInvisible = state.openLikeBottomSheet.not()

                    binding.darkMode.setIsCheckedIfItDoesNotMatch(state.isDarkModeEnabled)
                    binding.customStyle.setIsCheckedIfItDoesNotMatch(state.isCustomStyleEnabled)

                    binding.result.text = state.resultText

                    updateNightMode(state.isDarkModeEnabled)
                }
            }
        }
    }

    private fun initViewActionSubscription() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.viewActionFlow.collect { action ->
                    when (action) {
                        is ViewAction.OpenGalleryAction -> getImagesLauncher.launch(action.dto)
                    }
                }
            }
        }
    }

    private fun updateNightMode(isDarkModeEnabled: Boolean) {
        val nightMode = if (isDarkModeEnabled) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }

        if (nightMode != getDefaultNightMode()) {
            setDefaultNightMode(nightMode)
            delegate.applyDayNight()
        }
    }
}