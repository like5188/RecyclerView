package com.like.recyclerview.ui.util

import com.like.recyclerview.ui.loadmore.LoadMoreAdapter
import com.like.recyclerview.ui.loadmore.LoadMoreItem

object AdapterFactory {

    fun createLoadMoreAdapter(isAfter: Boolean = true) = LoadMoreAdapter(isAfter).apply {
        addToEnd(LoadMoreItem())
    }

}
