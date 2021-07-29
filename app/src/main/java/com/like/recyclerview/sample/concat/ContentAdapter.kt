package com.like.recyclerview.sample.concat

import android.util.Log
import com.like.recyclerview.adapter.AbstractAdapter
import com.like.recyclerview.sample.BR
import com.like.recyclerview.sample.R
import com.like.recyclerview.sample.databinding.ItemBinding
import com.like.recyclerview.sample.model.Item
import com.like.recyclerview.viewholder.BindingViewHolder

class ContentAdapter : AbstractAdapter<ItemBinding, Item>() {

    override fun onBindViewHolder(holder: BindingViewHolder<ItemBinding>, position: Int) {
        Log.v(
            "ContentAdapter",
            "onBindViewHolder position=$position bindingAdapterPosition=${holder.bindingAdapterPosition} absoluteAdapterPosition=${holder.absoluteAdapterPosition}"
        )
        holder.binding.setVariable(BR.item, mList[position])
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item
    }

}
