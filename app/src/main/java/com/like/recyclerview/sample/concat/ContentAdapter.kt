package com.like.recyclerview.sample.concat

import com.like.recyclerview.adapter.AbstractItemAdapter
import com.like.recyclerview.sample.BR
import com.like.recyclerview.sample.R
import com.like.recyclerview.sample.databinding.ItemBinding
import com.like.recyclerview.sample.model.Item
import com.like.recyclerview.viewholder.BindingViewHolder

class ContentAdapter : AbstractItemAdapter<ItemBinding>() {

    override fun onBindViewHolder(holder: BindingViewHolder<ItemBinding>, position: Int) {
        holder.binding.setVariable(BR.item, Item(position, "content $position", "des $position"))
    }

    override fun getItemCount(): Int {
        return 5
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item
    }

}
