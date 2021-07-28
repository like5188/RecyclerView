package com.like.recyclerview.sample.concat

import android.util.Log
import com.like.recyclerview.adapter.AbstractItemAdapter
import com.like.recyclerview.sample.BR
import com.like.recyclerview.sample.R
import com.like.recyclerview.sample.databinding.ItemBinding
import com.like.recyclerview.sample.model.Item
import com.like.recyclerview.viewholder.BindingViewHolder

class ContentAdapter(private val list: List<Item>) : AbstractItemAdapter<ItemBinding>() {

    override fun onBindViewHolder(holder: BindingViewHolder<ItemBinding>, position: Int) {
        Log.v("ContentAdapter", "position=$position bindingAdapterPosition=${holder.bindingAdapterPosition} absoluteAdapterPosition=${holder.absoluteAdapterPosition}")
        holder.binding.setVariable(BR.item, list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item
    }

}
