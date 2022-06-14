package com.like.recyclerview.ui.loadstate

import androidx.paging.LoadState
import com.like.recyclerview.adapter.BaseLoadStateAdapter
import com.like.recyclerview.ui.R
import com.like.recyclerview.ui.databinding.ItemLoadStateBinding
import com.like.recyclerview.viewholder.BindingViewHolder

class LoadStateAdapter : BaseLoadStateAdapter<ItemLoadStateBinding>(R.layout.item_load_state) {
    private val loadStateItem = LoadStateItem()

    override fun onBindViewHolder(holder: BindingViewHolder<ItemLoadStateBinding>) {
        holder.binding.loadStateItem = loadStateItem
    }

    override fun onLoadStateChange(preState: LoadState?, curState: LoadState?, holder: BindingViewHolder<ItemLoadStateBinding>) {
        when (curState) {
            is LoadState.Loading -> {
                loadStateItem.loading()
            }
            is LoadState.Error -> {
                loadStateItem.error(curState.error)
            }
            is LoadState.NotLoading -> {
                if (curState.endOfPaginationReached) {
                    loadStateItem.noMore()
                } else {// 空闲状态时，根据 preState 来显示，也就是保持前一个状态不变。
                    when (preState) {
                        is LoadState.Loading -> {
                            loadStateItem.loading()
                        }
                        is LoadState.Error -> {
                            loadStateItem.error(preState.error)
                        }
                        is LoadState.NotLoading -> {
                            if (preState.endOfPaginationReached) {
                                loadStateItem.noMore()
                            }
                        }
                    }
                }
            }
        }
    }

}