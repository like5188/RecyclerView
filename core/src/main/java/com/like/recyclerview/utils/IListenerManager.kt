package com.like.recyclerview.utils

import androidx.databinding.ViewDataBinding
import com.like.recyclerview.listener.OnItemClickListener
import com.like.recyclerview.listener.OnItemLongClickListener
import com.like.recyclerview.viewholder.BindingViewHolder

interface IListenerManager<VB : ViewDataBinding> {

    fun onItemClick(holder: BindingViewHolder<VB>)

    fun onItemLongClick(holder: BindingViewHolder<VB>)

    fun addOnItemClickListener(listener: OnItemClickListener<VB>)

    fun addOnItemLongClickListener(listener: OnItemLongClickListener<VB>)

    fun removeOnItemClickListener(listener: OnItemClickListener<VB>)

    fun removeOnItemLongClickListener(listener: OnItemLongClickListener<VB>)

    fun clearOnItemClickListeners()

    fun clearOnItemLongClickListeners()

}

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
        if (!mOnItemClickListeners.contains(listener)) {
            mOnItemClickListeners.add(listener)
        }
    }

    override fun addOnItemLongClickListener(listener: OnItemLongClickListener<VB>) {
        if (!mOnItemLongClickListeners.contains(listener)) {
            mOnItemLongClickListeners.add(listener)
        }
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
