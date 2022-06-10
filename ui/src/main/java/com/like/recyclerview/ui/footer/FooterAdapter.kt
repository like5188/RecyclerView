package com.like.recyclerview.ui.footer

import com.like.recyclerview.adapter.BaseLoadStateAdapter
import com.like.recyclerview.ui.R
import com.like.recyclerview.ui.databinding.ItemLoadMoreBinding
import com.like.recyclerview.ui.loadmore.LoadMoreItem
import com.like.recyclerview.viewholder.BindingViewHolder

class FooterAdapter : BaseLoadStateAdapter<ItemLoadMoreBinding>(R.layout.item_load_more) {
    private val loadMoreItem: LoadMoreItem = LoadMoreItem()

    override fun onBindViewHolder(holder: BindingViewHolder<ItemLoadMoreBinding>) {
        holder.binding.loadMoreItem = loadMoreItem
    }

    override fun onLoading(holder: BindingViewHolder<ItemLoadMoreBinding>) {
        loadMoreItem.loading()
    }

    override fun onEnd(holder: BindingViewHolder<ItemLoadMoreBinding>) {
        loadMoreItem.end()
    }

    override fun onError(holder: BindingViewHolder<ItemLoadMoreBinding>, throwable: Throwable) {
        loadMoreItem.error(throwable)
    }

}