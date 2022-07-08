package com.like.recyclerview.ui.util

import com.like.recyclerview.ui.loadstate.LoadStateAdapter
import com.like.recyclerview.ui.loadstate.LoadStateItem

object AdapterFactory {

    fun createLoadMoreAdapter() = LoadStateAdapter(LoadStateItem())

}
