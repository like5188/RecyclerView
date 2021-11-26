package com.like.recyclerview.utils

import android.view.animation.ScaleAnimation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.like.recyclerview.adapter.BaseAdapter

/*
* 使用方式：在 adapter 中
private val mItemTouchHelper by lazy {
    ItemTouchHelper(ItemTouchHelperCallback(this))
}

override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
    super.onAttachedToRecyclerView(recyclerView)
    mItemTouchHelper.attachToRecyclerView(recyclerView)
}
*/
/**
 * 支持拖拽 item 的 [ItemTouchHelper.Callback]。
 *
 * @param adapter                       数据的 Adapter
 * @param forbidsDraggingItemPosition   禁止拖拽的 item 的位置，默认 -1，表示没有需要禁止拖拽的 item。
 */
class ItemTouchHelperCallback(
    private val adapter: BaseAdapter<*, *>,
    private val forbidsDraggingItemPosition: Int = -1,
) : ItemTouchHelper.Callback() {

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        if (viewHolder.bindingAdapter != adapter || viewHolder.bindingAdapterPosition == forbidsDraggingItemPosition) {// 避免操作 PlusAdapter
            return makeMovementFlags(0, 0)
        }
        val dragFlags = when (recyclerView.layoutManager) {
            is GridLayoutManager, is StaggeredGridLayoutManager -> ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            else -> ItemTouchHelper.UP or ItemTouchHelper.DOWN
        }
        return makeMovementFlags(dragFlags, 0)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        if (viewHolder.bindingAdapter != adapter || target.bindingAdapterPosition == forbidsDraggingItemPosition) {
            return false
        }
        val fromPosition = viewHolder.bindingAdapterPosition
        val toPosition = target.bindingAdapterPosition// 不能使用 absoluteAdapterPosition，否则当移动到加号上去会崩溃。
        adapter.moveItem(fromPosition, toPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        // 当长按选中item的时候（拖拽开始的时候）调用
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            //当拖拽选中时放大选中的view
            viewHolder?.itemView?.apply {
                val scaleAnimation = ScaleAnimation(1f, 1.1f, 1f, 1.1f, width / 2f, height / 2f)
                scaleAnimation.duration = 200
                scaleAnimation.fillAfter = true
                startAnimation(scaleAnimation)
            }
        }
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        // 当手指松开的时候（拖拽完成的时候）调用。当需要清除之前在onSelectedChanged或者onChildDraw,onChildDrawOver设置的状态或者动画时执行
        if (!recyclerView.isComputingLayout) {
            viewHolder.itemView.apply {
                val scaleAnimation = ScaleAnimation(1.1f, 1f, 1.1f, 1f, width / 2f, height / 2f)
                scaleAnimation.duration = 200
                scaleAnimation.fillAfter = true
                startAnimation(scaleAnimation)
            }
        }
    }
}