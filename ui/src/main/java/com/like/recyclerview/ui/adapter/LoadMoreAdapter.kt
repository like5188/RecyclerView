package com.like.recyclerview.ui.adapter

import android.util.Log
import com.like.recyclerview.adapter.AbstractLoadMoreAdapter
import com.like.recyclerview.ui.BR
import com.like.recyclerview.ui.R
import com.like.recyclerview.ui.databinding.ItemLoadMoreBinding
import com.like.recyclerview.ui.model.LoadMoreItem
import com.like.recyclerview.viewholder.BindingViewHolder

class LoadMoreAdapter(onLoad: () -> Unit) : AbstractLoadMoreAdapter<ItemLoadMoreBinding, LoadMoreItem>(onLoad) {
    private lateinit var mLoadMoreItem: LoadMoreItem

    override fun onBindViewHolder(holder: BindingViewHolder<ItemLoadMoreBinding>, position: Int) {
        Log.i("LoadMoreAdapter",
            "onBindViewHolder position=$position bindingAdapterPosition=${holder.bindingAdapterPosition} absoluteAdapterPosition=${holder.absoluteAdapterPosition}")
        mLoadMoreItem = mList[position]
        super.onBindViewHolder(holder, position)
        holder.binding.setVariable(BR.loadMoreItem, mLoadMoreItem)
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_load_more
    }

    override fun onComplete() {
        super.onComplete()
        mLoadMoreItem.onComplete()
    }

    override fun onEnd() {
        super.onEnd()
        mLoadMoreItem.onEnd()
    }

    override fun onError(throwable: Throwable) {
        super.onError(throwable)
        mLoadMoreItem.onError(throwable)
    }

}
