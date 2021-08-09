package com.like.recyclerview.utils

import androidx.databinding.ViewDataBinding
import com.like.recyclerview.listener.OnItemClickListener
import com.like.recyclerview.listener.OnItemLongClickListener
import com.like.recyclerview.viewholder.BindingViewHolder

open class ListenerManager<VB : ViewDataBinding> : IListenerManager<VB> {
    private val mOnItemClickListeners = mutableListOf<OnItemClickListener<VB>>()
    private val mOnItemLongClickListeners = mutableListOf<OnItemLongClickListener<VB>>()

    override fun onItemClick(holder: BindingViewHolder<VB>) {
        mOnItemClickListeners.forEach {
            it.onItemClick(holder)
        }
    }

    override fun onItemLongClick(holder: BindingViewHolder<VB>) {
        mOnItemLongClickListeners.forEach {
            it.onItemLongClick(holder)
        }
    }

    override fun addOnItemClickListener(listener: OnItemClickListener<VB>) {
        mOnItemClickListeners.add(listener)
    }

    override fun addOnItemLongClickListener(listener: OnItemLongClickListener<VB>) {
        mOnItemLongClickListeners.add(listener)
    }

    override fun removeOnItemClickListener(listener: OnItemClickListener<VB>) {
        mOnItemClickListeners.remove(listener)
    }

    override fun removeOnItemLongClickListener(listener: OnItemLongClickListener<VB>) {
        mOnItemLongClickListeners.remove(listener)
    }

    override fun clearOnItemClickListeners() {
        mOnItemClickListeners.clear()
    }

    override fun clearOnItemLongClickListeners() {
        mOnItemLongClickListeners.clear()
    }
}