package com.like.recyclerview.ui.loadmore

import com.like.recyclerview.adapter.BaseLoadMoreAdapter
import com.like.recyclerview.ui.databinding.ItemLoadMoreBinding

class LoadMoreAdapter(onLoadMore: suspend () -> Unit) : BaseLoadMoreAdapter<ItemLoadMoreBinding, LoadMoreItem>(onLoadMore) {

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
