package com.like.recyclerview.listener

import androidx.databinding.ViewDataBinding
import com.like.recyclerview.viewholder.BindingViewHolder

fun interface OnItemLongClickListener<T : ViewDataBinding> {
    fun onItemLongClick(holder: BindingViewHolder<T>)
}