package com.samuelunknown.library.presentation.ui.screen.gallery

import android.content.DialogInterface
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.samuelunknown.library.R
import com.samuelunknown.library.databinding.FragmentGalleryBinding
import com.samuelunknown.library.domain.GetImagesUseCaseImpl
import com.samuelunknown.library.domain.model.ImagesResultDto
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
    private val onAcceptAction: (ImagesResultDto) -> Unit,
    private val onCancelAction: () -> Unit,
) : BottomSheetDialogFragment() {
    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!

    private val screenHeight: Int by lazy {
        val rectangle = Rect()
        requireActivity().window.decorView.getWindowVisibleDisplayFrame(rectangle)
        rectangle.height()
    }

    private val peekHeight: Int by lazy { screenHeight / 3 }

    private val bottomSheet: BottomSheetDialog
        get() = requireDialog() as BottomSheetDialog

    private val vm: GalleryFragmentVm by savedStateViewModel {
        GalleryFragmentVmFactory(GetImagesUseCaseImpl(requireContext().contentResolver))
    }

    private val galleryAdapter = GalleryAdapter(
        imageLoaderFactory = ImageLoaderFactoryHolder.imageLoaderFactory,
        changeSelectionAction = { item ->
            lifecycleScope.launch {
                vm.actionFlow.emit(GalleryAction.ChangeSelectionAction(item))
            }
        }
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBottomSheetDialog()
        initRootView()
        initRecyclerView()
        initSubscriptions()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recycler.adapter = null
        _binding = null
    }

    override fun onDismiss(dialog: DialogInterface) {
        onCancelAction()
        super.onDismiss(dialog)
    }

    private fun initBottomSheetDialog() {
        bottomSheet.behavior.apply {
            peekHeight = this@GalleryFragment.peekHeight
            state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun initRootView() {
        val params = binding.root.layoutParams as FrameLayout.LayoutParams
        params.height = screenHeight
        binding.root.layoutParams = params
    }

    private fun initRecyclerView() {
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

    private fun initSubscriptions() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.stateFlow.collect { state ->
                    Log.d(TAG, "State: $state")
                    when (state) {
                        is GalleryState.Init -> {
                            // todo
                        }
                        is GalleryState.Loaded -> {
                            galleryAdapter.updateItems(state.items)
                        }
                    }
                }
            }
        }
    }

    companion object {
        private val TAG = GalleryFragment::class.java.simpleName
        private const val SPAN_COUNT = 3
        
        fun init(
            onAcceptAction: (ImagesResultDto) -> Unit = {},
            onCancelAction: () -> Unit = {}
        ) = GalleryFragment(
            onAcceptAction = onAcceptAction,
            onCancelAction = onCancelAction
        )
    }
}