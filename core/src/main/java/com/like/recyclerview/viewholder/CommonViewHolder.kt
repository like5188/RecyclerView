package com.like.recyclerview.viewholder

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

/**
 * 通用的结合dataBinding的RecyclerView的ViewHolder。实际上只是持有ViewDataBinding的引用
 */
open class CommonViewHolder(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root)