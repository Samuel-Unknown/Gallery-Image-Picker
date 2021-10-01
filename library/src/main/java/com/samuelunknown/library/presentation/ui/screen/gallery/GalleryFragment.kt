package com.samuelunknown.library.presentation.ui.screen.gallery

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import androidx.core.view.marginBottom
import androidx.core.view.updatePadding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.math.MathUtils
import com.samuelunknown.library.R
import com.samuelunknown.library.databinding.FragmentGalleryBinding
import com.samuelunknown.library.domain.GetImagesUseCaseImpl
import com.samuelunknown.library.domain.model.ImagesResultDto
import com.samuelunknown.library.extensions.getScreenHeight
import com.samuelunknown.library.extensions.initActionBar
import com.samuelunknown.library.extensions.setDimVisibility
import com.samuelunknown.library.extensions.updateHeight
import com.samuelunknown.library.extensions.updateMargins
import com.samuelunknown.library.presentation.imageLoader.ImageLoaderFactoryHolder
import com.samuelunknown.library.presentation.model.GalleryAction
import com.samuelunknown.library.presentation.model.GalleryState
import com.samuelunknown.library.presentation.ui.savedStateViewModel
import com.samuelunknown.library.presentation.ui.screen.gallery.recycler.GalleryAdapter
import com.samuelunknown.library.presentation.ui.screen.gallery.recycler.GalleryItemAnimator
import com.samuelunknown.library.presentation.ui.screen.gallery.recycler.GridSpacingItemDecoration
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class GalleryFragment private constructor(
    private val onResultAction: (ImagesResultDto) -> Unit
) : BottomSheetDialogFragment() {

    // region Properties
    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!

    private val screenHeight: Int by lazy { requireActivity().getScreenHeight() }

    private val peekHeight: Int by lazy { (screenHeight * PEEK_HEIGHT_PERCENTAGE).roundToInt() }

    private val pickButtonDefaultBottomMargin: Int by lazy { binding.pickupButton.marginBottom }

    private val bottomSheet: BottomSheetDialog
        get() = requireDialog() as BottomSheetDialog

    private val galleryAdapter = GalleryAdapter(
        imageLoaderFactory = ImageLoaderFactoryHolder.imageLoaderFactory,
        changeSelectionAction = { item ->
            lifecycleScope.launch {
                vm.actionFlow.emit(GalleryAction.ChangeSelectionAction(item))
            }
        }
    )

    private val vm: GalleryFragmentVm by savedStateViewModel {
        GalleryFragmentVmFactory(GetImagesUseCaseImpl(requireContext().contentResolver))
    }
    // endregion

    // region Lifecycle
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRootView()
        initBottomSheetDialog()
        initToolbar()
        initRecycler()
        initPickupButton()
        initSubscriptions()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recycler.adapter = null
        _binding = null
    }

    override fun onDismiss(dialog: DialogInterface) {
        onResultAction(ImagesResultDto.Success())
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
            title = "Gallery",
            displayHomeAsUpEnabled = true,
            navigationAction = { dismiss() }
        )
    }

    private fun initRecycler() {
        binding.recycler.apply {
            adapter = galleryAdapter
            itemAnimator = GalleryItemAnimator()
            addItemDecoration(
                GridSpacingItemDecoration(
                    spanCount = SPAN_COUNT,
                    spacing = resources.getDimension(R.dimen.gallery_image_picker__grid_spacing).roundToInt()
                )
            )
            (layoutManager as GridLayoutManager).spanCount = SPAN_COUNT
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
                    Log.d(TAG, "State: $state")
                    when (state) {
                        is GalleryState.Init -> {
                            binding.pickupButton.isVisible = false
                        }
                        is GalleryState.Loaded -> {
                            binding.pickupButton.isVisible = true
                            galleryAdapter.updateItems(state.items)
                        }
                        is GalleryState.Picked -> {
                            onResultAction(state.result)
                            dismiss()
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

        // since pickupButton position changed we need update Recycler bottom padding
        updateRecyclerBottomPadding()
    }

    private fun updateRecyclerBottomPadding() {
        binding.pickupButton.doOnLayout {
            binding.recycler.updatePadding(bottom = screenHeight - it.top)
        }
    }

    companion object {
        private val TAG = GalleryFragment::class.java.simpleName
        private const val SPAN_COUNT = 3
        private const val PEEK_HEIGHT_PERCENTAGE = 0.7
        private const val IS_BOTTOM_SHEET_USED = true

        fun init(
            onResultAction: (ImagesResultDto) -> Unit = {}
        ) = GalleryFragment(
            onResultAction = onResultAction
        )
    }
}