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
