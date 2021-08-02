package com.like.recyclerview.ui.adapter

import android.util.Log
import com.like.recyclerview.adapter.AbstractAdapter
import com.like.recyclerview.ui.BR
import com.like.recyclerview.ui.R
import com.like.recyclerview.ui.databinding.ItemEmptyBinding
import com.like.recyclerview.ui.model.EmptyItem
import com.like.recyclerview.viewholder.BindingViewHolder

class EmptyAdapter : AbstractAdapter<ItemEmptyBinding, EmptyItem>() {

    override fun onBindViewHolder(holder: BindingViewHolder<ItemEmptyBinding>, position: Int) {
        Log.i("EmptyAdapter",
            "onBindViewHolder position=$position bindingAdapterPosition=${holder.bindingAdapterPosition} absoluteAdapterPosition=${holder.absoluteAdapterPosition}")
        holder.binding.setVariable(BR.emptyItem, get(position))
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_empty
    }

}
