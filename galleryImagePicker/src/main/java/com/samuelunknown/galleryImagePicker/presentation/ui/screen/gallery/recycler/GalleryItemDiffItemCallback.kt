package com.samuelunknown.galleryImagePicker.presentation.ui.screen.gallery.recycler

import androidx.recyclerview.widget.DiffUtil
import com.samuelunknown.galleryImagePicker.presentation.model.GalleryItem
import com.samuelunknown.galleryImagePicker.presentation.model.GalleryItemPayload

internal class GalleryItemDiffItemCallback : DiffUtil.ItemCallback<GalleryItem>() {
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

    override fun getChangePayload(oldItem: GalleryItem, newItem: GalleryItem): Any? {
        return when {
            oldItem is GalleryItem.Image && newItem is GalleryItem.Image -> {
                if (oldItem.counter != newItem.counter) {
                    GalleryItemPayload.SelectionPayload(newItem)
                } else {
                    super.getChangePayload(oldItem, newItem)
                }
            }
            else -> super.getChangePayload(oldItem, newItem)
        }
    }
}