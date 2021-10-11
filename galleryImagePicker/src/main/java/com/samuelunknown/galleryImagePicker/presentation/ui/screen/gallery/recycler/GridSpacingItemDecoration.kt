package com.samuelunknown.galleryImagePicker.presentation.ui.screen.gallery.recycler

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

internal class GridSpacingItemDecoration(
    private val spanCount: Int,
    private val spacing: Int
) : ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        val column = position % spanCount

        outRect.apply {
            left = spacing - column * spacing / spanCount
            right = (column + 1) * spacing / spanCount
            top = if (position < spanCount) spacing else top
            bottom = spacing
        }
    }
}