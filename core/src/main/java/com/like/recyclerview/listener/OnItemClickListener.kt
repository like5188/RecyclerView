package com.like.recyclerview.listener

import com.like.recyclerview.model.IRecyclerViewItem
import com.like.recyclerview.viewholder.CommonViewHolder

interface OnItemClickListener {
    fun onItemClick(holder: CommonViewHolder, position: Int, data: IRecyclerViewItem?)
}