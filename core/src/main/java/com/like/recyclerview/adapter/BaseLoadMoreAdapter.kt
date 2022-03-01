package com.like.recyclerview.adapter

import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.like.recyclerview.utils.findLastVisibleItemPosition
import com.like.recyclerview.viewholder.BindingViewHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 封装了加载更多逻辑，用于显示加载状态的 header（往前加载更多） 或者 footer（往后加载更多）
 */
abstract class BaseLoadMoreAdapter<VB : ViewDataBinding, ValueInList> : BaseAdapter<VB, ValueInList>() {
    private val hasMore = AtomicBoolean(false)
    internal var onLoadMore: suspend () -> Unit = {}
    private lateinit var mHolder: BindingViewHolder<VB>
    private lateinit var recyclerView: RecyclerView
    private val onScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            // 此回调在添加 item 时也会触发，但是重新清除所有并添加的 item 如果和上一次的一样，则不会触发（比如刷新时）
            // 所以只靠此方法触发加载更多不行，在 hasMore() 方法中也必须触发。否则在上述情况下会不能触发加载更多。
            isLoading()
        }
    }

    /**
     * 是否触发 [loading] 操作
     */
    private fun isLoading() {
        // 判断是否显示了 BaseLoadMoreAdapter
        if (recyclerView.findLastVisibleItemPosition() == (recyclerView.layoutManager?.itemCount ?: 0) - 1) {
            loading()
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
    }

    /**
     * 如果还有更多数据时调用此方法。
     */
    fun hasMore() {
        hasMore.compareAndSet(false, true)
        isLoading()
    }

    /**
     * 请求数据时调用此方法。
     */
    fun loading() {
        if (!::mHolder.isInitialized) return
        if (hasMore.compareAndSet(true, false)) {
            mHolder.binding.root.setOnClickListener(null)
            onLoading()
            val context = mHolder.itemView.context
            if (context is LifecycleOwner) {
                context.lifecycleScope.launch(Dispatchers.Main) {
                    onLoadMore()
                }
            }
        }
    }

    /**
     * 没有更多数据时调用此方法更新界面。
     */
    fun end() {
        if (!::mHolder.isInitialized) return
        mHolder.binding.root.setOnClickListener(null)
        onEnd()
    }

    /**
     * 请求数据出错时调用此方法更新界面。
     * 此方法中添加了出错重试点击监听。
     */
    fun error(throwable: Throwable) {
        if (!::mHolder.isInitialized) return
        mHolder.binding.root.setOnClickListener {
            hasMore.set(true)
            loading()
        }
        onError(throwable)
    }

    abstract fun onLoading()
    abstract fun onEnd()
    abstract fun onError(throwable: Throwable)
}
