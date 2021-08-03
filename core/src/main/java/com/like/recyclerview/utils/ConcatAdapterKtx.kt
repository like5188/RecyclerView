package com.like.recyclerview.utils

import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView

fun ConcatAdapter.clear() {
    adapters.forEach {
        this.removeAdapter(it)
    }
}

fun ConcatAdapter.contains(adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>): Boolean {
    adapters.forEach {
        if (it == adapter) {
            return true
        }
    }
    return false
}

/**
 * 移除所有指定的 adapter
 */
fun ConcatAdapter.removeAll(vararg adapters: RecyclerView.Adapter<out RecyclerView.ViewHolder>) {
    if (adapters.isEmpty()) return
    this.adapters.forEach {
        if (adapters.contains(it)) {
            this.removeAdapter(it)
        }
    }
}

/**
 * 移除所有，除了指定的 adapter
 */
fun ConcatAdapter.removeAllExclude(vararg adapters: RecyclerView.Adapter<out RecyclerView.ViewHolder>) {
    if (adapters.isEmpty()) {
        clear()
    } else {
        this.adapters.forEach {
            if (!adapters.contains(it)) {
                this.removeAdapter(it)
            }
        }
    }
}

/**
 * 移除所有，除了指定的 adapter，如果指定的 adapter 不存在，则按顺序添加。
 */
fun ConcatAdapter.removeAllExcludeAndAdd(vararg adapters: RecyclerView.Adapter<out RecyclerView.ViewHolder>) {
    removeAllExclude(*adapters)
    adapters.forEachIndexed { index, adapter ->
        if (!contains(adapter)) {
            addAdapter(index, adapter)
        }
    }
}
