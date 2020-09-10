package com.like.recyclerview.listener

import com.like.recyclerview.model.IRecyclerViewItem
import com.like.recyclerview.viewholder.CommonViewHolder

fun interface OnItemLongClickListener {
    fun onItemLongClick(holder: CommonViewHolder, position: Int, data: IRecyclerViewItem?)
}