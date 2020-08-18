package com.like.recyclerview.sample.model

import com.like.recyclerview.model.IHeader
import com.like.recyclerview.sample.BR
import com.like.recyclerview.sample.R

data class Header2(val id: Int, val name: String) : IHeader {
    override val layoutId: Int = R.layout.header2
    override fun variableId(): Int = BR.header2

    override fun sortTag(): Int {
        return 1
    }
}