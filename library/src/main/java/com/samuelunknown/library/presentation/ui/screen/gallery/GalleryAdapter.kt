package com.samuelunknown.library.presentation.ui.screen.gallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.samuelunknown.library.databinding.ItemImageGalleryBinding
import com.samuelunknown.library.presentation.imageLoader.ImageLoader
import com.samuelunknown.library.presentation.imageLoader.ImageLoaderFactory
import com.samuelunknown.library.presentation.model.GalleryItem

class GalleryAdapter(
    private val imageLoaderFactory: ImageLoaderFactory
) : RecyclerView.Adapter<GalleryAdapter.GalleryItemViewHolder>() {

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
        private val imageLoader: ImageLoader = imageLoaderFactory.create()

        fun bind(imageItem: GalleryItem.Image) {
            with(binding) {
                imageLoader.load(imageView = image, uri = imageItem.uri)
                root.setOnClickListener {
                    val clickedItem = items[layoutPosition] as GalleryItem.Image
                }
            }
        }

        fun clear() {
            with(binding) {
                imageLoader.cancel(image)
                root.setOnClickListener(null)
            }
        }
    }
}