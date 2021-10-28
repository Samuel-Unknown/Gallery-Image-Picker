package com.samuelunknown.galleryImagePicker.presentation.ui.screen.gallery.fragment

import android.Manifest
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
import androidx.core.view.updatePadding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.math.MathUtils
import com.samuelunknown.galleryImagePicker.R
import com.samuelunknown.galleryImagePicker.databinding.FragmentGalleryBinding
import com.samuelunknown.galleryImagePicker.domain.GetImagesUseCaseImpl
import com.samuelunknown.galleryImagePicker.domain.model.GalleryConfigurationDto
import com.samuelunknown.galleryImagePicker.domain.model.ImagesResultDto
import com.samuelunknown.galleryImagePicker.extensions.PermissionLauncher
import com.samuelunknown.galleryImagePicker.extensions.PermissionResult
import com.samuelunknown.galleryImagePicker.extensions.calculateScreenHeightWithoutSystemBars
import com.samuelunknown.galleryImagePicker.extensions.initActionBar
import com.samuelunknown.galleryImagePicker.extensions.setDimVisibility
import com.samuelunknown.galleryImagePicker.extensions.updateHeight
import com.samuelunknown.galleryImagePicker.extensions.updateMargins
import com.samuelunknown.galleryImagePicker.presentation.imageLoader.ImageLoaderFactoryHolder
import com.samuelunknown.galleryImagePicker.presentation.model.GalleryAction
import com.samuelunknown.galleryImagePicker.presentation.model.GalleryState
import com.samuelunknown.galleryImagePicker.presentation.ui.savedStateViewModel
import com.samuelunknown.galleryImagePicker.presentation.ui.screen.gallery.fragment.recycler.GalleryAdapter
import com.samuelunknown.galleryImagePicker.presentation.ui.screen.gallery.fragment.recycler.GalleryItemAnimator
import com.samuelunknown.galleryImagePicker.presentation.ui.screen.gallery.fragment.recycler.GridSpacingItemDecoration
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

internal class GalleryFragment : BottomSheetDialogFragment() {

    // region Properties
    private var onResultAction: ((ImagesResultDto) -> Unit)? = null
    private var result: ImagesResultDto? = null

    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!

    private var screenHeight: Int = 0
    private var peekHeight: Int = 0

    private val bottomSheet: BottomSheetDialog
        get() = requireDialog() as BottomSheetDialog

    private val permissionLauncher = PermissionLauncher.init(
        fragment = this,
        permission = Manifest.permission.READ_EXTERNAL_STORAGE,
        resultAction = { result ->
            when (result) {
                is PermissionResult.Granted -> {
                    lifecycleScope.launch {
                        vm.actionFlow.emit(GalleryAction.GetImages)
                    }
                }
                is PermissionResult.NotGranted -> {
                    lifecycleScope.launch {
                        delay(DELAY_IN_MILLISECONDS_FOR_SMOOTH_DIALOG_CLOSING_AFTER_PERMISSION_ERROR)
                        finishWithResult(
                            ImagesResultDto.Error.PermissionError(
                                permission = result.permission,
                                isGrantingPermissionInSettingsRequired = result.isGrantingPermissionInSettingsRequired
                            )
                        )
                    }
                }
            }
        }
    )

    private val configurationDto: GalleryConfigurationDto by lazy(LazyThreadSafetyMode.NONE) {
        arguments?.getParcelable<GalleryConfigurationDto>(EXTRA_ARGUMENT_DTO)
            ?: throw Exception("arguments is null)")
    }

    private val pickButtonDefaultBottomMargin: Int by lazy(LazyThreadSafetyMode.NONE) {
        binding.pickupButton.marginBottom
    }

    private val galleryAdapter: GalleryAdapter by lazy(LazyThreadSafetyMode.NONE) {
        GalleryAdapter(
            spanCount = configurationDto.spanCount,
            spacingSize = configurationDto.spacingSize,
            imageLoaderFactory = ImageLoaderFactoryHolder.imageLoaderFactory,
            changeSelectionAction = { item ->
                lifecycleScope.launch {
                    vm.actionFlow.emit(GalleryAction.ChangeSelectionAction(item))
                }
            }
        )
    }

    private val vm: GalleryFragmentVm by savedStateViewModel {
        GalleryFragmentVmFactory(
            configurationDto = configurationDto,
            getImagesUseCase = GetImagesUseCaseImpl(requireContext().contentResolver)
        )
    }
    // endregion

    // region Lifecycle
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // NB: since vm must be initialized (for collecting actionFlow actions) before views send any actions
        vm.run {
            requireActivity().calculateScreenHeightWithoutSystemBars() { height ->
                screenHeight = height
                peekHeight = (screenHeight * PEEK_HEIGHT_PERCENTAGE).roundToInt()

                initRootView()
                initBottomSheetDialog()
                initToolbar()
                initRecycler()
                initPickupButton()
                initSubscriptions()

                permissionLauncher.request()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recycler.adapter = null
        _binding = null
    }

    override fun onDismiss(dialog: DialogInterface) {
        // NB: the result is empty when we press the back button
        // on bottom navigation or somewhere outside of BottomSheet

        if (requireActivity().isChangingConfigurations.not()) {
            onResultAction?.invoke(result ?: ImagesResultDto.Success())
        }

        super.onDismiss(dialog)
    }
    // endregion Lifecycle

    // region Initializations
    private fun initRootView() {
        binding.root.updateHeight(screenHeight)
    }

    private fun initBottomSheetDialog() {
        with(bottomSheet) {
            behavior.apply {
                if (IS_BOTTOM_SHEET_USED) {
                    val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
                        override fun onStateChanged(view: View, state: Int) {
                            val isDimVisible = state != BottomSheetBehavior.STATE_EXPANDED
                            window?.setDimVisibility(isDimVisible)
                        }

                        override fun onSlide(view: View, offset: Float) {
                            val boundedOffset = if (offset < 0) 0f else offset
                            binding.motionLayout.progress = boundedOffset
                            updatePickButtonOffset(boundedOffset)
                        }
                    }

                    addBottomSheetCallback(bottomSheetCallback)
                    peekHeight = this@GalleryFragment.peekHeight
                    isHideable = true
                    state = BottomSheetBehavior.STATE_COLLAPSED
                    binding.motionLayout.progress = 0f
                    updatePickButtonOffset(0f)
                } else {
                    peekHeight = screenHeight
                    isHideable = false
                    state = BottomSheetBehavior.STATE_EXPANDED
                    binding.motionLayout.progress = 1f
                    updatePickButtonOffset(1f)
                    window?.setDimVisibility(false)
                }
            }
        }
    }

    private fun initToolbar() {
        initActionBar(
            toolbar = binding.toolbar,
            title = getString(R.string.gallery_image_picker__toolbar),
            displayHomeAsUpEnabled = true,
            navigationAction = { dismiss() }
        )
    }

    private fun initRecycler() {
        binding.recycler.apply {
            adapter = galleryAdapter
            itemAnimator = GalleryItemAnimator()
            setHasFixedSize(true)
            addItemDecoration(
                GridSpacingItemDecoration(
                    spanCount = configurationDto.spanCount,
                    spacing = configurationDto.spacingSize
                )
            )
            (layoutManager as GridLayoutManager).spanCount = configurationDto.spanCount
        }
    }

    private fun initPickupButton() {
        binding.pickupButton.setOnClickListener {
            lifecycleScope.launch {
                vm.actionFlow.emit(GalleryAction.Pickup)
            }
        }
    }

    private fun initSubscriptions() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.stateFlow.collect { state ->
                    when (state) {
                        is GalleryState.Init -> {
                            binding.pickupButton.isVisible = false
                        }
                        is GalleryState.Loaded -> {
                            binding.pickupButton.isVisible = true
                            galleryAdapter.updateItems(state.items)
                        }
                        is GalleryState.Picked -> {
                            finishWithResult(state.result)
                        }
                        is GalleryState.Error -> {
                            finishWithResult(state.error)
                        }
                    }
                }
            }
        }
    }
    // endregion

    private fun updatePickButtonOffset(offset: Float) {
        val bottomMargin = MathUtils.lerp(
            (pickButtonDefaultBottomMargin + screenHeight - peekHeight).toFloat(),
            pickButtonDefaultBottomMargin.toFloat(),
            offset
        ).toInt()

        binding.pickupButton.updateMargins(bottomMargin = bottomMargin)

        // since pickupButton position changed we need update Recycler bottom padding and pickupBackground height
        with(binding) {
            pickupButton.doOnLayout {
                val padding = screenHeight - it.top + it.marginTop
                recycler.updatePadding(bottom = padding)
                pickupBackground.updateHeight(padding)
            }
        }
    }

    private fun finishWithResult(resultDto: ImagesResultDto) {
        result = resultDto
        dismiss()
    }

    internal fun setOnResultAction(onResultAction: (ImagesResultDto) -> Unit = {}) {
        this.onResultAction = onResultAction
    }

    companion object {
        val TAG: String = GalleryFragment::class.java.simpleName
        private const val PEEK_HEIGHT_PERCENTAGE = 0.7
        private const val IS_BOTTOM_SHEET_USED = true
        private const val DELAY_IN_MILLISECONDS_FOR_SMOOTH_DIALOG_CLOSING_AFTER_PERMISSION_ERROR = 300L
        private const val EXTRA_ARGUMENT_DTO = "EXTRA_ARGUMENT_DTO"

        fun init(
            galleryConfigurationDto: GalleryConfigurationDto,
            onResultAction: (ImagesResultDto) -> Unit = {}
        ) = GalleryFragment().also {
            it.setOnResultAction(onResultAction)
            it.arguments = bundleOf(EXTRA_ARGUMENT_DTO to galleryConfigurationDto)
        }
    }
}