package com.samuelunknown.library.presentation.ui.screen.gallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.samuelunknown.library.databinding.ItemImageGalleryBinding
import com.samuelunknown.library.presentation.model.GalleryItem

class GalleryAdapter : RecyclerView.Adapter<GalleryAdapter.GalleryItemViewHolder>() {

    private val asyncListDiffer: AsyncListDiffer<GalleryItem> = AsyncListDiffer(
        this,
        GalleryItemDiffItemCallback()
    )

    private val items: List<GalleryItem>
        get() = asyncListDiffer.currentList

    fun updateItems(newItems: List<GalleryItem>) {
        asyncListDiffer.submitList(newItems)
    }

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryItemViewHolder {
        val binding = ItemImageGalleryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return GalleryItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GalleryItemViewHolder, position: Int) {
        when (val item = items[position]) {
            is GalleryItem.Image -> holder.bind(item)
        }
    }

    override fun onViewRecycled(holder: GalleryItemViewHolder) {
        holder.clear()
    }

    inner class GalleryItemViewHolder(
        private val binding: ItemImageGalleryBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(imageItem: GalleryItem.Image) {
            with(binding) {
                // todo set image with url from imageItem

                root.setOnClickListener {
                    val clickedItem = items[layoutPosition] as GalleryItem.Image
                }
            }
        }

        fun clear() {
            binding.root.setOnClickListener(null)
        }
    }
}