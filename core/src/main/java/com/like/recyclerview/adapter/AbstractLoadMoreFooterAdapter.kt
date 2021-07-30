package com.like.recyclerview.adapter

import android.util.Log
import androidx.databinding.ViewDataBinding
import com.like.recyclerview.viewholder.BindingViewHolder
import java.util.concurrent.atomic.AtomicBoolean

abstract class AbstractLoadMoreFooterAdapter<VB : ViewDataBinding, Data>(private val onLoad: () -> Unit) : AbstractAdapter<VB, Data>() {
    companion object {
        private const val TAG = "AbstractFooterAdapter"
    }

    private var isRunning = AtomicBoolean(false)
    private lateinit var mHolder: BindingViewHolder<VB>

    override fun onBindViewHolder(holder: BindingViewHolder<VB>, position: Int) {
        mHolder = holder
        load()
    }

    private fun load() {
        if (isRunning.compareAndSet(false, true)) {
            Log.v(TAG, "触发加载更多")
            onLoad()
        }
    }

    open fun onComplete() {
        mHolder.binding.root.setOnClickListener(null)
        isRunning.compareAndSet(true, false)
    }

    open fun onEnd() {
        mHolder.binding.root.setOnClickListener(null)
    }

    open fun onError(throwable: Throwable) {
        mHolder.binding.root.setOnClickListener {
            onComplete()
            load()
        }
    }
}
