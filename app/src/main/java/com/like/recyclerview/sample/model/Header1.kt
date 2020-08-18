package com.like.recyclerview.sample.model

import com.like.recyclerview.model.IHeader
import com.like.recyclerview.sample.BR
import com.like.recyclerview.sample.R

data class Header1(val id: Int, val name: String) : IHeader {
    override val layoutId: Int = R.layout.header1
    override fun variableId(): Int = BR.header1

    override fun sortTag(): Int {
        return 2
    }
}