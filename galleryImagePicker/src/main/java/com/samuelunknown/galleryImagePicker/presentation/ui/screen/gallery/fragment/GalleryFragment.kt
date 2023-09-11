package com.samuelunknown.galleryImagePicker.presentation.ui.screen.gallery.fragment

import android.Manifest
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
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
import com.samuelunknown.galleryImagePicker.databinding.FragmentGalleryBinding
import com.samuelunknown.galleryImagePicker.domain.model.GalleryConfigurationDto
import com.samuelunknown.galleryImagePicker.domain.model.ImagesResultDto
import com.samuelunknown.galleryImagePicker.domain.useCase.getFoldersUseCase.GetFoldersUseCaseImpl
import com.samuelunknown.galleryImagePicker.domain.useCase.getImagesUseCase.GetImagesUseCaseImpl
import com.samuelunknown.galleryImagePicker.extensions.PermissionLauncher
import com.samuelunknown.galleryImagePicker.extensions.PermissionResult
import com.samuelunknown.galleryImagePicker.extensions.calculateScreenHeightWithoutSystemBars
import com.samuelunknown.galleryImagePicker.extensions.initActionBar
import com.samuelunknown.galleryImagePicker.extensions.setDimVisibility
import com.samuelunknown.galleryImagePicker.extensions.updateHeight
import com.samuelunknown.galleryImagePicker.extensions.updateMargins
import com.samuelunknown.galleryImagePicker.presentation.imageLoader.ImageLoaderFactoryHolder
import com.samuelunknown.galleryImagePicker.presentation.model.FolderItem
import com.samuelunknown.galleryImagePicker.presentation.model.GalleryAction
import com.samuelunknown.galleryImagePicker.presentation.model.GalleryState
import com.samuelunknown.galleryImagePicker.presentation.ui.savedStateViewModel
import com.samuelunknown.galleryImagePicker.presentation.ui.screen.gallery.fragment.recycler.GalleryAdapter
import com.samuelunknown.galleryImagePicker.presentation.ui.screen.gallery.fragment.recycler.GalleryItemAnimator
import com.samuelunknown.galleryImagePicker.presentation.ui.screen.gallery.fragment.recycler.GridSpacingItemDecoration
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal class GalleryFragment private constructor(
    private val configurationDto: GalleryConfigurationDto,
    private val onResultAction: (ImagesResultDto) -> Unit = {}
) : BottomSheetDialogFragment() {

    // region Properties
    private var result: ImagesResultDto? = null

    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!

    private var screenHeight: Int = 0
    private var peekHeight: Int = 0

    private val bottomSheet: BottomSheetDialog
        get() = requireDialog() as BottomSheetDialog

    private val permissionLauncher = PermissionLauncher.init(
        fragment = this,
        permission = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) Manifest.permission.READ_EXTERNAL_STORAGE else Manifest.permission.READ_MEDIA_IMAGES,
        resultAction = { result ->
            when (result) {
                is PermissionResult.Granted -> {
                    lifecycleScope.launch {
                        vm.actionFlow.emit(GalleryAction.GetImagesAndFoldersAction)
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

    private val defaultToolbarTitle: String by lazy(LazyThreadSafetyMode.NONE) {
        binding.toolbar.title.toString()
    }

    private val pickButtonDefaultBottomMargin: Int by lazy(LazyThreadSafetyMode.NONE) {
        binding.pickupButton.marginBottom
    }

    private val popupMenu: PopupMenu by lazy(LazyThreadSafetyMode.NONE) {
        PopupMenu(requireContext(), binding.toolbarTitle)
    }

    private val galleryAdapter: GalleryAdapter by lazy(LazyThreadSafetyMode.NONE) {
        GalleryAdapter(
            context = requireContext(),
            spanCount = configurationDto.spanCount,
            spacingSize = configurationDto.spacingSizeInPixels,
            imageLoaderFactory = ImageLoaderFactoryHolder.imageLoaderFactory,
            changeSelectionAction = { item ->
                lifecycleScope.launch {
                    vm.actionFlow.emit(GalleryAction.ChangeSelectionAction(item))
                }
            },
            selectorSizeRatio = configurationDto.selectorSizeRatio,
            selectedImageScale = configurationDto.selectedImageScale,
        )
    }

    private val vm: GalleryFragmentVm by savedStateViewModel {
        val contentResolver = requireContext().contentResolver
        GalleryFragmentVmFactory(
            configurationDto = configurationDto,
            getImagesUseCase = GetImagesUseCaseImpl(contentResolver),
            getFoldersUseCase = GetFoldersUseCaseImpl(contentResolver)
        )
    }
    // endregion

    // region Lifecycle
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // NB: since vm must be initialized (for collecting actionFlow actions) before views send any actions
        vm.run {
            requireActivity().calculateScreenHeightWithoutSystemBars { height, width ->
                initRootView(screenHeight = height)
                initBottomSheetDialog(screenHeight = height, screenWidth = width)
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
            onResultAction.invoke(result ?: ImagesResultDto.Success())
        }

        super.onDismiss(dialog)
    }
    // endregion Lifecycle

    // region Initializations
    private fun initRootView(screenHeight: Int) {
        this.screenHeight = screenHeight
        binding.root.updateHeight(screenHeight)
    }

    private fun initBottomSheetDialog(screenHeight: Int, screenWidth: Int) {
        peekHeight = screenHeight * configurationDto.peekHeightInPercents / 100

        with(bottomSheet) {
            behavior.apply {
                maxWidth = screenWidth
                if (configurationDto.openLikeBottomSheet) {
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
            displayHomeAsUpEnabled = true,
            navigationAction = { dismiss() }
        )

        binding.toolbarTitle.apply {
            text = defaultToolbarTitle
            setOnClickListener { popupMenu.show() }
        }

        binding.toolbar.title = ""
    }

    private fun initRecycler() {
        binding.recycler.apply {
            adapter = galleryAdapter
            itemAnimator = GalleryItemAnimator(
                selectedImageScale = configurationDto.selectedImageScale,
                selectionAnimationDurationInMillis = configurationDto.selectionAnimationDurationInMillis
            )
            setHasFixedSize(true)
            addItemDecoration(
                GridSpacingItemDecoration(
                    spanCount = configurationDto.spanCount,
                    spacing = configurationDto.spacingSizeInPixels
                )
            )
            (layoutManager as GridLayoutManager).spanCount = configurationDto.spanCount
        }
    }

    private fun initPickupButton() {
        binding.pickupButton.setOnClickListener {
            lifecycleScope.launch {
                vm.actionFlow.emit(GalleryAction.PickupAction)
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
                            binding.toolbarTitle.text =
                                state.selectedFolder?.name ?: defaultToolbarTitle

                            galleryAdapter.submitList(state.items)
                            setPopupMenuFoldersFolders(state.folders)
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

        // since pickupButton position changed we need update Recycler bottom padding and underlay height
        with(binding) {
            pickupButton.doOnLayout {
                val padding = screenHeight - it.top + it.marginTop
                recycler.updatePadding(bottom = padding)
                underlay.updateHeight(padding)
            }
        }
    }

    private fun finishWithResult(resultDto: ImagesResultDto) {
        result = resultDto
        dismiss()
    }

    private fun setPopupMenuFoldersFolders(folders: List<FolderItem>) {
        val menuItems = folders.toMutableList()
            .apply { add(0, FolderItem(id = "", name = defaultToolbarTitle)) }
            .toList()

        popupMenu.menu.clear()

        menuItems.forEachIndexed { index, item ->
            popupMenu.menu.add(Menu.NONE, index, index, item.name)
        }

        popupMenu.setOnMenuItemClickListener {
            val selectedFolder = if (it.itemId == 0) null else menuItems[it.itemId]
            lifecycleScope.launch {
                vm.actionFlow.emit(GalleryAction.GetImagesAction(selectedFolder))
            }
            true
        }
    }

    companion object {
        val TAG: String = GalleryFragment::class.java.simpleName
        private const val DELAY_IN_MILLISECONDS_FOR_SMOOTH_DIALOG_CLOSING_AFTER_PERMISSION_ERROR =
            300L

        fun init(
            configurationDto: GalleryConfigurationDto,
            onResultAction: (ImagesResultDto) -> Unit = {}
        ) = GalleryFragment(
            configurationDto = configurationDto,
            onResultAction = onResultAction
        )
    }
}