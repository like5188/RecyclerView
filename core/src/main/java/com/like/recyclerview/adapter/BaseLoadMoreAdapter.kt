package com.like.recyclerview.adapter

import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.like.recyclerview.viewholder.BindingViewHolder
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 封装了加载更多逻辑，用于显示加载状态的 header（往前加载更多） 或者 footer（往后加载更多）
 */
abstract class BaseLoadMoreAdapter<VB : ViewDataBinding, ValueInList> : BaseErrorAdapter<VB, ValueInList>() {
    internal val canLoadMore = AtomicBoolean(true)
    internal var onLoadMore: suspend () -> Unit = {}
    private lateinit var mHolder: BindingViewHolder<VB>

    override fun onBindViewHolder(holder: BindingViewHolder<VB>, item: ValueInList) {
        super.onBindViewHolder(holder, item)
        mHolder = holder
        loading()
    }

    /**
     * 请求数据时调用此方法。
     */
    fun loading() {
        if (!::mHolder.isInitialized) return
        if (canLoadMore.compareAndSet(true, false)) {
            mHolder.binding.root.setOnClickListener(null)
            onLoading()
            val context = mHolder.itemView.context
            if (context is LifecycleOwner) {
                context.lifecycleScope.launch {
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
    final override fun error(throwable: Throwable) {
        super.error(throwable)
        if (!::mHolder.isInitialized) return
        mHolder.binding.root.setOnClickListener {
            canLoadMore.set(true)
            loading()
        }
        onError(throwable)
    }

    abstract fun onLoading()
    abstract fun onEnd()
    abstract fun onError(throwable: Throwable)
}
