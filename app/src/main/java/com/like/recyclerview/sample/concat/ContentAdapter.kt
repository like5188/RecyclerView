package com.like.recyclerview.sample.concat

import android.util.Log
import com.like.recyclerview.adapter.AbstractAdapter
import com.like.recyclerview.sample.BR
import com.like.recyclerview.sample.R
import com.like.recyclerview.sample.databinding.Item1Binding
import com.like.recyclerview.sample.model.Item
import com.like.recyclerview.viewholder.BindingViewHolder

class ContentAdapter : AbstractAdapter<Item1Binding, Item>() {

    override fun onBindViewHolder(holder: BindingViewHolder<Item1Binding>, position: Int) {
        Log.v(
            "ContentAdapter",
            "onBindViewHolder position=$position bindingAdapterPosition=${holder.bindingAdapterPosition} absoluteAdapterPosition=${holder.absoluteAdapterPosition}"
        )
        holder.binding.setVariable(BR.item, mList[position])
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item1
    }

}
