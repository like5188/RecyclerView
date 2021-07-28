package com.like.recyclerview.adapter

import android.util.Log
import androidx.databinding.ViewDataBinding
import com.like.recyclerview.viewholder.BindingViewHolder
import java.util.concurrent.atomic.AtomicBoolean

abstract class AbstractLoadAfterAdapter<T : ViewDataBinding> : AbstractItemAdapter<T>() {
    companion object {
        private const val TAG = "LoadAfterAdapter"
    }

    private var isRunning = AtomicBoolean(false)
    var onLoad: (() -> Unit)? = null
    var onStart: (() -> Unit)? = null
    var onComplete: (() -> Unit)? = null
    var onError: ((Throwable) -> Unit)? = null

    override fun onBindViewHolder(holder: BindingViewHolder<T>, position: Int) {
        if (isRunning.compareAndSet(false, true)) {
            onStart?.invoke()
            Log.v(TAG, "触发往后加载更多")
            try {
                onLoad?.invoke()
                isRunning.set(false)
                onComplete?.invoke()
            } catch (e: Exception) {
                isRunning.set(false)
                onError?.invoke(e)
            }
        }
    }

}
