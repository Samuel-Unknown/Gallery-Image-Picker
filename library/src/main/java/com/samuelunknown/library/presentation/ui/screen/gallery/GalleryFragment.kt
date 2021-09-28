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
import com.samuelunknown.library.databinding.FragmentGalleryBinding
import com.samuelunknown.library.domain.GetImagesUseCaseImpl
import com.samuelunknown.library.domain.model.ImagesResultDto
import com.samuelunknown.library.presentation.imageLoader.ImageLoaderFactoryHolder
import com.samuelunknown.library.presentation.model.GalleryState
import com.samuelunknown.library.presentation.ui.savedStateViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

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
        ImageLoaderFactoryHolder.imageLoaderFactory
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBottomSheetDialog()
        initRootViewSizes()
        initRecyclerView()
        initSubscriptions()
    }

    private fun initBottomSheetDialog() {
        bottomSheet.behavior.apply {
            peekHeight = this@GalleryFragment.peekHeight
            state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun initRootViewSizes() {
        val params = binding.root.layoutParams as FrameLayout.LayoutParams
        params.height = screenHeight
        binding.root.layoutParams = params
    }


    private fun initRecyclerView() {
        binding.recycler.apply {
            adapter = galleryAdapter
            (layoutManager as GridLayoutManager).spanCount = 3
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

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recycler.adapter = null
        _binding = null
    }

    override fun onDismiss(dialog: DialogInterface) {
        onCancelAction()
        super.onDismiss(dialog)
    }

    companion object {
        private val TAG = GalleryFragment::class.java.simpleName

        fun init(
            onAcceptAction: (ImagesResultDto) -> Unit = {},
            onCancelAction: () -> Unit = {}
        ) = GalleryFragment(
            onAcceptAction = onAcceptAction,
            onCancelAction = onCancelAction
        )
    }
}