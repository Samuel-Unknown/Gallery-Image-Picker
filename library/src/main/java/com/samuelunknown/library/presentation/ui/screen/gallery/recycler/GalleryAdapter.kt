package com.samuelunknown.library.presentation.ui.screen.gallery.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.doOnAttach
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.samuelunknown.library.databinding.ItemImageGalleryBinding
import com.samuelunknown.library.presentation.imageLoader.ImageLoader
import com.samuelunknown.library.presentation.imageLoader.ImageLoaderFactory
import com.samuelunknown.library.presentation.model.GalleryItem

class GalleryAdapter(
    private val imageLoaderFactory: ImageLoaderFactory,
    private val changeSelectionAction: (GalleryItem.Image) -> Unit
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

    override fun onBindViewHolder(
        holder: GalleryItemViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    override fun onViewRecycled(holder: GalleryItemViewHolder) {
        holder.clear()
    }

    inner class GalleryItemViewHolder(
        val binding: ItemImageGalleryBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private val imageLoader: ImageLoader = imageLoaderFactory.create()

        fun bind(imageItem: GalleryItem.Image) {
            with(binding) {
                root.doOnAttach {
                    root.progress = if (imageItem.isSelected) 1f else 0f
                }

                counter.text = imageItem.counterText
                imageLoader.load(imageView = image, uri = imageItem.uri)
                root.setOnClickListener {
                    val clickedItem = items[layoutPosition] as GalleryItem.Image
                    changeSelectionAction(clickedItem)
                }
                root.setOnLongClickListener {
                    val clickedItem = items[layoutPosition] as GalleryItem.Image
                    changeSelectionAction(clickedItem)
                    true
                }
            }
        }

        fun clear() {
            with(binding) {
                imageLoader.cancel(image)
                root.setOnClickListener(null)
                root.setOnLongClickListener(null)
            }
        }
    }
}