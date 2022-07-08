package com.like.recyclerview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import com.like.recyclerview.utils.findFirstVisibleItemPosition
import com.like.recyclerview.utils.findLastVisibleItemPosition
import com.like.recyclerview.viewholder.BindingViewHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 加载状态 Adapter
 */
abstract class BaseLoadStateAdapter<VB : ViewDataBinding> : RecyclerView.Adapter<BindingViewHolder<VB>>() {
    private val hasMore = AtomicBoolean(false)
    internal var onLoadMore: suspend () -> Unit = {}
    private lateinit var mHolder: BindingViewHolder<VB>
    private lateinit var recyclerView: RecyclerView
    internal var isAfter: Boolean = true
    private val onScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            // 此回调在添加 item 时也会触发，但是重新清除所有并添加的 item 如果和上一次的一样多，则不会触发（比如刷新时）
            // 所以只靠此方法触发加载更多不行，需要在 onBindViewHolder 方法中也触发以处理上述情况。
            if (newState == SCROLL_STATE_IDLE || isVisible()) {// 注意：newState==SCROLL_STATE_IDLE 有时不能触发，所以需要添加额外的判断。参考：https://blog.csdn.net/wangcheeng/article/details/109722538
                loadMore()// 滚动时触发加载更多
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

    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<VB> {
        return BindingViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), getLayoutId(), parent, false))
    }

    final override fun onBindViewHolder(holder: BindingViewHolder<VB>, position: Int) {
        mHolder = holder
        onBindViewHolder(holder)
        loadMore()// 初始化或者刷新时触发加载更多
    }

    final override fun getItemCount(): Int {
        return 1
    }

    abstract fun getLayoutId(): Int
    open fun onBindViewHolder(holder: BindingViewHolder<VB>) {}

    /**
     * 如果还有更多数据时调用此方法进行标记。
     */
    internal fun hasMore() {
        hasMore.compareAndSet(false, true)
    }

    /**
     * 判断 BaseLoadMoreAdapter 是否可见
     */
    private fun isVisible(): Boolean {
        if (isAfter) {
            if (recyclerView.findLastVisibleItemPosition() != (recyclerView.layoutManager?.itemCount ?: 0) - 1) {
                return false
            }
        } else {
            if (recyclerView.findFirstVisibleItemPosition() != 0) {
                return false
            }
        }
        return true
    }

    /**
     * 加载更多数据
     */
    private fun loadMore() {
        if (!::mHolder.isInitialized) return
        recyclerView.post {// 这里必须用 post，否则 onBindViewHolder 调用此方法时，计算不了 findLastVisibleItemPosition。
            if (!isVisible()) {
                return@post
            }
            if (hasMore.compareAndSet(true, false)) {
                mHolder.itemView.setOnClickListener(null)
                onLoading()
                val context = mHolder.itemView.context
                if (context is LifecycleOwner) {
                    context.lifecycleScope.launch(Dispatchers.Main) {
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
        mHolder.itemView.setOnClickListener(null)
        onEnd()
    }

    /**
     * 请求数据出错时调用此方法更新界面。
     * 此方法中添加了出错重试点击监听。
     */
    internal fun error(throwable: Throwable) {
        if (!::mHolder.isInitialized) return
        mHolder.itemView.setOnClickListener {
            hasMore.set(true)
            loadMore()
        }
        onError(throwable)
    }

    abstract fun onLoading()
    abstract fun onEnd()
    abstract fun onError(throwable: Throwable)
}
