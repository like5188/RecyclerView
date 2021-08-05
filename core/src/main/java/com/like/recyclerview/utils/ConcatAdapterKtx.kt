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

fun ConcatAdapter.add(adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>?) {
    adapter ?: return
    addAdapter(adapter)
}

fun ConcatAdapter.addAll(vararg adapters: RecyclerView.Adapter<out RecyclerView.ViewHolder>?) {
    adapters.forEach {
        add(it)
    }
}

fun ConcatAdapter.remove(adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>?) {
    adapter ?: return
    removeAdapter(adapter)
}

fun ConcatAdapter.removeAll(vararg adapters: RecyclerView.Adapter<out RecyclerView.ViewHolder>?) {
    adapters.forEach {
        remove(it)
    }
}
