package com.like.recyclerview.adapter

import android.util.Log
import androidx.databinding.ViewDataBinding
import com.like.recyclerview.layoutmanager.WrapLinearLayoutManager
import com.like.recyclerview.viewholder.BindingViewHolder
import java.util.concurrent.atomic.AtomicBoolean

abstract class AbstractHeaderAdapter<VB : ViewDataBinding, Data>(
    private val pageSize: Int,
    private val onLoadBefore: () -> Unit,
) : AbstractAdapter<VB, Data>() {
    companion object {
        private const val TAG = "AbstractHeaderAdapter"
    }

    private var isRunning = AtomicBoolean(false)

    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
        isRunning.set(false)
        val rv = recyclerView ?: return
        if (getItemCount() == pageSize) {
            // 初始化或者刷新时，RecyclerView自动滚动到最底部。
            rv.scrollToPosition(itemCount - 1)
        } else {
            // 做类似于聊天界面的处理
            val layoutManager = rv.layoutManager
            if (layoutManager is WrapLinearLayoutManager) {
                // 第一个item的视图
                layoutManager.getChildAt(0)?.let {
                    val offset = it.top
                    val position = layoutManager.getPosition(it)
                    layoutManager.scrollToPositionWithOffset(itemCount + position, offset)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: BindingViewHolder<VB>, position: Int) {
        if (isRunning.compareAndSet(false, true)) {
            Log.v(TAG, "触发往前加载更多")
            onLoadBefore()
        }
    }
}
