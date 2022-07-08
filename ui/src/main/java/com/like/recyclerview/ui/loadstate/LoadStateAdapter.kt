package com.like.recyclerview.ui.loadstate

import com.like.recyclerview.adapter.BaseLoadStateAdapter
import com.like.recyclerview.ui.R
import com.like.recyclerview.ui.databinding.ItemLoadStateBinding
import com.like.recyclerview.viewholder.BindingViewHolder

class LoadStateAdapter(private val loadStateItem: LoadStateItem) : BaseLoadStateAdapter<ItemLoadStateBinding>() {

    override fun onLoading() {
        loadStateItem.loading()
    }

    override fun onEnd() {
        loadStateItem.end()
    }

    override fun onError(throwable: Throwable) {
        loadStateItem.error(throwable)
    }

    override fun getLayoutId(): Int {
        return R.layout.item_load_state
    }

    override fun onBindViewHolder(holder: BindingViewHolder<ItemLoadStateBinding>) {
        holder.binding.loadMoreItem = loadStateItem
    }

}
