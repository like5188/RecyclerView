package com.like.recyclerview.ui.error

import com.like.recyclerview.adapter.BaseErrorAdapter
import com.like.recyclerview.ui.databinding.ItemErrorBinding

class ErrorAdapter : BaseErrorAdapter<ItemErrorBinding, ErrorItem>() {

    override fun onError(throwable: Throwable) {
        super.onError(throwable)
        get(0)?.onError(throwable)
    }

}
