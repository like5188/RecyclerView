package com.like.recyclerview.adapter

import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.like.common.util.Logger
import com.like.recyclerview.utils.findFirstVisibleItemPosition
import com.like.recyclerview.utils.findLastVisibleItemPosition
import com.like.recyclerview.viewholder.BindingViewHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 加载状态 Adapter
 */
abstract class BaseLoadStateAdapter<VB : ViewDataBinding, ValueInList> : BaseAdapter<VB, ValueInList>() {
    private val hasMore = AtomicBoolean(false)
    internal var onLoadMore: suspend () -> Unit = {}
    private lateinit var mHolder: BindingViewHolder<VB>
    private lateinit var recyclerView: RecyclerView
    internal var isAfter: Boolean = true
    private val onScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            Logger.e("onScrolled")
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            // 此回调在添加 item 时也会触发，但是重新清除所有并添加的 item 如果和上一次的一样多，则不会触发（比如刷新时）
            // 所以只靠此方法触发加载更多不行，需要在 onBindViewHolder 方法中也触发以处理上述情况。
            Logger.e("onScrollStateChanged newState=$newState")
            if (newState == 0) {
                loadMore()
            }
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
        recyclerView.addOnScrollListener(onScrollListener)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        recyclerView.removeOnScrollListener(onScrollListener)
    }

    override fun onBindViewHolder(holder: BindingViewHolder<VB>, item: ValueInList) {
        super.onBindViewHolder(holder, item)
        mHolder = holder
        Logger.e("onBindViewHolder")
        loadMore()
    }

    /**
     * 如果还有更多数据时调用此方法进行标记。
     */
    internal fun hasMore() {
        hasMore.compareAndSet(false, true)
        Logger.e("hasMore")
    }

    /**
     * 加载更多数据
     */
    private fun loadMore() {
        Logger.e("loadMore")
        if (!::mHolder.isInitialized) return
        recyclerView.post {// 这里必须用 post，否则 onBindViewHolder 调用此方法时，计算不了 findLastVisibleItemPosition。
            // 判断是否显示了 BaseLoadMoreAdapter
            if (isAfter) {
                if (recyclerView.findLastVisibleItemPosition() != (recyclerView.layoutManager?.itemCount ?: 0) - 1) {
                    return@post
                }
            } else {
                if (recyclerView.findFirstVisibleItemPosition() != 0) {
                    return@post
                }
            }
            if (hasMore.compareAndSet(true, false)) {
                mHolder.binding.root.setOnClickListener(null)
                onLoading()
                val context = mHolder.itemView.context
                if (context is LifecycleOwner) {
                    context.lifecycleScope.launch(Dispatchers.Main) {
                        Logger.e("onLoadMore")
                        onLoadMore()
                    }
                }
            }
        }
    }

    /**
     * 没有更多数据时调用此方法更新界面。
     */
    internal fun end() {
        if (!::mHolder.isInitialized) return
        mHolder.binding.root.setOnClickListener(null)
        onEnd()
    }

    /**
     * 请求数据出错时调用此方法更新界面。
     * 此方法中添加了出错重试点击监听。
     */
    internal fun error(throwable: Throwable) {
        if (!::mHolder.isInitialized) return
        mHolder.binding.root.setOnClickListener {
            hasMore.set(true)
            loadMore()
        }
        onError(throwable)
    }

    abstract fun onLoading()
    abstract fun onEnd()
    abstract fun onError(throwable: Throwable)
}
