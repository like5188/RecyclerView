package com.like.recyclerview.sample.concat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.like.recyclerview.model.IRecyclerViewItem
import com.like.recyclerview.sample.concat.vo.Item1
import com.like.recyclerview.sample.concat.vo.Item2
import com.like.recyclerview.viewholder.BindingViewHolder

class ItemListAdapter : ListAdapter<IRecyclerViewItem, BindingViewHolder<ViewDataBinding>>(
    object : DiffUtil.ItemCallback<IRecyclerViewItem>() {
        override fun areItemsTheSame(oldItem: IRecyclerViewItem, newItem: IRecyclerViewItem): Boolean {
            return if (oldItem is Item1 && newItem is Item1) {
                oldItem.id == newItem.id
            } else if (oldItem is Item2 && newItem is Item2) {
                oldItem.id == newItem.id
            } else {
                false
            }
        }

        override fun areContentsTheSame(oldItem: IRecyclerViewItem, newItem: IRecyclerViewItem): Boolean {
            return if (oldItem is Item1 && newItem is Item1) {
                oldItem == newItem
            } else if (oldItem is Item2 && newItem is Item2) {
                oldItem == newItem
            } else {
                false
            }
        }
    }
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<ViewDataBinding> {
        return BindingViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), viewType, parent, false))
    }

    override fun onBindViewHolder(holder: BindingViewHolder<ViewDataBinding>, position: Int) {
        val item = getItem(holder.bindingAdapterPosition) ?: return
        val variableId = item.variableId
        if (variableId >= 0) {
            try {
                holder.binding.setVariable(variableId, item)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position) ?: return -1
        return item.layoutId
    }
}
