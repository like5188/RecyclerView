package com.like.recyclerview.sample.concat.vo

import com.like.recyclerview.model.IRecyclerViewItem
import com.like.recyclerview.sample.BR
import com.like.recyclerview.sample.R

data class Header1(val name: String) : IRecyclerViewItem {
    override val layoutId: Int = R.layout.header1
    override val variableId: Int = BR.header1
}