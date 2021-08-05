package com.like.recyclerview.ext.adapter

import android.content.Context
import android.view.animation.ScaleAnimation
import android.widget.Toast
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.like.recyclerview.adapter.AbstractAdapter

/**
 * 添加图片的适配器。带+号，可拖拽交换位置，不使用分页
 */
open class AbstractAddImageViewAdapter<VB : ViewDataBinding, ValueInList>(
    recyclerView: RecyclerView,
    private val plus: ValueInList,
    private val maxImageCount: Int = 9
) : AbstractAdapter<VB, ValueInList>() {
    private val context: Context = recyclerView.context

    init {
        addToEnd(plus)
        ItemTouchHelper(object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                val dragFlags = when {
                    isPlus(viewHolder) -> 0// +号图片不让拖拽
                    recyclerView.layoutManager is GridLayoutManager -> ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                    else -> ItemTouchHelper.UP or ItemTouchHelper.DOWN
                }
                return makeMovementFlags(dragFlags, 0)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                if (isPlus(target)) {// +号图片不让替换位置
                    return false
                }
                moveItem(viewHolder.adapterPosition, target.adapterPosition)
                return true
            }

            override fun onSwiped(p0: RecyclerView.ViewHolder, p1: Int) {
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
                    // 刷新position，避免拖拽导致的位置错乱。
                    // 因为mAdapterDataManager.moveItem()方法没有更新位置，放到了这里更新位置（如果在onMove()的时候更新的话，会导致拖拽bug）。
                    notifyDataSetChanged()
                }
            }

        }).attachToRecyclerView(recyclerView)
    }

    /**
     * 获取除开+号以外的其它数据
     */
    fun getItemsExceptPlus(): List<ValueInList> {
        val items = mutableListOf<ValueInList>()
        mList.forEach {
            if (it != plus) {
                items.add(it)
            }
        }
        return items
    }

    fun addItems(items: List<ValueInList>) {
        val curImageCount = getItemCountExceptPlus()
        when {
            curImageCount + items.size < maxImageCount -> {// 不删除+号图片
                // 添加到+号之前
                addAll(itemCount - 1, items)
            }
            curImageCount + items.size == maxImageCount -> {// 删除+号图片
                remove(plus)
                addAllToEnd(items)
            }
            else -> {// 不能添加
                Toast.makeText(context, "只能添加 $maxImageCount 张图片", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun removeItem(item: ValueInList) {
        val curImageCount = getItemCountExceptPlus()
        when {
            curImageCount < maxImageCount -> {// 不添加+号图片
                remove(item)
            }
            curImageCount == maxImageCount -> {// 添加+号图片
                remove(item)
                addToEnd(plus)
            }
        }
    }

    /**
     * 是否+号图片
     */
    private fun isPlus(holder: RecyclerView.ViewHolder): Boolean =
        get(holder.bindingAdapterPosition) == plus

    /**
     * 除了+号的其它图片数量
     */
    private fun getItemCountExceptPlus() = mList.count { it != plus }

}