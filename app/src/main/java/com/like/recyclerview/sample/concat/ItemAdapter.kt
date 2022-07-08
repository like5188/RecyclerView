package com.like.recyclerview.sample.concat

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.like.recyclerview.adapter.BaseListAdapter
import com.like.recyclerview.model.IRecyclerViewItem
import com.like.recyclerview.sample.concat.vo.Header1
import com.like.recyclerview.sample.concat.vo.Header2
import com.like.recyclerview.sample.concat.vo.Item1
import com.like.recyclerview.sample.concat.vo.Item2

class ItemAdapter : BaseListAdapter<ViewDataBinding, IRecyclerViewItem>(
    object : DiffUtil.ItemCallback<IRecyclerViewItem>() {
        override fun areItemsTheSame(oldItem: IRecyclerViewItem, newItem: IRecyclerViewItem): Boolean {
            return if (oldItem is Item1 && newItem is Item1) {
                oldItem.id == newItem.id
            } else if (oldItem is Item2 && newItem is Item2) {
                oldItem.id == newItem.id
            } else if (oldItem is Header1 && newItem is Header1) {
                oldItem.name == newItem.name
            } else if (oldItem is Header2 && newItem is Header2) {
                oldItem.name == newItem.name
            } else {
                false
            }
        }

        override fun areContentsTheSame(oldItem: IRecyclerViewItem, newItem: IRecyclerViewItem): Boolean {
            return if (oldItem is Item1 && newItem is Item1) {
                oldItem == newItem
            } else if (oldItem is Item2 && newItem is Item2) {
                oldItem == newItem
            } else if (oldItem is Header1 && newItem is Header1) {
                oldItem == newItem
            } else if (oldItem is Header2 && newItem is Header2) {
                oldItem == newItem
            } else {
                false
            }
        }
    }
)
