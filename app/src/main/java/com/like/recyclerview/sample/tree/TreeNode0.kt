package com.like.recyclerview.sample.tree

import androidx.annotation.DrawableRes
import com.like.recyclerview.ext.model.BaseTreeNode
import com.like.recyclerview.ext.model.IPinnedItem
import com.like.recyclerview.sample.BR
import com.like.recyclerview.sample.R

class TreeNode0(@DrawableRes val icon: Int, val name: String, var count: Int) : BaseTreeNode(),
    IPinnedItem {
    override var layoutId: Int = R.layout.tree_item0
    override fun variableId(): Int = BR.treeNode0
}