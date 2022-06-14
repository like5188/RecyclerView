package com.like.recyclerview.ui.loadstate

import androidx.paging.LoadState
import com.like.recyclerview.adapter.BaseLoadStateAdapter
import com.like.recyclerview.ui.R
import com.like.recyclerview.ui.databinding.ItemLoadMoreBinding
import com.like.recyclerview.ui.loadmore.LoadMoreItem
import com.like.recyclerview.viewholder.BindingViewHolder

class LoadStateAdapter : BaseLoadStateAdapter<ItemLoadMoreBinding>(R.layout.item_load_more) {
    private val loadMoreItem = LoadMoreItem()

    override fun onBindViewHolder(holder: BindingViewHolder<ItemLoadMoreBinding>) {
        holder.binding.loadMoreItem = loadMoreItem
    }

    override fun onLoadStateChange(preState: LoadState?, curState: LoadState?, holder: BindingViewHolder<ItemLoadMoreBinding>) {
        when (curState) {
            is LoadState.Loading -> {
                loadMoreItem.loading()
            }
            is LoadState.Error -> {
                loadMoreItem.error(curState.error)
            }
            is LoadState.NotLoading -> {
                if (curState.endOfPaginationReached) {
                    loadMoreItem.end()
                } else {// 空闲状态时，根据 preState 来显示，也就是保持前一个状态不变。
                    when (preState) {
                        is LoadState.Loading -> {
                            loadMoreItem.loading()
                        }
                        is LoadState.Error -> {
                            loadMoreItem.error(preState.error)
                        }
                        is LoadState.NotLoading -> {
                            if (preState.endOfPaginationReached) {
                                loadMoreItem.end()
                            }
                        }
                    }
                }
            }
        }
    }

}