package com.samuelunknown.library.presentation.ui.screen.gallery.recycler

import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.motion.widget.TransitionAdapter
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.samuelunknown.library.presentation.model.GalleryItemPayload
import java.util.*

internal class GalleryItemAnimator : DefaultItemAnimator() {

    private val viewHoldersInProgress: WeakHashMap<RecyclerView.ViewHolder, UpdateAction> = WeakHashMap()

    override fun canReuseUpdatedViewHolder(
        viewHolder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>
    ): Boolean = payloads.filterIsInstance<GalleryItemPayload.SelectionPayload>().isNotEmpty()

    override fun recordPreLayoutInformation(
        state: RecyclerView.State,
        viewHolder: RecyclerView.ViewHolder,
        changeFlags: Int,
        payloads: MutableList<Any>
    ): ItemHolderInfo {
        if (changeFlags == FLAG_CHANGED) {
            payloads.filterIsInstance<GalleryItemPayload.SelectionPayload>()
                .firstOrNull()
                ?.let { selectionPayload ->
                    return DilemmaItemHolderInfo(
                        if (selectionPayload.item.isSelected)
                            UpdateAction.Select(counterText = selectionPayload.item.counterText)
                        else
                            UpdateAction.Deselect(counterText = selectionPayload.item.counterText)
                    )
                }
        }

        return super.recordPreLayoutInformation(state, viewHolder, changeFlags, payloads)
    }

    override fun animateChange(
        oldHolder: RecyclerView.ViewHolder,
        newHolder: RecyclerView.ViewHolder,
        preInfo: ItemHolderInfo,
        postInfo: ItemHolderInfo
    ): Boolean {
        if (preInfo is DilemmaItemHolderInfo) {
            cancelCurrentAnimationIfExists(newHolder)
            viewHoldersInProgress.remove(newHolder)

            val holder = newHolder as GalleryAdapter.GalleryItemViewHolder
            animateHolder(holder, preInfo.updateAction)

            return false
        }

        return super.animateChange(oldHolder, newHolder, preInfo, postInfo)
    }

    override fun endAnimation(item: RecyclerView.ViewHolder) {
        super.endAnimation(item)
        cancelCurrentAnimationIfExists(item)
        viewHoldersInProgress.remove(item)
    }

    override fun endAnimations() {
        super.endAnimations()

        // removing by iterator to avoid ConcurrentModificationException
        val iterator = viewHoldersInProgress.iterator()
        while (iterator.hasNext()) {
            cancelCurrentAnimationIfExists(iterator.next().key)
            iterator.remove()
        }
    }

    private fun animateHolder(
        holder: GalleryAdapter.GalleryItemViewHolder,
        updateAction: UpdateAction
    ) {
        val transitionAdapter = object : TransitionAdapter() {
            override fun onTransitionCompleted(motionLayout: MotionLayout, currentId: Int) {
                viewHoldersInProgress.remove(holder)
                dispatchAnimationFinished(holder)
            }
        }

        viewHoldersInProgress[holder] = updateAction
        with(holder.binding) {
            root.setTransitionListener(transitionAdapter)
            when (updateAction) {
                is UpdateAction.Select -> {
                    counter.text = updateAction.counterText
                    root.transitionToEnd()
                }
                is UpdateAction.Deselect -> {
                    root.transitionToStart()
                }
            }
        }
    }

    private fun cancelCurrentAnimationIfExists(item: RecyclerView.ViewHolder) {
        if (viewHoldersInProgress.containsKey(item)) {
            val dilemmaItemViewHolder = viewHoldersInProgress.keys.firstOrNull { it == item }
                    as? GalleryAdapter.GalleryItemViewHolder

            dilemmaItemViewHolder?.let {
                when (val action = viewHoldersInProgress[item]) {
                    is UpdateAction.Select -> {
                        it.binding.root.progress = 1f
                        it.binding.counter.text = action.counterText
                    }
                    is UpdateAction.Deselect -> {
                        it.binding.root.progress = 0f
                        it.binding.counter.text = action.counterText
                    }
                }
            }
        }
    }

    private data class DilemmaItemHolderInfo(val updateAction: UpdateAction) : ItemHolderInfo()

    private sealed class UpdateAction(open val counterText: String) {
        data class Select(override val counterText: String) : UpdateAction(counterText)
        data class Deselect(override val counterText: String) : UpdateAction(counterText)
    }
}