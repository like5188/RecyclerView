package com.like.recyclerview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
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

/**
 * 加载状态 Adapter
 *
 * 处理了3种触发加载更多的情况：1、数据插入时触发；2、滚动界面触发；3、加载失败后由点击事件触发；
 */
abstract class BaseLoadStateAdapter<VB : ViewDataBinding> : RecyclerView.Adapter<BindingViewHolder<VB>>() {
    internal var onLoadMore: suspend () -> Unit = {}
    private lateinit var mHolder: BindingViewHolder<VB>
    private lateinit var recyclerView: RecyclerView
    internal var isAfter: Boolean = true

    private val onScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            // onScrolled 在添加 item 时也会触发，但是刷新后，如果添加的 item 和上一次的一样多，则不会触发。
            // 所以只靠此方法触发加载更多不行，需要在 hasMore 方法中也触发以处理上述情况。
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                // onScrollStateChanged 在添加 item 时不会触发，所以刷新时不会触发。
                // 所以只靠此方法触发加载更多不行，需要在 hasMore 方法中也触发以处理上述情况。
                // 滚动界面触发
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

    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<VB> {
        return BindingViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), getLayoutId(), parent, false))
    }

    final override fun onBindViewHolder(holder: BindingViewHolder<VB>, position: Int) {
        mHolder = holder
        onBindViewHolder(holder)
    }

    final override fun getItemCount(): Int {
        return 1
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
    internal fun loadMore() {
        if (!::mHolder.isInitialized) return
        if (!isVisible()) {
            return
        }
        mHolder.itemView.setOnClickListener(null)
        val context = mHolder.itemView.context
        if (context is LifecycleOwner) {
            context.lifecycleScope.launch(Dispatchers.Main) {
                onLoading()
                Logger.w("loadMore")
                onLoadMore()
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
            // 加载失败后由点击事件触发
            loadMore()
        }
        onError(throwable)
    }

    abstract fun onLoading()
    abstract fun onEnd()
    abstract fun onError(throwable: Throwable)
    abstract fun getLayoutId(): Int
    open fun onBindViewHolder(holder: BindingViewHolder<VB>) {}
}
