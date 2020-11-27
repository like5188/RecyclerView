package com.like.recyclerview.adapter

import android.util.Log
import com.like.recyclerview.model.IEmptyItem
import com.like.recyclerview.model.IErrorItem
import com.like.recyclerview.model.IRecyclerViewItem
import com.like.recyclerview.viewholder.CommonViewHolder
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 当需要往后加载更多时使用
 *
 * 做了加载更多的触发条件处理。即最后一个item出现的时候。
 */
open class BaseLoadAfterAdapter(private val onLoadAfter: () -> Unit) : BaseAdapter() {
    companion object {
        private const val TAG = "BaseLoadAfterAdapter"
    }

    private var mLoadAfterRunning = AtomicBoolean(false)

    override fun onItemRangeInsertedForLoadMore(positionStart: Int, itemCount: Int) {
        mLoadAfterRunning.set(false)
    }

    override fun onBindViewHolder(holder: CommonViewHolder, position: Int, item: IRecyclerViewItem?) {
        super.onBindViewHolder(holder, position, item)
        if (item !is IEmptyItem && item !is IErrorItem) {
            if (position == itemCount - 1) {
                if (mLoadAfterRunning.compareAndSet(false, true)) {
                    Log.v(TAG, "触发往后加载更多")
                    onLoadAfter()
                }
            }
        }
    }

}
