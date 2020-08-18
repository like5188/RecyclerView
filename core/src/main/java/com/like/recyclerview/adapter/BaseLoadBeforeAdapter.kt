package com.like.recyclerview.adapter

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.like.recyclerview.layoutmanager.WrapLinearLayoutManager
import com.like.recyclerview.model.IEmptyItem
import com.like.recyclerview.model.IErrorItem
import com.like.recyclerview.model.IRecyclerViewItem
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

    private lateinit var recyclerView: RecyclerView

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
        mLoadBeforeRunning.set(false)
        if (!::recyclerView.isInitialized) {
            return
        }
        if (mAdapterDataManager.getItemCount() == pageSize) {
            // 初始化或者刷新时，RecyclerView自动滚动到最底部。
            recyclerView.scrollToPosition(itemCount - 1)
        } else {
            // 做类似于聊天界面的处理
            val layoutManager = recyclerView.layoutManager
            if (layoutManager is WrapLinearLayoutManager) {
                // 第一个item的视图
                layoutManager.getChildAt(mAdapterDataManager.getHeaderCount())?.let {
                    val offset = it.top
                    val position = layoutManager.getPosition(it)
                    layoutManager.scrollToPositionWithOffset(itemCount + position, offset)
                }
            }
        }
    }

    override fun onGetItem(position: Int, item: IRecyclerViewItem?) {
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
