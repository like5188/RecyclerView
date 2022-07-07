package com.like.recyclerview.ui.loadstate

import com.like.recyclerview.adapter.BaseLoadStateAdapter
import com.like.recyclerview.ui.databinding.ItemLoadStateBinding

class LoadStateAdapter : BaseLoadStateAdapter<ItemLoadStateBinding, LoadStateItem>() {

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
