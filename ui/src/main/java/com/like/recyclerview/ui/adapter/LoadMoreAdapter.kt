package com.like.recyclerview.ui.adapter

import android.util.Log
import com.like.recyclerview.adapter.AbstractLoadMoreAdapter
import com.like.recyclerview.ui.BR
import com.like.recyclerview.ui.R
import com.like.recyclerview.ui.databinding.ItemLoadMoreBinding
import com.like.recyclerview.ui.model.LoadMoreItem
import com.like.recyclerview.viewholder.BindingViewHolder

class LoadMoreAdapter(onLoad: () -> Unit) : AbstractLoadMoreAdapter<ItemLoadMoreBinding, LoadMoreItem>(onLoad) {

    override fun onBindViewHolder(holder: BindingViewHolder<ItemLoadMoreBinding>, position: Int) {
        Log.i(
            "LoadMoreAdapter",
            "onBindViewHolder position=$position bindingAdapterPosition=${holder.bindingAdapterPosition} absoluteAdapterPosition=${holder.absoluteAdapterPosition}"
        )
        super.onBindViewHolder(holder, position)
        holder.binding.setVariable(BR.loadMoreItem, get(0))
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_load_more
    }

    override fun onComplete() {
        super.onComplete()
        get(0)?.onComplete()
    }

    override fun onEnd() {
        super.onEnd()
        get(0)?.onEnd()
    }

    override fun onError(throwable: Throwable) {
        super.onError(throwable)
        get(0)?.onError(throwable)
    }

}
