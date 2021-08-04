package com.like.recyclerview.listener

import androidx.databinding.ViewDataBinding
import com.like.recyclerview.viewholder.BindingViewHolder

fun interface OnItemClickListener<VB : ViewDataBinding> {
    fun onItemClick(holder: BindingViewHolder<VB>)
}