package com.like.recyclerview.sample.model

import com.like.recyclerview.model.IRecyclerViewItem
import com.like.recyclerview.sample.BR
import com.like.recyclerview.sample.R

data class Header2(val name: String) : IRecyclerViewItem {
    override val layoutId: Int = R.layout.header2
    override val variableId: Int = BR.header2
}