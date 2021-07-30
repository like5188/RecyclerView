package com.like.recyclerview.adapter

import android.util.Log
import androidx.databinding.ViewDataBinding
import com.like.recyclerview.viewholder.BindingViewHolder
import java.util.concurrent.atomic.AtomicBoolean

abstract class AbstractFooterAdapter<VB : ViewDataBinding, Data>(private val onLoad: () -> Unit) : AbstractAdapter<VB, Data>() {
    companion object {
        private const val TAG = "AbstractFooterAdapter"
    }

    private var isRunning = AtomicBoolean(false)

    override fun onBindViewHolder(holder: BindingViewHolder<VB>, position: Int) {
        if (isRunning.compareAndSet(false, true)) {
            Log.v(TAG, "触发往后加载更多")
            onLoad()
        }
    }

    fun trigger() {
        isRunning.compareAndSet(true, false)
    }

}
