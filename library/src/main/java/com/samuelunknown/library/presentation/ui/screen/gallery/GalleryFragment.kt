package com.samuelunknown.library.presentation.ui.screen.gallery

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.samuelunknown.library.databinding.FragmentGalleryBinding
import com.samuelunknown.library.domain.GetImagesUseCaseImpl
import com.samuelunknown.library.presentation.model.ImagesResultDto
import com.samuelunknown.library.presentation.ui.savedStateViewModel

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

    private lateinit var bottomSheet: BottomSheetDialog
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>

    private val vm: GalleryFragmentVm by savedStateViewModel {
        GalleryFragmentVmFactory(GetImagesUseCaseImpl(requireContext().contentResolver))
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FragmentGalleryBinding.inflate(LayoutInflater.from(context))
        bottomSheet = (super.onCreateDialog(savedInstanceState) as BottomSheetDialog)

        initBottomSheetDialog()
        initRootViewSizes()

        return bottomSheet
    }

    private fun initBottomSheetDialog(){
        bottomSheet.setContentView(binding.root)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDismiss(dialog: DialogInterface) {
        onCancelAction()
        super.onDismiss(dialog)
    }

    companion object {
        fun init(
            onAcceptAction: (ImagesResultDto) -> Unit = {},
            onCancelAction: () -> Unit = {}
        ) = GalleryFragment(
            onAcceptAction = onAcceptAction,
            onCancelAction = onCancelAction
        )
    }
}