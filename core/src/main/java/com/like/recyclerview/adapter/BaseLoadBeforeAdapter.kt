package com.like.recyclerview.adapter

import android.util.Log
import com.like.recyclerview.layoutmanager.WrapLinearLayoutManager
import com.like.recyclerview.model.IEmptyItem
import com.like.recyclerview.model.IErrorItem
import com.like.recyclerview.model.IRecyclerViewItem
import com.like.recyclerview.viewholder.CommonViewHolder
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 当需要往前加载更多时使用
 *
 * 做了加载更多的触发条件处理。即第一个item出现的时候。
 * 做了滚动处理：
 * 1、初始化或者刷新时，RecyclerView自动滚动到最底部。
 * 2、往前加载更多时，做类似于聊天界面的处理。
 */
open class BaseLoadBeforeAdapter(
    private val pageSize: Int,
    private val onLoadBefore: () -> Unit
) : BaseAdapter() {
    companion object {
        private const val TAG = "BaseLoadBeforeAdapter"
    }

    private var mLoadBeforeRunning = AtomicBoolean(false)

    override fun onItemRangeInsertedForLoadMore(positionStart: Int, itemCount: Int) {
        mLoadBeforeRunning.set(false)
        val rv = recyclerView ?: return
        if (getItemCount() == pageSize) {
            // 初始化或者刷新时，RecyclerView自动滚动到最底部。
            rv.scrollToPosition(itemCount - 1)
        } else {
            // 做类似于聊天界面的处理
            val layoutManager = rv.layoutManager
            if (layoutManager is WrapLinearLayoutManager) {
                // 第一个item的视图
                layoutManager.getChildAt(getHeaders().size)?.let {
                    val offset = it.top
                    val position = layoutManager.getPosition(it)
                    layoutManager.scrollToPositionWithOffset(itemCount + position, offset)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: CommonViewHolder, position: Int, item: IRecyclerViewItem?) {
        super.onBindViewHolder(holder, position, item)
        if (item !is IEmptyItem && item !is IErrorItem) {
            if (position == 0) {
                if (mLoadBeforeRunning.compareAndSet(false, true)) {
                    Log.v(TAG, "触发往前加载更多")
                    onLoadBefore()
                }
            }
        }
    }

}
