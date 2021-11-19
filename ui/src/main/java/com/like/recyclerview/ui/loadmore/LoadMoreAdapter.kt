package com.like.recyclerview.ui.loadmore

import com.like.recyclerview.adapter.BaseLoadMoreAdapter
import com.like.recyclerview.ui.databinding.ItemLoadMoreBinding

class LoadMoreAdapter : BaseLoadMoreAdapter<ItemLoadMoreBinding, LoadMoreItem>() {

    override fun loading() {
        super.loading()
        get(0)?.loading()
    }

    override fun end() {
        super.end()
        get(0)?.end()
    }

    override fun error(throwable: Throwable) {
        super.error(throwable)
        get(0)?.error(throwable)
    }

}
