package com.like.recyclerview.adapter

import android.util.Log
import androidx.databinding.ViewDataBinding
import com.like.recyclerview.layoutmanager.WrapLinearLayoutManager
import com.like.recyclerview.viewholder.BindingViewHolder
import java.util.concurrent.atomic.AtomicBoolean

abstract class AbstractLoadMoreHeaderAdapter<VB : ViewDataBinding, Data>(private val pageSize: Int) : AbstractAdapter<VB, Data>() {
    companion object {
        private const val TAG = "AbstractHeaderAdapter"
    }

    private var isRunning = AtomicBoolean(false)

    fun onItemRangeInserted(itemCount: Int) {
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
            Log.v(TAG, "触发加载更多")
            try {
                if (onLoad()) {
                    onComplete()
                } else {
                    onEnd()
                }
            } catch (e: Exception) {
                onError(e)
            } finally {
                isRunning.compareAndSet(true, false)
            }
        }
    }

    abstract fun onLoad(): Boolean
    abstract fun onComplete()
    abstract fun onEnd()
    abstract fun onError(throwable: Throwable)
}
