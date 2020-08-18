package com.like.recyclerview.sample.model

import com.like.recyclerview.model.IFooter
import com.like.recyclerview.sample.BR
import com.like.recyclerview.sample.R

data class Footer1(val id: Int, val name: String) : IFooter {
    override val layoutId: Int = R.layout.footer1
    override fun variableId(): Int = BR.footer1

    override fun sortTag(): Int {
        return 2
    }
}