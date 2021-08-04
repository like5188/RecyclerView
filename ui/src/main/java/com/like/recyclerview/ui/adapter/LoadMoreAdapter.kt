package com.like.recyclerview.ui.adapter

import com.like.recyclerview.adapter.AbstractLoadMoreAdapter
import com.like.recyclerview.ui.databinding.ItemLoadMoreBinding
import com.like.recyclerview.ui.model.LoadMoreItem

class LoadMoreAdapter(onLoad: () -> Unit) : AbstractLoadMoreAdapter<ItemLoadMoreBinding, LoadMoreItem>(onLoad) {

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
