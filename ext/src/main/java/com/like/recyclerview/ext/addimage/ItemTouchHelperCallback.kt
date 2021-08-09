package com.like.recyclerview.ext.addimage

import android.util.Log
import android.view.animation.ScaleAnimation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.like.recyclerview.adapter.AbstractAdapter
import java.util.*

/**
 * 支持拖拽 item 的 [ItemTouchHelper.Callback]。
 */
class ItemTouchHelperCallback(private val adapter: AbstractAdapter<*, *>) : ItemTouchHelper.Callback() {
    companion object {
        private val TAG = "ItemTouchHelperCallback"
    }

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val dragFlags = when (recyclerView.layoutManager) {
            is GridLayoutManager, is StaggeredGridLayoutManager -> ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            else -> ItemTouchHelper.UP or ItemTouchHelper.DOWN
        }
        Log.e(TAG, "getMovementFlags")
        return makeMovementFlags(dragFlags, 0)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        val fromPosition = viewHolder.bindingAdapterPosition
        val toPosition = target.bindingAdapterPosition
        Log.e(TAG, "onMove fromPosition=$fromPosition toPosition=$toPosition")
        Collections.swap(adapter.mList, fromPosition, toPosition)
        adapter.notifyItemMoved(fromPosition, toPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        Log.e(TAG, "onSwiped")
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
        Log.e(TAG, "onSelectedChanged actionState=$actionState")
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
        Log.e(TAG, "clearView")
    }
}