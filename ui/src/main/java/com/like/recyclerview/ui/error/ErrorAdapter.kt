package com.like.recyclerview.ui.error

import com.like.recyclerview.adapter.BaseErrorAdapter
import com.like.recyclerview.ui.databinding.ItemErrorBinding

class ErrorAdapter : BaseErrorAdapter<ItemErrorBinding, ErrorItem>() {

    override fun error(throwable: Throwable) {
        super.error(throwable)
        get(0)?.error(throwable)
    }

}
