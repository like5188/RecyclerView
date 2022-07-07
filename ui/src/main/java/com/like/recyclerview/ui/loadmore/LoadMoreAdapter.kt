package com.like.recyclerview.ui.loadmore

import com.like.recyclerview.adapter.BaseLoadMoreAdapter
import com.like.recyclerview.ui.databinding.ItemLoadMoreBinding

class LoadMoreAdapter(isAfter: Boolean) : BaseLoadMoreAdapter<ItemLoadMoreBinding, LoadMoreItem>(isAfter) {

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
