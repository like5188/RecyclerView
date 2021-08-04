package com.like.recyclerview.utils

import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView

fun ConcatAdapter.clear() {
    adapters.forEach {
        this.removeAdapter(it)
    }
}

fun ConcatAdapter.contains(adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>?): Boolean {
    adapter ?: return false
    adapters.forEach {
        if (it == adapter) {
            return true
        }
    }
    return false
}

/**
 * 移除所有指定的 adapter。如果指定的 adapter 为 null，则不做任何操作。
 */
fun ConcatAdapter.removeAll(vararg adapters: RecyclerView.Adapter<out RecyclerView.ViewHolder>?) {
    val nonNullAdapters = adapters.filterNotNull().toTypedArray()
    if (nonNullAdapters.isEmpty()) {
        return
    }
    this.adapters.forEach {
        if (nonNullAdapters.contains(it)) {
            this.removeAdapter(it)
        }
    }
}

/**
 * 移除所有，除了指定的 adapter
 */
fun ConcatAdapter.removeAllExclude(vararg adapters: RecyclerView.Adapter<out RecyclerView.ViewHolder>?) {
    val nonNullAdapters = adapters.filterNotNull().toTypedArray()
    if (nonNullAdapters.isEmpty()) {
        clear()
    } else {
        this.adapters.forEach {
            if (!nonNullAdapters.contains(it)) {
                this.removeAdapter(it)
            }
        }
    }
}

/**
 * 如果指定的 adapter 为 null，则不做任何操作。
 * 如果指定的 adapter 不存在于 ConcatAdapter 中，则按顺序添加到其中。
 */
fun ConcatAdapter.addAllIfAbsent(vararg adapters: RecyclerView.Adapter<out RecyclerView.ViewHolder>?) {
    val nonNullAdapters = adapters.filterNotNull().toTypedArray()
    if (nonNullAdapters.isEmpty()) {
        return
    }
    nonNullAdapters.forEachIndexed { index, adapter ->
        if (!contains(adapter)) {
            addAdapter(index, adapter)
        }
    }
}

fun ConcatAdapter.removeAllExcludeAndAddAllIfAbsent(vararg adapters: RecyclerView.Adapter<out RecyclerView.ViewHolder>?) {
    removeAllExclude(*adapters)
    addAllIfAbsent(*adapters)
}
