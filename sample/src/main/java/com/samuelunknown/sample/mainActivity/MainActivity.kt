package com.samuelunknown.sample.mainActivity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.samuelunknown.galleryImagePicker.domain.model.ImagesResultDto
import com.samuelunknown.galleryImagePicker.presentation.resultContract.ImagesResultContract
import com.samuelunknown.sample.databinding.ActivityMainBinding
import com.samuelunknown.sample.extensions.setIsCheckedIfItDoesNotMatch
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

                    binding.result.text = state.resultText
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
}