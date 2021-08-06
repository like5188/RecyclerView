package com.like.recyclerview.ui.error

import com.like.recyclerview.adapter.AbstractErrorAdapter
import com.like.recyclerview.ui.databinding.ItemErrorBinding

class ErrorAdapter : AbstractErrorAdapter<ItemErrorBinding, ErrorItem>() {

    override fun onError(throwable: Throwable) {
        get(0)?.onError(throwable)
    }

}
