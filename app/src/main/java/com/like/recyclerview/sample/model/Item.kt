package com.like.recyclerview.sample.model

import com.like.recyclerview.model.IItem
import com.like.recyclerview.sample.BR
import com.like.recyclerview.sample.R

data class Item(val id: Int, val name: String, val des: String) : IItem {
    override val layoutId: Int = R.layout.item
    override fun variableId(): Int = BR.item
}