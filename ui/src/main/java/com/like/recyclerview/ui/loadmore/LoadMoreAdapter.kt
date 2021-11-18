package com.like.recyclerview.ui.loadmore

import com.like.recyclerview.adapter.BaseLoadMoreAdapter
import com.like.recyclerview.ui.databinding.ItemLoadMoreBinding

class LoadMoreAdapter : BaseLoadMoreAdapter<ItemLoadMoreBinding, LoadMoreItem>() {

    override fun onLoading() {
        super.onLoading()
        get(0)?.onLoading()
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
