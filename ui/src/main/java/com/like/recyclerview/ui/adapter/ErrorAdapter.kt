package com.like.recyclerview.ui.adapter

import com.like.recyclerview.adapter.AbstractErrorAdapter
import com.like.recyclerview.ui.databinding.ItemErrorBinding
import com.like.recyclerview.ui.model.ErrorItem

class ErrorAdapter : AbstractErrorAdapter<ItemErrorBinding, ErrorItem>() {

    override fun onError(throwable: Throwable) {
        get(0)?.onError(throwable)
    }

}
