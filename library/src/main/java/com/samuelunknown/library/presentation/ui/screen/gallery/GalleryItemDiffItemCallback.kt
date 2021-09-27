package com.samuelunknown.library.presentation.ui.screen.gallery

import androidx.recyclerview.widget.DiffUtil
import com.samuelunknown.library.presentation.model.GalleryItem

class GalleryItemDiffItemCallback : DiffUtil.ItemCallback<GalleryItem>() {
    override fun areItemsTheSame(oldItem: GalleryItem, newItem: GalleryItem): Boolean {
        return when {
            oldItem is GalleryItem.Image && newItem is GalleryItem.Image -> {
                oldItem.uri == newItem.uri
            }
            else -> false
        }
     }

    override fun areContentsTheSame(oldItem: GalleryItem, newItem: GalleryItem): Boolean {
        return oldItem == newItem
    }
}