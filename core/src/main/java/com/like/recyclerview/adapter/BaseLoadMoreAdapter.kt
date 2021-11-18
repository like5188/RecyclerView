package com.like.recyclerview.adapter

import android.util.Log
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.like.recyclerview.viewholder.BindingViewHolder
import kotlinx.coroutines.launch

/**
 * 封装了加载更多逻辑，用于显示加载状态的 header（往前加载更多） 或者 footer（往后加载更多）
 */
open class BaseLoadMoreAdapter<VB : ViewDataBinding, ValueInList> : BaseErrorAdapter<VB, ValueInList>() {
    companion object {
        private const val TAG = "AbstractLoadMoreAdapter"
    }

    internal var onLoadMore: suspend () -> Unit = {}
    private lateinit var mHolder: BindingViewHolder<VB>

    override fun onBindViewHolder(holder: BindingViewHolder<VB>, binding: VB, position: Int, item: ValueInList) {
        super.onBindViewHolder(holder, binding, position, item)
        mHolder = holder
        load()
    }

    private fun load() {
        val context = mHolder.itemView.context
        if (context is LifecycleOwner) {
            context.lifecycleScope.launch {
                Log.v(TAG, "触发加载更多")
                onLoadMore()
            }
        }
    }

    /**
     * 重新加载数据，从而触发 onBindViewHolder 方法，触发加载更多逻辑。
     * 因为在数据量太少时，比如 pagesize==1，不能多次触发加载更多。
     */
    fun reload() {
        val data = get(0) ?: return
        clear()
        addToEnd(data)
    }

    /**
     * 请求数据时调用此方法。子类可以重写此方法进行界面更新。
     */
    open fun onLoading() {
        if (!::mHolder.isInitialized) return
        mHolder.binding.root.setOnClickListener(null)
    }

    /**
     * 没有更多数据时调用此方法。子类可以重写此方法进行界面更新。
     */
    open fun onEnd() {
        if (!::mHolder.isInitialized) return
        mHolder.binding.root.setOnClickListener(null)
    }

    /**
     * 请求数据出错时调用此方法。子类可以重写此方法进行界面更新。
     * 此方法中添加了出错重试点击监听。
     */
    override fun onError(throwable: Throwable) {
        super.onError(throwable)
        if (!::mHolder.isInitialized) return
        mHolder.binding.root.setOnClickListener {
            onLoading()
            load()
        }
    }
}
