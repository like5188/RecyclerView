package com.like.recyclerview.adapter

import android.util.Log
import androidx.databinding.ViewDataBinding
import com.like.recyclerview.viewholder.BindingViewHolder
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 封装了加载更多逻辑，用于显示加载状态的 header（往前加载更多） 或者 footer（往后加载更多）
 */
abstract class AbstractLoadMoreAdapter<VB : ViewDataBinding, Data>(private val onLoad: () -> Unit) : AbstractAdapter<VB, Data>() {
    companion object {
        private const val TAG = "AbstractLoadMoreAdapter"
    }

    private var isRunning = AtomicBoolean(true)
    private lateinit var mHolder: BindingViewHolder<VB>

    override fun onBindViewHolder(holder: BindingViewHolder<VB>, position: Int) {
        mHolder = holder
        load()
    }

    override fun onItemRangeInserted() {
        // 避免刷新后重新添加 Footer 时无法触发加载更多
        isRunning.compareAndSet(true, false)
    }

    private fun load() {
        if (isRunning.compareAndSet(false, true)) {
            Log.v(TAG, "触发加载更多")
            onLoad()
        }
    }

    /**
     * 请求数据完成时调用此方法。子类可以重写此方法进行界面更新。
     */
    open fun onComplete() {
        mHolder.binding.root.setOnClickListener(null)
        isRunning.compareAndSet(true, false)
    }

    /**
     * 没有更多数据时调用此方法。子类可以重写此方法进行界面更新。
     */
    open fun onEnd() {
        mHolder.binding.root.setOnClickListener(null)
    }

    /**
     * 请求数据出错时调用此方法。子类可以重写此方法进行界面更新。
     * 此方法中添加了出错重试点击监听。
     */
    open fun onError(throwable: Throwable) {
        mHolder.binding.root.setOnClickListener {
            onComplete()
            load()
        }
    }
}
