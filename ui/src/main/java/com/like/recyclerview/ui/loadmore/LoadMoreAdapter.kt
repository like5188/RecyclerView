package com.like.recyclerview.ui.loadmore

import com.like.recyclerview.adapter.BaseLoadMoreAdapter
import com.like.recyclerview.ui.databinding.ItemLoadMoreBinding

class LoadMoreAdapter : BaseLoadMoreAdapter<ItemLoadMoreBinding, LoadMoreItem>() {

    override fun onLoading() {
        get(0)?.loading()
    }

    override fun onEnd() {
        get(0)?.end()
    }

    override fun onError(throwable: Throwable) {
        get(0)?.error(throwable)
    }

}
