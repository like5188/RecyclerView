package com.like.recyclerview.ui.loadstate

import com.like.recyclerview.adapter.BaseLoadStateAdapter
import com.like.recyclerview.ui.R
import com.like.recyclerview.ui.databinding.ItemLoadStateBinding
import com.like.recyclerview.viewholder.BindingViewHolder

class LoadStateAdapter : BaseLoadStateAdapter<ItemLoadStateBinding>(R.layout.item_load_state) {
    private val loadStateItem = LoadStateItem()

    override fun onBindViewHolder(holder: BindingViewHolder<ItemLoadStateBinding>) {
        holder.binding.loadStateItem = loadStateItem
    }

    override fun onLoading(holder: BindingViewHolder<ItemLoadStateBinding>) {
        loadStateItem.loading()
    }

    override fun onNoMore(holder: BindingViewHolder<ItemLoadStateBinding>) {
        loadStateItem.noMore()
    }

    override fun onError(holder: BindingViewHolder<ItemLoadStateBinding>, throwable: Throwable) {
        loadStateItem.error(throwable)
    }

}